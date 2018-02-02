package com.tuandai.architecture.dao;

import static com.tuandai.architecture.domain.filter.LogDataFilter.TIME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.tuandai.architecture.domain.LogData;
import com.tuandai.architecture.domain.TableType;
import com.tuandai.architecture.domain.filter.LogDataFilter;

@Component
public class LogDataDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	public LogData findById(String id, TableType type) {
		String collectionName = null;
		if (type != null) {
			if (type == TableType.TCC_START) {// 开始表
				collectionName = "tcctype";
			} else {// 结束表
				collectionName = "tcc";
			}
		}
		return mongoTemplate.findById(id, LogData.class, collectionName);
	}

	public List<LogData> findAll() {
		return mongoTemplate.findAll(LogData.class, "tcc");
	}

	public LogData findByUId(String uid, TableType type) {
		Query query = new Query(Criteria.where("uid").is(uid));
		String collectionName = null;
		if (type != null) {
			if (type == TableType.TCC_START) {// 开始表
				collectionName = "tcctype";
			} else {// 结束表
				collectionName = "tcc";
			}
		}
		return mongoTemplate.findOne(query, LogData.class, collectionName);
		// return mongoTemplate.find(query, LogData.class, "tcc");
	}

	public List<LogData> findByFilter(LogDataFilter filter) {
		Query query = null;
		if (filter.start().getTime() == 0) {
			query = new Query(Criteria.where(TIME).gte(filter.start()).lte(filter.end()));
		} else {
			query = new Query(Criteria.where(TIME).gt(filter.start()).lte(filter.end()));
		}
		query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "time")));
		query.limit(filter.size());
		return mongoTemplate.find(query, LogData.class, "tcctype");
	}
}
