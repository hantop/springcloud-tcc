package com.tuandai.architecture.component;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.tuandai.architecture.repository.TransRepository;
import com.tuandai.architecture.repository.TransRetryRepository;

@Component
public class InitailDBTables {
	@Resource
	private TransRepository transRepository;

	// @Resource
	// private TransUrlsRepository transUrlsRepository;

	@Resource
	private TransRetryRepository transRetryRepository;

	public void createTables() {
		transRepository.createIfNotExistsTable();
		// transUrlsRepository.createIfNotExistsTable();
		transRetryRepository.createIfNotExistsTable();
	}
}
