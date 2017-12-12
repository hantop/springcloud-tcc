package com.tuandai.architecture.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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

import com.alibaba.fastjson.JSONObject;
import com.tuandai.architecture.componet.ThresholdsTimeManage;
import com.tuandai.architecture.componet.ToInstancesIPUrl;
import com.tuandai.architecture.config.RestTemplateHelper;
import com.tuandai.architecture.constant.BZStatusCode;
import com.tuandai.architecture.constant.Constants;
import com.tuandai.architecture.constant.TransState;
import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.exception.ServiceException;
import com.tuandai.architecture.repository.TransRepository;
import com.tuandai.architecture.service.TccTaskService;

@Service
public class TccTaskServiceImpl implements TccTaskService {

	private static final Logger logger = LoggerFactory.getLogger(TccTaskServiceImpl.class);

	// 执行Check操作线程池
	private static ExecutorService executorCheckServicePool = Executors.newCachedThreadPool();

	@Autowired
	TransRepository transRepository;

	@Autowired
	ThresholdsTimeManage thresholdsTimeManage;

	@Autowired
	ToInstancesIPUrl toInstancesIPUrl;

	@Override
	public List<Trans> getCCTrans(int state) {
		if (TransState.CONFIRM.code() != state && TransState.CANCEL.code() != state) {
			return null;
		}
		Trans trans = new Trans();
		trans.setTransState(state);
		trans.setCcTime(new Date());

		return transRepository.getScheduleCCTrans(trans);
	}

	@Override
	public List<Trans> getCheckTrans() {
		Trans trans = new Trans();
		trans.setTransState(TransState.PENDING.code());
		trans.setCcTime(new Date());

		return transRepository.getScheduleCheckTrans(trans);
	}

	@Override
	public int checkTran(Trans trans) {
		if (null == trans) {
			throw new ServiceException(BZStatusCode.INVALID_MODEL_FIELDS);
		}
		if (TransState.PENDING.code() != trans.getTransState()) {
			throw new ServiceException(BZStatusCode.INVALID_STATE_OPTION);
		}

		// 更新阀值
		int checkTimes = trans.getCheckTimes();
		trans.setCheckTimes(checkTimes + 1);
		int checkThreshold = trans.getCheckThreshold();
		trans.setCheckThreshold(checkThreshold - 1);
		trans.setCheckTime(thresholdsTimeManage.createTccCheckTime(trans.getCheckThreshold()));
		transRepository.update(trans);

		// 请求体
		JSONObject httpEntity = new JSONObject();
		httpEntity.put("transId", trans.getTransId());
		// 执行请求
		try {
			ResponseEntity<String> response = null;
			response = RestTemplateHelper.getRestTemplate().exchange(
					toInstancesIPUrl.getIPUrl(trans.getServiceName() + trans.getCheckUrl()), HttpMethod.POST,
					new HttpEntity<String>(String.valueOf(trans.getTransId()), Constants.header), String.class);
			logger.debug("CheckUrl: {} ", response.toString());
			if (HttpStatus.OK.equals(response.getStatusCode())) {
				int status = Integer.valueOf(response.getBody());
				return status;
			}
		} catch (Exception e) {
			logger.error("check transId: {} , exception: {}",trans.getTransId(), e.getMessage());
		}
		return -1;
	}

	@Override
	public List<Trans> checkTrans(List<Trans> trans) {

		final ConcurrentHashMap<Long, Integer> isCheckedMap = new ConcurrentHashMap<>();
		final CountDownLatch latch = new CountDownLatch(trans.size());
		for (Trans tu : trans) {
			executorCheckServicePool.execute(() -> {
				Integer state = checkTran(tu);
				isCheckedMap.put(tu.getTransId(), state);
				latch.countDown();
			});
		}

		try {
			// 此处必须用超时机制，不可直接： x.await();
			latch.await(30, TimeUnit.SECONDS);
			for (Trans tu : trans) {
				tu.setTransState(isCheckedMap.get(tu.getTransId()));
			}
		} catch (InterruptedException e) {
			logger.error("=================countDownLatch exception============= {}", e.getMessage());
		}

		return trans;
	}

}
