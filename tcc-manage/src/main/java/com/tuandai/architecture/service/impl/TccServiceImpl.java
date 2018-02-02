package com.tuandai.architecture.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuandai.architecture.client.TccServiceClient;
import com.tuandai.architecture.component.ThresholdsTimeManage;
import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.repository.TransRepository;
import com.tuandai.architecture.repository.TransRetryRepository;
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
	// private static ExecutorService executorCCServicePool =
	// Executors.newCachedThreadPool();

	@Autowired
	ThresholdsTimeManage thresholdsTimeManage;

	@Autowired
	TransRepository transRepository;

	@Autowired
	TransRetryRepository transRetryRepository;

	@Autowired
	TccLogService transLogsService;

	@Autowired
	TccServiceClient tccServiceClient;

	@Override
	public Boolean insertTrans(Trans trans) {
		if (1 != transRepository.insert(trans)) {
			// throw new ServiceException(BZStatusCode.DUPLICATE_KEY);
			logger.error("insertTrans exception:duplicate data inserted");
		}
		return true;
	}

	@Override
	public boolean insertTransRetry(Trans trans) {
		if (1 != transRetryRepository.insert(trans)) {
			// throw new ServiceException(BZStatusCode.DUPLICATE_KEY);
			logger.error("insertTransRetry exception:duplicate data inserted");
		}
		return true;
	}

	@Override
	public boolean updateTrans(Trans trans) {
		Trans temp = transRepository.getByTransId(trans.getTransId());
		if (temp != null) {
			trans.setCreateTime(temp.getCreateTime());
			return 1 == transRepository.update(trans);
		} else {
			return 1 == transRepository.insert(trans);
		}
	}

	@Override
	public boolean updateTransRetry(Trans trans) {
		Trans temp = transRetryRepository.getByTransId(trans.getTransId());
		if (temp != null) {
			trans.setCreateTime(temp.getCreateTime());
			return 1 == transRetryRepository.update(trans);
		} else {
			return 1 == transRetryRepository.insert(trans);
		}
	}

	@Override
	public void forceCancelTrans(String transId) {

	}

	@Override
	public Trans getTrans(String transId) {
		return transRepository.getByTransId(transId);
	}
}
