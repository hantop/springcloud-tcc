package com.tuandai.architecture.repository;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.UserPoint;

@Mapper
public interface UserPointRepository {

	void createIfNotExistsTable();

	void truncateTable();

	int delete(String uid);

	UserPoint getById(String uid);

	void dropTable();

	int insert(UserPoint userAccount);

	int confirmPoint(UserPoint userAccount);
}
