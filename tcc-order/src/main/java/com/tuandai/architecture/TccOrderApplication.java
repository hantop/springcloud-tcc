package com.tuandai.architecture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.tuandai.architecture.component.InitailDBTables;

@EnableCircuitBreaker
@EnableFeignClients(basePackages ="com.tuandai.architecture.controller.client")
@EnableEurekaClient
@SpringBootApplication
@EnableTransactionManagement
public class TccOrderApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(TccOrderApplication.class, args);
		
		//初始化数据库表
		applicationContext.getBean(InitailDBTables.class).createTables();
	}
	
}
