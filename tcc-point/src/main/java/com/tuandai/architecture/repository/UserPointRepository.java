package com.tuandai.architecture.repository;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.UserPoint;

@Mapper
public interface UserPointRepository {

	void createIfNotExistsTable();

	void truncateTable();

	int delete(Integer id);

	UserPoint getById(Integer id);

	void dropTable();

	int insert(UserPoint userAccount);

	int confirmPoint(UserPoint userAccount);
}
