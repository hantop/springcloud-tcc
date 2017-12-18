package com.tuandai.architecture.component;

import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.tuandai.architecture.constant.Constants;

@Component
public class RestTemplateHelper {
	final HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
            .setMaxConnTotal(200)
            .setMaxConnPerRoute(100)
            .build());

	
	@LoadBalanced
	@Bean
	protected RestTemplate restTemplate() {

		httpRequestFactory.setConnectionRequestTimeout(Constants.TCC_RESTFUL_MAX_TIMEOUT_SECONDS * 1000);
		httpRequestFactory.setConnectTimeout(Constants.TCC_RESTFUL_MAX_TIMEOUT_SECONDS * 1000);
		httpRequestFactory.setReadTimeout(Constants.TCC_RESTFUL_MAX_TIMEOUT_SECONDS * 1000);
		
		return new RestTemplate(httpRequestFactory);
	}
}
