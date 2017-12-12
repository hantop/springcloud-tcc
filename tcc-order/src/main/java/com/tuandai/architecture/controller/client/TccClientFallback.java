package com.tuandai.architecture.controller.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tuandai.architecture.model.PatchTransModel;
import com.tuandai.architecture.model.PostTransModel;

@Component
public class TccClientFallback implements TccClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(TccClientFallback.class);

	
	@Override
	public String create(PostTransModel body) {
		didNotGetResponse();
		return null;
	}

	@Override
	public String tryTrans(PatchTransModel body) {
		didNotGetResponse();
		return null;
	}

	@Override
	public String confirmTrans(String transId) {
		didNotGetResponse();
		return null;
	}

	@Override
	public String cancelTrans(String transId) {
		didNotGetResponse();
		return null;
	}


    private void didNotGetResponse() {
        LOGGER.error("service '{}' has become unreachable", TccClient.SERVICE_ID);
    }
}
