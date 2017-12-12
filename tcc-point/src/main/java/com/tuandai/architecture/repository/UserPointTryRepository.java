package com.tuandai.architecture.repository;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.UserPointTry;

@Mapper
public interface UserPointTryRepository {

    void createIfNotExistsTable();
    
    void truncateTable();
        
    int delete(Integer id);
    
    UserPointTry getById(Integer id);
    
    void dropTable();
    
    int insert(UserPointTry userAccountTry);

	int tryPoint(UserPointTry userAccountTry);

	int cancelPoint(UserPointTry userAccountTry);
}
