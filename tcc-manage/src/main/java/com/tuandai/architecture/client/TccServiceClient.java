package com.tuandai.architecture.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * 
 */
@Service
public class TccServiceClient {
	private static final Logger logger = LoggerFactory.getLogger(TccServiceClient.class);

	@Autowired
	RestTemplate restTemplate;

	@HystrixCommand(commandKey = "RestTryTransCmd", fallbackMethod = "tryTransFallback", threadPoolKey = "RestTryTransPool")
	public String tryTrans(String transUrl,JSONObject jsonBody) {
		logger.debug("[tryIn] tryTrans Fallback  url: {} , body: {}", transUrl, jsonBody );
		HttpHeaders header = new HttpHeaders();
		ResponseEntity<String> response = restTemplate.exchange("http://" + transUrl + "/try", HttpMethod.POST,
				new HttpEntity<JSONObject>(jsonBody, header), String.class);
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return response.getBody();
		}
		return null;
	}
	
	public String tryTransFallback(String transUrl,JSONObject jsonBody) {
		logger.error("[fallback] tryTrans Fallback  url: {} , body: {}", transUrl, jsonBody );
		return null;
	}

	public String confirmTrans(String transUrl,String transId) {
		HttpHeaders header = new HttpHeaders();
		ResponseEntity<String> response = restTemplate.exchange("http://" + transUrl + "/confirm", HttpMethod.POST,
				new HttpEntity<String>(transId, header), String.class);
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return response.getBody();
		}
		return null;
	}

	public String cancelTrans(String transUrl,String transId) {
		HttpHeaders header = new HttpHeaders();
		ResponseEntity<String> response = restTemplate.exchange("http://" + transUrl + "/cancel", HttpMethod.POST,
				new HttpEntity<String>(transId, header), String.class);
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return response.getBody();
		}
		return null;
	}

}
