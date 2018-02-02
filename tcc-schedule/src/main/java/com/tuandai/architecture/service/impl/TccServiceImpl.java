package com.tuandai.architecture.service.impl;

import java.util.ArrayList;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tuandai.architecture.client.TccServiceClient;
import com.tuandai.architecture.component.ThresholdsTimeManage;
import com.tuandai.architecture.constant.MyConstants;
import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.domain.TransUrls;
import com.tuandai.architecture.repository.TransRepository;
import com.tuandai.architecture.repository.TransRetryRepository;
import com.tuandai.architecture.repository.TransUrlsRepository;
import com.tuandai.architecture.service.TccLogService;
import com.tuandai.architecture.service.TccService;
import com.tuandai.transaction.constant.Constants;
import com.tuandai.transaction.constant.TCCState;

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
	public Boolean ccTrans(Trans trans) {

		// 获取列表，执行确认操作
		List<TransUrls> transUrlList = transUrlsRepository.getByTransId(trans.getTransId());

		if (null == transUrlList || 0 == transUrlList.size()) {
			transUrlList = insertTransUrls(trans);
		}

		// 远程调用
		RestCC(trans, transUrlList);

		// 结果检查
		Date dt = new Date();
		for (TransUrls tu : transUrlList) {
			if (HttpStatus.OK.value() == tu.getCode()) {
				continue;
			}
			// 提交失败
			String logstr = "confirm ::: cc = failed!";
			if (TCCState.CANCEL.value() == trans.getTransState()) {
				logstr = "cancel ::: cc = failed!";
			}
			transLogsService.writeLog(dt, logstr, trans.getTransId(), trans.getTransState(), tu.getTransUrl());
			return false;
		}

		// 记录日志
		String logstr = "";
		if (TCCState.COMMIT.value() == trans.getTransState()) {
			logstr = "confirm ::: confirm = success!";
		} else {
			logstr = "cancel ::: cancel = success!";
		}
		transLogsService.writeLog(new Date(), logstr, trans.getTransId(), trans.getTransState(), "over");

		return true;
	}

	private void RestCC(Trans trans, List<TransUrls> transUrlList) {

		final CountDownLatch latch = new CountDownLatch(transUrlList.size());
		for (TransUrls tu : transUrlList) {
			executorCCServicePool.execute(() -> {
				CCRestTask(tu, trans.getTransState());
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
			if (TCCState.CANCEL.value() == state) {
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
	public void decreaseCCThreshold(Trans trans) {
		// 更新阀值
		int ccTimes = trans.getCcTimes();
		trans.setCcTimes(ccTimes + 1);
		int ccThreshold = trans.getCcThreshold();
		trans.setCcThreshold(ccThreshold == 0 ? 0 : ccThreshold - 1);
		trans.setCcTime(thresholdsTimeManage.createCCTime(trans.getCcThreshold()));
		logger.debug("========transId: {} , Decrease CC Threshold: {} , ccTime: {} =========", trans.getTransId(),
				trans.getCcThreshold(), trans.getCcTime());
		transRetryRepository.update(trans);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean ccOver(String transId) {
		logger.debug("ccOver > transId: {}", transId);
		transRepository.delete(transId);
		transRetryRepository.delete(transId);
		transUrlsRepository.deleteByTransId(transId);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean ccOverList(List<String> transIds) {
		transRetryRepository.deleteBatch(transIds);
		transUrlsRepository.deleteBatchByTransId(transIds);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean ccToRetry(String transId, int state) {
		logger.debug("ccToRetry > transId: {}", transId);
		Trans trans = transRepository.getByTransId(transId);
		trans.setTransState(state);
		trans.setCcThreshold(MyConstants.MAX_THRESHOLD);
		trans.setCcTime(thresholdsTimeManage.createCCTime(trans.getCcThreshold()));

		transRetryRepository.insert(trans);
		transRepository.delete(transId);
		return true;
	}

	@Override
	public void confrimMark(String transId) {
		Trans trans = transRepository.getByTransId(transId);
		if (null == trans) {
			return;
		}

		String logstr = "confirm ::: confirm = toRetry!";
		transLogsService.writeLog(new Date(), logstr, trans.getTransId(), trans.getTransState(), "");

		// 数据迁移
		ccToRetry(transId, TCCState.COMMIT.value());
	}

	@Override
	public void cancelMark(String transId) {
		Trans trans = transRepository.getByTransId(transId);
		if (null == trans) {
			return;
		}

		String logstr = "cancel ::: cancel = toRetry!";
		transLogsService.writeLog(new Date(), logstr, trans.getTransId(), trans.getTransState(), "");

		// 数据迁移
		ccToRetry(transId, TCCState.CANCEL.value());
	}

	@Override
	public void forceCancelTrans(String transId) {
		Trans trans = transRepository.getByTransId(transId);
		if (null == trans) {
			return;
		}
		String logstr = "cancel ::: cancel = forceCancel !";
		transLogsService.writeLog(new Date(), logstr, transId, TCCState.CANCEL.value(), "force");
		// 清除已完成事务信息
		ccOver(transId);
	}

	@Override
	public Trans getTrans(String transId) {
		return transRepository.getByTransId(transId);
	}

	@Override
	public List<TransUrls> getTransUrl(String transId) {
		return transUrlsRepository.getByTransId(transId);
	}

	private List<TransUrls> insertTransUrls(Trans trans) {
		if (null == trans.getResUrls()) {
			return null;
		}

		List<TransUrls> ltransUrls = new ArrayList<TransUrls>();
		String[] urls = trans.getResUrls().split(Constants.RES_URL_SPLIT);
		Date dt = new Date();
		for (String url : urls) {
			if(url.trim().length() < 2){
				break;
			}
			TransUrls transUrls = new TransUrls();
			transUrls.setCode(0);
			transUrls.setTransId(trans.getTransId());
			transUrls.setTransUrl(url);
			transUrls.setCreateTime(dt);
			transUrls.setUpdateTime(dt);
			transUrlsRepository.insert(transUrls);
			
			ltransUrls.add(transUrls);
		}

		return ltransUrls;
	}

}
