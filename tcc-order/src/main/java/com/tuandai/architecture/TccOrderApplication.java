package com.tuandai.architecture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import com.tuandai.transaction.client.TCCClient;

@EnableCircuitBreaker
@EnableFeignClients(basePackages = "com.tuandai.architecture.controller.client")
@EnableEurekaClient
@SpringBootApplication
public class TccOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TccOrderApplication.class, args);

		/**
		 * 初始化事务
		 */
		if ("true".equals(args[0])) {
			TCCClient.initAsLog4j();;
		}else{
			TCCClient.initAsRpc(null);		
		}

	}

}
