package com.tuandai.architecture.repository;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.UserAccount;

@Mapper
public interface UserAccountRepository {

	void createIfNotExistsTable();

	void truncateTable();

	int delete(Integer id);

	UserAccount getById(Integer id);

	void dropTable();

	int insert(UserAccount userAccount);

	int tryAccount(UserAccount userAccount);

	int cancelAccount(UserAccount userAccount);
}
