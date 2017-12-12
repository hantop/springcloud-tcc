package com.tuandai.architecture.repository;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.TranOrder;

@Mapper
public interface TranOrderRepository {

	void createIfNotExistsTable();

	void truncateTable();

	void dropTable();
	
	int insert(TranOrder tranOrder);
	
	int updateState(TranOrder tranOrder);

	int delete(Integer id);

	TranOrder getByTranId(String transId);

}
