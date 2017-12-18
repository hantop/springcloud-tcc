package com.tuandai.architecture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.tuandai.architecture.component.InitailDBTables;
import com.tuandai.architecture.config.SpringBootConfig;

@EnableScheduling
@EnableEurekaClient
@SpringBootApplication
@EnableTransactionManagement
public class TccScheduleApplication {

	public static void main(String[] args) {

		ApplicationContext applicationContext = SpringApplication.run(TccScheduleApplication.class, args);

		// 初始化数据库表
		applicationContext.getBean(InitailDBTables.class).createTables();

		// 初始化配置
		applicationContext.getBean(SpringBootConfig.class).initailConfig();
		
	}

}
