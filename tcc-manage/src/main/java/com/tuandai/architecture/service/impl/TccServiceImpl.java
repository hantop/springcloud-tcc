package com.tuandai.architecture.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.architecture.componet.ThresholdsTimeManage;
import com.tuandai.architecture.componet.ToInstancesIPUrl;
import com.tuandai.architecture.config.RestTemplateHelper;
import com.tuandai.architecture.constant.BZStatusCode;
import com.tuandai.architecture.constant.Constants;
import com.tuandai.architecture.constant.TransState;
import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.domain.TransLogs;
import com.tuandai.architecture.domain.TransUrls;
import com.tuandai.architecture.exception.ServiceException;
import com.tuandai.architecture.repository.TransRepository;
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
	
	//执行CC操作线程池
	private static ExecutorService executorCCServicePool = Executors.newCachedThreadPool();

	@Autowired
	TransRepository transRepository;

	@Autowired
	TransUrlsRepository transUrlsRepository;

	@Autowired
	ThresholdsTimeManage thresholdsTimeManage;

	@Autowired
	ToInstancesIPUrl toInstancesIPUrl;
	
	@Autowired
	TccLogService transLogsService;

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
		JSONObject httpEntity = JSONObject.parseObject(transUrlParam);
		httpEntity.put("transId", transId);

		// 执行请求
		StringBuffer logsb = new StringBuffer();
		ResponseEntity<Object> response = patchTransRest(logsb, transId, transUrl, transUrlParam);
		transLogsService.writeLog(dt, logsb.toString(), transId, trans.getTransState(), transUrl);
		return response;
	}

	ResponseEntity<Object> patchTransRest(StringBuffer logsb, Long transId, String transUrl, String transUrlParam) {
		ResponseEntity<Object> response = null;
		// 请求体
		JSONObject httpEntity = JSONObject.parseObject(transUrlParam);
		httpEntity.put("transId", transId);

		try {
			RestTemplate rt = RestTemplateHelper.getRestTemplate();
			String url = toInstancesIPUrl.getIPUrl(transUrl);
			logger.debug("try request: {},{}", rt.toString(), url);
			response = rt.exchange(url, HttpMethod.POST, new HttpEntity<JSONObject>(httpEntity, Constants.header),
					Object.class);
			logger.debug("transUrl: {} ", response.toString());
			logsb.append("try ::: pending = " + response.toString());
		} catch (Exception e) {
			logsb.append("try ::: exception = " + e.getMessage());
			response = new ResponseEntity<Object>("{}", HttpStatus.METHOD_NOT_ALLOWED);
		}
		return response;
	}

	@Override
	public void confrimTrans(Long transId) {
		ccTrans(transId, TransState.CONFIRM.code());
	}

	@Override
	public void confrimMark(Long transId) {
		Trans trans = transRepository.getByTransId(transId);
		if (null == trans) {
			return;
		}
		if (TransState.PENDING.code() == trans.getTransState()) {
			trans.setTransState(TransState.CONFIRM.code());
			trans.setUpdateTime(new Date());
			transRepository.update(trans);
		}
	}

	@Override
	public void cancelTrans(Long transId) {
		ccTrans(transId, TransState.CANCEL.code());
	}

	@Override
	public void cancelMark(Long transId) {
		Trans trans = transRepository.getByTransId(transId);
		if (null == trans) {
			return;
		}
		// 【注： CANCEL操作必须在Try之后执行,防止因为熔断降级，导致CC在前，Try在后；】
		if (TransState.PENDING.code() == trans.getTransState()) {
			trans.setTransState(TransState.CANCEL.code());
			trans.setUpdateTime(new Date());
			transRepository.update(trans);
		}
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
		ccOver(transId, TransState.CANCEL.code());

	}

	@Override
	public void ccTrans(Long transId, int state) {
		Trans trans = transRepository.getByTransId(transId);
		if (null == trans) {
			return;
		}

		if (TransState.CANCEL.code() != state && TransState.CONFIRM.code() != state) {
			return;
		}

		if (TransState.CANCEL.code() == state) {
			// 【注： CANCEL操作必须在Try之后执行,防止因为熔断降级，导致CANCEL在前，Try在后； 必须是UPDATE TIME
			// 进行比较】
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, 15);
			if (!trans.getUpdateTime().before(calendar.getTime())) {
				return;
			}
		}

		if (TransState.PENDING.code() == trans.getTransState()) {
			trans.setTransState(state);
			trans.setUpdateTime(new Date());
			transRepository.update(trans);
		}

		// 获取列表，执行确认操作
		List<TransUrls> transUrlList = transUrlsRepository.getByTransId(trans.getTransId());

		// 远程调用
		String optionStr = "";
		if (TransState.CANCEL.code() == state) {
			optionStr = "cancel";
			RestCC(trans.getTransId(), transUrlList, HttpMethod.DELETE);
		} else {
			optionStr = "confirm";
			RestCC(trans.getTransId(), transUrlList, HttpMethod.PUT);
		}
		// 结果检查
		Date dt = new Date();
		for (TransUrls tu : transUrlList) {
			if (HttpStatus.OK.value() == tu.getCode()) {
				continue;
			}
			// 提交失败
			String logstr = optionStr + " ::: cc = failed!";
			transLogsService.writeLog(dt, logstr, trans.getTransId(), trans.getTransState(), tu.getTransUrl());

			// 更新阀值
			changeCCThreshold(trans);
			return;
		}

		// 清除已完成事务信息
		ccOver(trans.getTransId(), trans.getTransState());
	}
	
	private void changeCCThreshold(Trans trans) {
		// 更新阀值
		int ccTimes = trans.getCcTimes();
		trans.setCcTimes(ccTimes + 1);
		int ccThreshold = trans.getCcThreshold();
		trans.setCcThreshold(ccThreshold == 0 ? 0 : ccThreshold - 1);
		trans.setCcTime(thresholdsTimeManage.createCCTime(trans.getCcThreshold()));
		transRepository.update(trans);
	}

	private void RestCC(Long transId, List<TransUrls> transUrlList, HttpMethod hMethod) {

		final CountDownLatch latch = new CountDownLatch(transUrlList.size());	
		for (TransUrls tu : transUrlList) {
			executorCCServicePool.execute(() -> {
				CCRestTask(tu, hMethod);
				latch.countDown();
			});
		}

		try {
			// 此处必须用超时机制，不可直接： x.await();
			latch.await(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("=================countDownLatch exception============= {}", e.getMessage());
		}
	}

	private void CCRestTask(TransUrls tu, HttpMethod hMethod) {
		try {
			logger.debug("[CCRestTask] transId: {} , transUrl: {} ", tu.getTransId(), tu.getTransUrl());
			ResponseEntity<String> response = null;
			response = RestTemplateHelper.getRestTemplate().exchange(toInstancesIPUrl.getIPUrl(tu.getTransUrl()),
					hMethod, new HttpEntity<String>(String.valueOf(tu.getTransId()), Constants.header), String.class);
			logger.debug("[CCRestTask] transId: {} , transUrl: {} , response: {} ", tu.getTransId(), tu.getTransUrl(),
					response.toString());
			if (HttpStatus.OK.equals(response.getStatusCode())) {
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

	@Transactional(rollbackFor = Exception.class)
	public void ccOver(Long transId, int state) {
		logger.debug("ccOver > transId: {}, state: {}", transId, state);
		String logstr = "";
		if (TransState.CONFIRM.code() == state) {
			logstr = "confirm ::: confirm = success!";
		} else if (TransState.CANCEL.code() == state) {
			logstr = "cancel ::: cancel = success!";
		} else {
			logstr = "other ::: other = over!";
		}

		transLogsService.writeLog(new Date(), logstr, transId, state, "over");

		transRepository.delete(transId);
		transUrlsRepository.deleteByTransId(transId);
	}

	@Override
	public Trans getTrans(Long transId) {
		return transRepository.getByTransId(transId);
	}

	@Override
	public List<TransUrls> getTransUrl(Long transId) {
		return transUrlsRepository.getByTransId(transId);
	}

	@Override
	public List<TransLogs> getTransLog(Long transId) {
		return transLogsService.getLog(transId);
	}

}
