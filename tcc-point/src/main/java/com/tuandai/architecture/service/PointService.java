package com.tuandai.architecture.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tuandai.architecture.domain.UserPoint;
import com.tuandai.architecture.domain.UserPointTry;
import com.tuandai.architecture.exception.ServiceException;
import com.tuandai.architecture.repository.UserPointRepository;
import com.tuandai.architecture.repository.UserPointTryRepository;

@Service
public class PointService {

	private static final Logger logger = LoggerFactory.getLogger(PointService.class);

	@Autowired
	private UserPointRepository userPointRepository;

	@Autowired
	private UserPointTryRepository userPointTryRepository;

	@Transactional(rollbackFor = Exception.class)
	public int transTry(String name, String transId) {
		try {
			Integer point = 1;
			UserPointTry userpointTry = new UserPointTry();
			userpointTry.setUid(transId);
			userpointTry.setPoint(point);
			userpointTry.setName(name);
			userpointTry.setCreateTime(new Date());

			int ir = userPointTryRepository.tryPoint(userpointTry);
			if (0 == ir) {
				logger.error("try point error: {}", transId);
				throw new ServiceException();
			}
			logger.error("try point : {}", transId);
			return ir;
		} catch (Exception ex) {
			logger.error("try point Exception: {},{}", transId, ex.getMessage());
			throw new ServiceException();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean transCancel(String transId) {
		try {
			UserPointTry userPointTry = userPointTryRepository.getById(transId);

			// 幂等
			if (null == userPointTry) {
				return false;
			}

			if (1 != userPointTryRepository.delete(transId)) {
				logger.error("cancel point error: {}", transId);
				throw new ServiceException();
			}
			logger.error("cancel point : {}", transId);

			return true;
		} catch (Exception ex) {
			logger.error("cancel point Exception: {},{}", transId, ex.getMessage());
			throw new ServiceException();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean transConfirm(String transId) {
		try {
			UserPointTry userpointTry = userPointTryRepository.getById(transId);
			// 幂等
			if (null == userpointTry) {
				return false;
			}

			UserPoint userPoint = new UserPoint();
			userPoint.setUid(userpointTry.getUid());
			userPoint.setName(userpointTry.getName());
			userPoint.setPoint(userpointTry.getPoint());
			userPoint.setCreateTime(new Date());

			if (1 != userPointRepository.confirmPoint(userPoint)) {
				logger.error("confirm userPointRepository error: {}", transId);
				throw new ServiceException();
			}

			if (1 != userPointTryRepository.delete(transId)) {
				logger.error("confirm userPointTryRepository error: {}", transId);
				throw new ServiceException();
			}
			logger.error("confirm point : {}", transId);
			return true;
		} catch (Exception ex) {
			logger.error("confirm point Exception: {},{}", transId, ex.getMessage());
			throw new ServiceException();
		}
	}

}
