package com.tuandai.architecture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.tuandai.architecture.component.InitailDBTables;


@EnableEurekaClient
@SpringBootApplication
@EnableTransactionManagement
public class TccAccountApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(TccAccountApplication.class, args);
		
		//初始化数据库表
		applicationContext.getBean(InitailDBTables.class).createTables();
	}
	
}
