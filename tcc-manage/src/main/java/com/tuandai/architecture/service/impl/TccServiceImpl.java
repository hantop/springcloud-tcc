package com.tuandai.architecture.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.architecture.client.TccServiceClient;
import com.tuandai.architecture.component.ThresholdsTimeManage;
import com.tuandai.architecture.constant.BZStatusCode;
import com.tuandai.architecture.constant.Constants;
import com.tuandai.architecture.constant.TransState;
import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.domain.TransUrls;
import com.tuandai.architecture.exception.ServiceException;
import com.tuandai.architecture.repository.TransRepository;
import com.tuandai.architecture.repository.TransRetryRepository;
import com.tuandai.architecture.repository.TransUrlsRepository;
import com.tuandai.architecture.service.TccLogService;
import com.tuandai.architecture.service.TccService;

/**
 * 
 * @author Gus
 *
 */
@Service
public class TccServiceImpl implements TccService {

	private static final Logger logger = LoggerFactory.getLogger(TccServiceImpl.class);
	// 执行CC操作线程池
	private static ExecutorService executorCCServicePool = Executors.newCachedThreadPool();

	@Autowired
	ThresholdsTimeManage thresholdsTimeManage;

	@Autowired
	TransRepository transRepository;

	@Autowired
	TransRetryRepository transRetryRepository;

	@Autowired
	TccLogService transLogsService;

	@Autowired
	TransUrlsRepository transUrlsRepository;

	@Autowired
	TccServiceClient tccServiceClient;

	@Override
	public Long createTrans(String serviceName) {

		Trans trans = new Trans();
		Date dt = new Date();
		trans.setServiceName(serviceName);
		trans.setCheckUrl(Constants.CHECK_URL);
		trans.setTransState(TransState.PENDING.code());

		trans.setCheckTimes(0);
		trans.setCheckThreshold(Constants.MAX_THRESHOLD);
		trans.setCheckTime(thresholdsTimeManage.createTccCheckTime(Constants.MAX_THRESHOLD));

		trans.setCcThreshold(Constants.MAX_THRESHOLD);
		trans.setCcTimes(0);
		trans.setCcTime(dt);

		trans.setCreateTime(dt);
		trans.setUpdateTime(dt);

		if (1 != transRepository.insert(trans)) {
			throw new ServiceException(BZStatusCode.SERVER_UNKNOWN_ERROR);
		}

		String logstr = "create ::: " + serviceName;
		transLogsService.writeLog(dt, logstr, trans.getTransId(), 10, serviceName);

		return trans.getTransId();
	}

	@Override
	public ResponseEntity<Object> patchTrans(Long transId, String transUrl, String transUrlParam) {

		Trans trans = transRepository.getByTransId(transId);
		if (null == trans) {
			throw new ServiceException(BZStatusCode.INVALID_MODEL_FIELDS);
		}
		if (TransState.PENDING.code() != trans.getTransState()) {
			throw new ServiceException(BZStatusCode.INVALID_STATE_OPTION);
		}
		// 持久化远程请求
		Date dt = new Date();
		TransUrls transUrls = new TransUrls();
		transUrls.setCreateTime(dt);
		transUrls.setTransId(transId);
		transUrls.setTransUrl(transUrl);
		transUrls.setTransUrlParam(transUrlParam);
		transUrls.setUpdateTime(dt);
		int rd = transUrlsRepository.insert(transUrls);
		// 【注意： 一致性保障关键】
		if (1 != rd) {
			throw new ServiceException(BZStatusCode.DUPLICATE_KEY);
		}

		// 请求体
		JSONObject jsonBody = JSONObject.parseObject(transUrlParam);
		jsonBody.put("transId", transId);

		// 执行请求
		StringBuffer logsb = new StringBuffer();
		try {
			String result = tccServiceClient.tryTrans(transUrl, jsonBody);
			if (null != result) {
				logsb.append("try ::: pending done!");
				transLogsService.writeLog(dt, logsb.toString(), transId, trans.getTransState(), transUrl);
				return new ResponseEntity<Object>(result, HttpStatus.OK);
			}
			logsb.append("try ::: pending error : null");
		} catch (Exception ex) {
			logger.error("patchTransRest Exception: {}", ex.getMessage());
			logsb.append("try ::: pending exception: " + ex.getMessage());
		}
		transLogsService.writeLog(dt, logsb.toString(), transId, trans.getTransState(), transUrl);
		return new ResponseEntity<Object>("{}", HttpStatus.FAILED_DEPENDENCY);

	}

