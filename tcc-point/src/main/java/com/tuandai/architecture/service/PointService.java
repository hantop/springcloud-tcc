package com.tuandai.architecture.service;

import java.util.Date;

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

	@Autowired
	private UserPointRepository userPointRepository;

	@Autowired
	private UserPointTryRepository userPointTryRepository;

	@Transactional(rollbackFor = Exception.class)
	public void transTry(String name, Integer transId) {
		Integer point = 3;
		UserPointTry userpointTry = new UserPointTry();
		userpointTry.setId(transId);
		userpointTry.setpoint(point);
		userpointTry.setName(name);
		userpointTry.setCreateTime(new Date());

		int ir = userPointTryRepository.tryPoint(userpointTry);
		if (0 == ir) {
			throw new ServiceException();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void transCancel(Integer transId) {
		UserPointTry userPointTry = userPointTryRepository.getById(transId);

		// 幂等
		if (null == userPointTry) {
			return;
		}

		if (1 != userPointTryRepository.delete(transId)) {
			throw new ServiceException();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void transConfirm(Integer transId) {
		UserPointTry userpointTry = userPointTryRepository.getById(transId);
		// 幂等
		if (null == userpointTry) {
			return;
		}

		UserPoint userPoint = new UserPoint();
		userPoint.setId(userpointTry.getId());
		userPoint.setName(userpointTry.getName());
		userPoint.setpoint(userpointTry.getpoint());
		userPoint.setCreateTime(new Date());

		if (1 != userPointRepository.confirmPoint(userPoint)) {
			throw new ServiceException();
		}

		if (1 != userPointTryRepository.delete(transId)) {
			throw new ServiceException();
		}
	}

}
