package com.tuandai.architecture.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tuandai.architecture.domain.UserAccount;
import com.tuandai.architecture.domain.UserAccountTry;
import com.tuandai.architecture.exception.ServiceException;
import com.tuandai.architecture.repository.UserAccountRepository;
import com.tuandai.architecture.repository.UserAccountTryRepository;

@Service
public class AccountService {

	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private UserAccountTryRepository userAccountTryRepository;

	@Transactional(rollbackFor = Exception.class)
	public void transTry(String name, Integer transId) {
		Integer account = 3;
		UserAccountTry userAccountTry = new UserAccountTry();
		userAccountTry.setId(transId);
		userAccountTry.setAccount(account);
		userAccountTry.setName(name);
		userAccountTry.setCreateTime(new Date());

		userAccountTryRepository.insert(userAccountTry);
		UserAccount userAccount = new UserAccount();
		userAccount.setName(name);
		userAccount.setAccount(account);

		int ir = userAccountRepository.tryAccount(userAccount);
		if (0 == ir) {
			logger.error("try account error: {}",userAccount.getId());
			throw new ServiceException();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void transCancel(Integer transId) {
		UserAccountTry userAccountTry = userAccountTryRepository.getById(transId);
		// 幂等
		if (null == userAccountTry) {
			return;
		}
		UserAccount userAccount = new UserAccount();
		userAccount.setName(userAccountTry.getName());
		userAccount.setAccount(userAccountTry.getAccount());

		if (1 != userAccountRepository.cancelAccount(userAccount)) {
			logger.error("cancel account error: {}",transId);
			throw new ServiceException();
		}

		if (1 != userAccountTryRepository.delete(transId)) {
			logger.error("delete account error: {}",transId);
			throw new ServiceException();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void transConfirm(Integer transId) {
		UserAccountTry userAccountTry = userAccountTryRepository.getById(transId);
		// 幂等
		if (null == userAccountTry) {
			return;
		}

		if (1 != userAccountTryRepository.delete(transId)) {
			throw new ServiceException();
		}
	}

}
