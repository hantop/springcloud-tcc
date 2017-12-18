package com.tuandai.architecture.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.netflix.config.ConfigurationManager;

@Component
public class SpringBootConfig {
	private static final Logger logger = LoggerFactory.getLogger(SpringBootConfig.class);
	
	@Value("${spring.application.name}")
	String serviceName;
	
	@Value("${tcc.check.thresholds}")
	String tccCheckThresholds;

	@Value("${tcc.cc.thresholds}")
	String tccCCThresholds;

	@Value("${timeoutTransactionPatchTransCmd:3000}")
	int timeoutTransactionPatchTransCmd;

	@Value("${sizeTransactionPatchTransPool:200}")
	int sizeTransactionPatchTransPool;

	@Value("${timeoutTransactionCCTransCmd:3000}")
	int timeoutTransactionCCTransCmd;

	@Value("${sizeTransactionCCTransPool:200}")
	int sizeTransactionCCTransPool;

	@Value("${timeoutRestTryTransCmd:5000}")
	int timeoutRestTryTransCmd;

	@Value("${sizeRestTryTransPool:500}")
	int sizeRestTryTransPool;
	
	
	public String getTccCheckThresholds() {
		return tccCheckThresholds;
	}

	public String getTccCCThresholds() {
		return tccCCThresholds;
	}
	
	// 断路器配置
	public void initalNetflixConfig() {
		// fallback make default to 200
		ConfigurationManager.getConfigInstance()
				.setProperty("hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests", 500);

		logger.debug("========= timeoutTransactionPatchTransCmd =========: {}",
				timeoutTransactionPatchTransCmd);
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.command.TransactionPatchTransCmd.execution.isolation.thread.timeoutInMilliseconds",
				timeoutTransactionPatchTransCmd);

		logger.debug("========= sizeTransactionPatchTransPool =========: {}",
				sizeTransactionPatchTransPool);
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.threadpool.TransactionPatchTransPool.coreSize",
				sizeTransactionPatchTransPool);
		

		logger.debug("========= timeoutTransactionCCTransCmd =========: {}",
				timeoutTransactionCCTransCmd);
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.command.TransactionCCTransCmd.execution.isolation.thread.timeoutInMilliseconds",
				timeoutTransactionCCTransCmd);

		logger.debug("========= sizeTransactionCCTransPool =========: {}",
				sizeTransactionCCTransPool);
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.threadpool.TransactionCCTransPool.coreSize",
				sizeTransactionCCTransPool);

		

		logger.debug("========= timeoutRestTryTransCmd =========: {}",
				timeoutRestTryTransCmd);
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.command.RestTryTransCmd.execution.isolation.thread.timeoutInMilliseconds",
				timeoutRestTryTransCmd);

		logger.debug("========= sizeRestTryTransPool =========: {}",
				sizeRestTryTransPool);
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.threadpool.RestTryTransPool.coreSize",
				sizeRestTryTransPool);
		
	}
}
