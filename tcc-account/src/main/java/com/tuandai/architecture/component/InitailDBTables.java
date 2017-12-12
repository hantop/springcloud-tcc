package com.tuandai.architecture.component;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.tuandai.architecture.repository.UserAccountRepository;
import com.tuandai.architecture.repository.UserAccountTryRepository;

@Component
public class InitailDBTables {
	@Resource
	private UserAccountRepository userAccountRepository;
	@Resource
	private UserAccountTryRepository userAccountTryRepository;


	public void createTables() {
		userAccountRepository.createIfNotExistsTable();
		userAccountTryRepository.createIfNotExistsTable();
	}
}
