package com.tuandai.architecture.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.Trans;

@Mapper
public interface TransRepository {

	void createIfNotExistsTable();

	void truncateTable();

	void dropTable();

	int insert(Trans trans);

	int update(Trans trans);

	int delete(String id);
	
    void deleteBatch(List<Long> transIds);

	Trans getByTransId(String id);
	
	List<Trans> getScheduleCCTrans(Trans trans);
	
	List<Trans> getScheduleCheckTrans(Trans trans);
}
