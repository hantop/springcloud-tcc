package com.tuandai.architecture.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpringBootConfig {
	private static final Logger logger = LoggerFactory.getLogger(SpringBootConfig.class);

	
	@Value("${spring.application.name}")
	String serviceName;
	
	@Value("${tcc.check.thresholds}")
	String tccCheckThresholds;

	@Value("${tcc.cc.thresholds}")
	String tccCCThresholds;

	

	public String getTccCheckThresholds() {
		return tccCheckThresholds;
	}

	public String getTccCCThresholds() {
		return tccCCThresholds;
	}
	
	public void initailConfig() {
		
		logger.debug("========= tccCheckThresholds =========: {}",this.tccCheckThresholds);
		logger.debug("========= tccCCThresholds =========: {}",this.tccCCThresholds);
						
	}

}
