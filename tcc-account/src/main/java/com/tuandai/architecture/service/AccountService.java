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
	public int transTry(String name, String transId) {
		try {
			Integer account = 1;
			UserAccountTry userAccountTry = new UserAccountTry();
			userAccountTry.setUid(transId);
			userAccountTry.setAccount(account);
			userAccountTry.setName(name);
			userAccountTry.setCreateTime(new Date());

			userAccountTryRepository.insert(userAccountTry);
			UserAccount userAccount = new UserAccount();
			userAccount.setName(name);
			userAccount.setAccount(account);

			int ir = userAccountRepository.tryAccount(userAccount);
			if (0 == ir) {
				logger.error("try account error: {}", transId);
				throw new ServiceException();
			}

			logger.error("try account : {}", transId);
			return ir;
		} catch (Exception ex) {
			logger.error("try account Exception: {},{}", transId, ex.getMessage());
			throw new ServiceException();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean transCancel(String transId) {
		try {
			UserAccountTry userAccountTry = userAccountTryRepository.getById(transId);
			// 幂等
			if (null == userAccountTry) {
				return true;
			}
			UserAccount userAccount = new UserAccount();
			userAccount.setName(userAccountTry.getName());
			userAccount.setAccount(userAccountTry.getAccount());

			if (1 != userAccountRepository.cancelAccount(userAccount)) {
				logger.error("cancel userAccountRepository error: {}", transId);
				throw new ServiceException();
			}

			if (1 != userAccountTryRepository.delete(transId)) {
				logger.error("cancel userAccountTryRepository error: {}", transId);
				throw new ServiceException();
			}
			logger.error("cancel account : {}", transId);
			return true;
		} catch (Exception ex) {
			logger.error("cancel account Exception: {},{}", transId, ex.getMessage());
			throw new ServiceException();
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean transConfirm(String transId) {
		try {
			UserAccountTry userAccountTry = userAccountTryRepository.getById(transId);
			// 幂等
			if (null == userAccountTry) {
				return true;
			}

			if (1 != userAccountTryRepository.delete(transId)) {
				logger.error("confirm account error: {}", transId);
				throw new ServiceException();
			}
			logger.error("confirm account : {}", transId);
			return true;
		} catch (Exception ex) {
			logger.error("confirm account Exception: {},{}", transId, ex.getMessage());
			throw new ServiceException();
		}
	}

}
