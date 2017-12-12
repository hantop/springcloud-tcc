package com.tuandai.architecture.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.TransUrls;

@Mapper
public interface TransUrlsRepository {

	void createIfNotExistsTable();

	void truncateTable();

	int deleteByTransId(Long transId);

	List<TransUrls> getByTransId(Long transId);

	void dropTable();

	int insert(TransUrls transUrls);

}
