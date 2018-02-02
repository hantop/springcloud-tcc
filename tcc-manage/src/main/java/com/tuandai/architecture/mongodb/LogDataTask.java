package com.tuandai.architecture.mongodb;

import java.util.concurrent.CountDownLatch;

import com.tuandai.architecture.domain.LogData;

public class LogDataTask implements Runnable {
	//private static ApplicationContext context = SpringUtil.getApplicationContext();
	private LogDataService logDataService;
	private LogData logData;
	private CountDownLatch latch;

	public LogDataTask(LogDataService logDataService, LogData logData, CountDownLatch latch) {
		this.logDataService = logDataService;
		this.logData = logData;
		this.latch = latch;
	}

	@Override
	public void run() {
		try {
			logDataService.analysis(logData);
			//context.publishEvent(event);
		} finally {
			latch.countDown();
		}
	}
}
