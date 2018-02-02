package com.tuandai.architecture.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuandai.architecture.config.SpringBootConfig;
import com.tuandai.architecture.domain.TransLogs;
import com.tuandai.architecture.service.TccLogService;

@Service
public class TccLogServiceImpl implements TccLogService {

	private static final Logger logger = LoggerFactory.getLogger(TccLogServiceImpl.class);


	@Autowired
	SpringBootConfig springBootConfig;

	@Override
	public void writeLog(Date createTime, String logstr, String transId, Integer transState, String transUrl) {
		TransLogs log = new TransLogs();
		log.setCreateTime(createTime);
		log.setLog(logstr);
		log.setTransId(transId);
		log.setTransState(transState);
		log.setTransUrl(transUrl);

		logger.info("TCCLOG: " + log.toString());
	}

}
