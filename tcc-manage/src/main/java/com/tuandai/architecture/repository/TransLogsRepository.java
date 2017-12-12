package com.tuandai.architecture.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.TransLogs;

@Mapper
public interface TransLogsRepository {

	void createIfNotExistsTable();

	void truncateTable();

	int deleteByTransId(Long transId);

	List<TransLogs> getByTransId(Long transId);

	void dropTable();

	int insert(TransLogs transLogs);

}
