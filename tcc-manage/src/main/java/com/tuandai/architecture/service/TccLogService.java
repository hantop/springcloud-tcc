package com.tuandai.architecture.service;

import java.util.Date;
import java.util.List;

import com.tuandai.architecture.domain.TransLogs;

public interface TccLogService {
	
	void writeLog(Date createTime, String logstr, Long transId, Integer transState, String transUrl);
	
	List<TransLogs> getLog(Long transId);
}
