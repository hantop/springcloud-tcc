package com.tuandai.architecture.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.netflix.config.ConfigurationManager;
import com.tuandai.architecture.componet.TccJobs;
import com.tuandai.architecture.componet.ToInstancesIPUrl;

@Component
public class SpringBootConfig {
	private static final Logger logger = LoggerFactory.getLogger(SpringBootConfig.class);

	@Value("${tcc.manage.isHLT:false}")
	Boolean isHLT;

	
	@Value("${tcc.file.log:true}")
	Boolean tccFileLog;
	
	@Value("${spring.application.name}")
	String serviceName;

	@Value("${tcc.check.thresholds}")
	String tccCheckThresholds;

	@Value("${tcc.cc.thresholds}")
	String tccCCThresholds;

	@Value("${timeoutTransactionPatchTransCmd:3000}")
	int timeoutTransactionPatchTransCmd;

	@Value("${sizeTransactionPatchTransPool:100}")
	int sizeTransactionPatchTransPool;

	@Value("${timeoutTransactionCCTransCmd:3000}")
	int timeoutTransactionCCTransCmd;

	@Value("${sizeTransactionCCTransPool:200}")
	int sizeTransactionCCTransPool;
	
	public Boolean isFileLog(){
		return tccFileLog;
	}
	

	public String getTccCheckThresholds() {
		return tccCheckThresholds;
	}

	public String getTccCCThresholds() {
		return tccCCThresholds;
	}

	public void initailConfig() {
		logger.debug("========= isHLT =========: {}",this.isHLT);
		logger.debug("========= tccCheckThresholds =========: {}",this.tccCheckThresholds);
		logger.debug("========= tccCCThresholds =========: {}",this.tccCCThresholds);
		logger.debug("========= TccJobs.CC_MINUTE =========: {}",TccJobs.CC_MINUTE);
		logger.debug("========= TccJobs.CHECK_MINUTE =========: {}",TccJobs.CHECK_MINUTE);
		
		//HTL 开关
		ToInstancesIPUrl.isHLT = this.isHLT;
				
	}

	// 断路器配置
	public void initalNetflixConfig() {
		// fallback make default to 200
		ConfigurationManager.getConfigInstance()
				.setProperty("hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests", 200);

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
		
		
	}
}
