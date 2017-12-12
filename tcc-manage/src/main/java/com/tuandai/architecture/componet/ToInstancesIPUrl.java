package com.tuandai.architecture.componet;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

@Component
public class ToInstancesIPUrl {

	@Autowired
	DiscoveryClient discoveryClient;

	static final Random rd = new Random();

	public static Boolean isHLT = false;

	public String getIPUrl(String serviceUrl) {
		if (isHLT) {
			return "http://" + serviceUrl;
		}

		String[] serviceName = serviceUrl.split("/");
		List<ServiceInstance> listServiceInstance = discoveryClient.getInstances(serviceName[0]);
		if (1 > listServiceInstance.size()) {
			return null;
		}

		int ird = rd.nextInt(listServiceInstance.size());
		String ipPort = listServiceInstance.get(ird).getHost() + ":" + listServiceInstance.get(ird).getPort();

		String url = "http://" + serviceUrl.replace(serviceName[0], ipPort);

		return url;
	}

}