	private void RestCC(int state, List<TransUrls> transUrlList) {

		final CountDownLatch latch = new CountDownLatch(transUrlList.size());
		for (TransUrls tu : transUrlList) {
			executorCCServicePool.execute(() -> {
				CCRestTask(tu, state);
				latch.countDown();
			});
		}

		try {
			// 此处必须用超时机制，不可直接： x.await();
			latch.await(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("=================countDownLatch exception============={}", e.getMessage());
		}
	}

	private void CCRestTask(TransUrls tu, int state) {
		try {

			String result = null;
			if (TransState.CANCEL.code() == state) {
				result = tccServiceClient.cancelTrans(tu.getTransUrl(), String.valueOf(tu.getTransId()));
			} else {
				result = tccServiceClient.confirmTrans(tu.getTransUrl(), String.valueOf(tu.getTransId()));
			}

			if (null != result) {
				tu.setCode(HttpStatus.OK.value());
			} else {
				tu.setCode(HttpStatus.FAILED_DEPENDENCY.value());
			}
		} catch (Exception e) {
			logger.error("[Exception] ccTrans  transId: {} , TransUrl: {}, Exception: {}", tu.getTransId(),
					tu.getTransUrl(), e.getMessage());
			tu.setCode(HttpStatus.FAILED_DEPENDENCY.value());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean ccOver(Long transId) {
		logger.debug("ccOver > transId: {}", transId);
		transRepository.delete(transId);
		transUrlsRepository.deleteByTransId(transId);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean ccToRetry(Long transId, int state) {
		logger.debug("ccToRetry > transId: {}", transId);
		Trans trans = transRepository.getByTransId(transId);
		trans.setTransState(state);
		trans.setCcThreshold(Constants.MAX_THRESHOLD);
		trans.setCcTime(thresholdsTimeManage.createCCTime(trans.getCcThreshold()));

		transRetryRepository.insert(trans);
		transRepository.delete(transId);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean ccOverList(List<Long> transIds) {
		transRepository.deleteBatch(transIds);
		transUrlsRepository.deleteBatchByTransId(transIds);
		return true;
	}

	@Override
	public void confrimTrans(Long transId) {

		Trans trans = transRepository.getByTransId(transId);
		if (null == trans) {
			return;
		}

		// 获取列表，执行确认操作
		List<TransUrls> transUrlList = transUrlsRepository.getByTransId(trans.getTransId());

		// 远程调用
		RestCC(TransState.CONFIRM.code(), transUrlList);

		// 结果检查
		String logstr = "";
		Date dt = new Date();
		for (TransUrls tu : transUrlList) {
			if (HttpStatus.OK.value() == tu.getCode()) {
				continue;
			}
			// 提交失败
			logstr = "confirm ::: confirm = toRetry!";
			transLogsService.writeLog(dt, logstr, trans.getTransId(), trans.getTransState(), tu.getTransUrl());

			// 数据迁移
			ccToRetry(transId, TransState.CONFIRM.code());
			return;
		}

		// 提交成功
		logstr = "confirm ::: confirm = success!";
		transLogsService.writeLog(new Date(), logstr, trans.getTransId(), trans.getTransState(), "over");

		// 清除已完成事务信息
		ccOver(transId);
	}

	@Override
	public void cancelMark(Long transId) {
		Trans trans = transRepository.getByTransId(transId);
		if (null == trans) {
			return;
		}

		String logstr = "cancel ::: cancel = toRetry!";
		transLogsService.writeLog(new Date(), logstr, trans.getTransId(), trans.getTransState(), "");

		// 数据迁移
		ccToRetry(transId, TransState.CANCEL.code());
	}

	@Override
	public void forceCancelTrans(Long transId) {
		Trans trans = transRepository.getByTransId(transId);
		if (null == trans) {
			return;
		}
		String logstr = "cancel ::: cancel = forceCancel !";
		transLogsService.writeLog(new Date(), logstr, transId, TransState.CANCEL.code(), "force");
		// 清除已完成事务信息
		ccOver(transId);
	}

	@Override
	public Trans getTrans(Long transId) {
		return transRepository.getByTransId(transId);
	}

	@Override
	public List<TransUrls> getTransUrl(Long transId) {
		return transUrlsRepository.getByTransId(transId);
	}

}
