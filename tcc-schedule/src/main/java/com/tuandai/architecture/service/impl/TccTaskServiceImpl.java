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
import org.springframework.stereotype.Service;

import com.tuandai.architecture.client.TccServiceClient;
import com.tuandai.architecture.component.ThresholdsTimeManage;
import com.tuandai.architecture.config.SpringBootConfig;
import com.tuandai.architecture.constant.BZStatusCode;
import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.exception.ServiceException;
import com.tuandai.architecture.repository.TransRepository;
import com.tuandai.architecture.repository.TransRetryRepository;
import com.tuandai.architecture.service.TccTaskService;
import com.tuandai.transaction.constant.TCCState;

@Service
public class TccTaskServiceImpl implements TccTaskService {

	private static final Logger logger = LoggerFactory.getLogger(TccTaskServiceImpl.class);

	// 执行Check操作线程池
	private static ExecutorService executorCheckServicePool = Executors.newFixedThreadPool(200);

	@Autowired
	TransRepository transRepository;
	
	@Autowired
	TransRetryRepository transRetryRepository;

	@Autowired
	ThresholdsTimeManage thresholdsTimeManage;

	@Autowired
	TccServiceClient tccServiceClient;

	@Autowired
	SpringBootConfig springBootConfig;

	@Override
	public List<Trans> getCCTrans(int state) {
		if (TCCState.COMMIT.value() != state && TCCState.CANCEL.value() != state) {
			return null;
		}
		Trans trans = new Trans();
		trans.setTransState(state);
		trans.setCcTime(new Date());

		if (TCCState.COMMIT.value() == state) {
			return transRetryRepository.getScheduleConfirmTrans(trans);
		}else{
			return transRetryRepository.getScheduleCancelTrans(trans);
		}
		
	}

	@Override
	public List<Trans> getCheckTrans() {
		Trans trans = new Trans();
		trans.setTransState(TCCState.UNKNOW.value());
		trans.setCcTime(new Date());

		return transRepository.getScheduleCheckTrans(trans);
	}

	@Override
	public int checkTran(Trans trans) {
		if (null == trans) {
			throw new ServiceException(BZStatusCode.INVALID_MODEL_FIELDS);
		}
		if (TCCState.UNKNOW.value() != trans.getTransState()) {
			throw new ServiceException(BZStatusCode.INVALID_STATE_OPTION);
		}

		// 更新阀值
		int checkTimes = trans.getCheckTimes();
		trans.setCheckTimes(checkTimes + 1);
		int checkThreshold = trans.getCheckThreshold();
		trans.setCheckThreshold(checkThreshold - 1);
		trans.setCheckTime(thresholdsTimeManage.createTccCheckTime(trans.getCheckThreshold()));
		transRepository.update(trans);

		// 执行请求
		try {
			String result = tccServiceClient.checkTrans(trans);
			if (null != result) {
				logger.debug("CheckUrl: {} ", result);
				int status = Integer.valueOf(result);
				return status;
			}
		} catch (Exception e) {
			logger.error("check transId: {} , exception: {}", trans.getTransId(), e.getMessage());
		}
		return -1;
	}

	@Override
	public List<Trans> checkTrans(List<Trans> trans) {

		final ConcurrentHashMap<String, Integer> isCheckedMap = new ConcurrentHashMap<>();
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
