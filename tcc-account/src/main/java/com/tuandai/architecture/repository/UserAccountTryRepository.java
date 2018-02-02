package com.tuandai.architecture.repository;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.UserAccountTry;

@Mapper
public interface UserAccountTryRepository {

    void createIfNotExistsTable();
    
    void truncateTable();
        
    int delete(String uid);
    
    UserAccountTry getById(String uid);
    
    void dropTable();
    
    int insert(UserAccountTry userAccountTry);
}
