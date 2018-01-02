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

	
	@Value("${timeoutTransactionPatchTransCmd:600}")
	int timeoutTransactionPatchTransCmd;

	@Value("${sizeTransactionPatchTransPool:100}")
	int sizeTransactionPatchTransPool;


	@Value("${timeoutRestTryTransCmd:600}")
	int timeoutRestTryTransCmd;

	@Value("${sizeRestTryTransPool:100}")
	int sizeRestTryTransPool;
	

	@Value("${timeoutTransactionCCTransCmd:200}")
	int timeoutTransactionCCTransCmd;

	@Value("${sizeTransactionCCTransPool:50}")
	int sizeTransactionCCTransPool;
	
	

	@Value("${timeoutRestConfirmTransCmd:500}")
	int timeoutRestConfirmTransCmd;

	@Value("${sizeRestConfirmTransPool:10}")
	int sizeRestConfirmTransPool;

	@Value("${timeoutRestCancelTransCmd:500}")
	int timeoutRestCancelTransCmd;

	@Value("${sizeRestCancelTransPool:10}")
	int sizeRestCancelTransPool;
	
	
	
	public String getTccCheckThresholds() {
		return tccCheckThresholds;
	}

	public String getTccCCThresholds() {
		return tccCCThresholds;
	}
	
	// 断路器配置
	public void initalNetflixConfig() {
		// fallback make default to 1000
		ConfigurationManager.getConfigInstance()
		.setProperty("hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests", 1000);

		ConfigurationManager.getConfigInstance()
				.setProperty("hystrix.command.default.circuitBreaker.enabled", true);
		
		ConfigurationManager.getConfigInstance()
		.setProperty("hystrix.command.default.circuitBreaker.requestVolumeThreshold", 50);
		
		ConfigurationManager.getConfigInstance()
		.setProperty("hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds", 5000);

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
		

		logger.debug("========= timeoutRestConfirmTransCmd =========: {}",
				timeoutRestConfirmTransCmd);
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.command.RestConfirmTransCmd.execution.isolation.thread.timeoutInMilliseconds",
				timeoutRestConfirmTransCmd);

		logger.debug("========= sizeRestConfirmTransPool =========: {}",
				sizeRestConfirmTransPool);
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.threadpool.RestConfirmTransPool.coreSize",
				sizeRestConfirmTransPool);
		

		logger.debug("========= timeoutRestCancelTransCmd =========: {}",
				timeoutRestCancelTransCmd);
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.command.RestCancelTransCmd.execution.isolation.thread.timeoutInMilliseconds",
				timeoutRestCancelTransCmd);

		logger.debug("========= sizeRestCancelTransPool =========: {}",
				sizeRestCancelTransPool);
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.threadpool.RestCancelTransPool.coreSize",
				sizeRestCancelTransPool);
		
	}
}
