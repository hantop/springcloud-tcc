package com.tuandai.architecture.service;

import java.util.Date;

public interface TccLogService {
	
	void writeLog(Date createTime, String logstr, Long transId, Integer transState, String transUrl);
	
}
