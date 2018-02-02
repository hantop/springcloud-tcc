package com.tuandai.architecture.controller.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tuandai.architecture.controller.PatchTransModel;

@Component
public class PointTccClientFallback implements PointTccClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(PointTccClientFallback.class);


	@Override
	public String tryTrans(PatchTransModel body) {
		didNotGetResponse();
		return null;
	}

	private void didNotGetResponse() {
		LOGGER.error("service '{}' has become unreachable", PointTccClient.SERVICE_ID);
	}
}
