package com.tuandai.architecture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@EnableHystrixDashboard
@EnableEurekaServer
@SpringBootApplication
public class EurekaRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaRegistryApplication.class, args);
	}
}
