package com.tuandai.architecture.config;

import java.util.HashMap;
import java.util.Random;

import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.tuandai.architecture.constant.Constants;

/**
 * 
 */
@Configuration
public class RestTemplateHelper {
	public final static int MIN_REST_POOL_SIZE = 5;

	public final static int MAX_REST_POOL_SIZE = 30;
	
	private static Random rd = new Random();

	private static HashMap<String, RestTemplate> hstemp = new HashMap<String, RestTemplate>();

	public static void initRestTemplate(int size) {
		final HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
	            .setMaxConnTotal(200)
	            .setMaxConnPerRoute(100)
	            .build());
		httpRequestFactory.setConnectionRequestTimeout(Constants.RESTFUL_MAX_TIMEOUT_SECONDS * 1000);
		httpRequestFactory.setConnectTimeout(Constants.RESTFUL_MAX_TIMEOUT_SECONDS * 1000);
		httpRequestFactory.setReadTimeout(Constants.RESTFUL_MAX_TIMEOUT_SECONDS * 1000);

		hstemp.clear();

		for (int i = 0; i < size; i++) {
			hstemp.put(String.valueOf(i), new RestTemplate(httpRequestFactory));
		}

	}

	public static RestTemplate getRestTemplate() {
		if (hstemp.size() < RestTemplateHelper.MIN_REST_POOL_SIZE) {
			return null;
		}
		int ii = rd.nextInt(hstemp.size());
		return hstemp.get(String.valueOf(ii));
	}
}
