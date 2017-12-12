package com.tuandai.architecture.component;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.tuandai.architecture.repository.TranOrderRepository;

@Component
public class InitailDBTables {
	@Resource
	private TranOrderRepository tranOrderRepository;
	
	public void createTables() {
		tranOrderRepository.createIfNotExistsTable();
	}
}
