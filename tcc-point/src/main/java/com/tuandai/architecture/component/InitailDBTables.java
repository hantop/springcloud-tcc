package com.tuandai.architecture.component;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.tuandai.architecture.repository.UserPointRepository;
import com.tuandai.architecture.repository.UserPointTryRepository;

@Component
public class InitailDBTables {
	@Resource
	private UserPointRepository userPointRepository;
	@Resource
	private UserPointTryRepository userPointTryRepository;


	public void createTables() {
		userPointRepository.createIfNotExistsTable();
		userPointTryRepository.createIfNotExistsTable();
	}
}
