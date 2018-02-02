package com.tuandai.architecture.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.TransUrls;

@Mapper
public interface TransUrlsRepository {

	void createIfNotExistsTable();

	void truncateTable();

	int deleteByTransId(String transId);
	
    void deleteBatchByTransId(List<String> transIds);

	List<TransUrls> getByTransId(String transId);

	void dropTable();

	int insert(TransUrls transUrls);

}
