package com.tuandai.architecture.repository;

import org.apache.ibatis.annotations.Mapper;

import com.tuandai.architecture.domain.UserAccountTry;

@Mapper
public interface UserAccountTryRepository {

    void createIfNotExistsTable();
    
    void truncateTable();
        
    int delete(Integer id);
    
    UserAccountTry getById(Integer id);
    
    void dropTable();
    
    int insert(UserAccountTry userAccountTry);
}
