package com.tuandai.architecture.componet;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.tuandai.architecture.repository.TransLogsRepository;
import com.tuandai.architecture.repository.TransRepository;
import com.tuandai.architecture.repository.TransUrlsRepository;

@Component
public class InitailDBTables {
	@Resource
	private TransRepository transRepository;
	
	@Resource
	private TransUrlsRepository transUrlsRepository;
	
	@Resource
	private TransLogsRepository transLogsRepository;

	public void createTables() {
		transRepository.createIfNotExistsTable();
		transUrlsRepository.createIfNotExistsTable();
		transLogsRepository.createIfNotExistsTable();
	}
}
