package com.tuandai.transaction.client;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientFacade {
	private static final Logger logger = LoggerFactory.getLogger(ClientFacade.class);
	private static final Logger flume = LoggerFactory.getLogger("flume");
	private static final String DEFAULT_IP = "127.0.0.1";
	private static final String DEFAULT_PORT1 = "41414";
	private static final String DEFAULT_PORT2 = "41414";

	private static Boolean IS_FILE = false;

	private static RpcClient client;
	private static Properties props = new Properties();

	private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

	/**
	 * 初始化
	 * 
	 * @param isLogFile
	 *            是否初始化为 日志文件方式输出；
	 * @param ip
	 *            如果为RPC方式，需要指明RPC服务所在IP地址，默认端口： 41414 ，41415
	 */
	public static void init(Boolean isLog4j, String ip, String rpcPath) {
		if (isLog4j) {
			logger.error("isLogFile: true");
			IS_FILE = true;

			// 定时刷空日志，保障日志数据及时发送。
			scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					flume.info("{}");
				}
			}, 30, 30, TimeUnit.SECONDS);

			return;
		}
		
		if (null == rpcPath) {
			rpcPath = System.getProperty("user.dir") + "/rpcPath";
		}
		LogMapFile.setDirectory(rpcPath);
		initRpc(ip);
		RunningClient();
	}

	private static void initRpc(String ip) {

		if (null == ip || !Isipv4(ip)) {
			logger.error("{} is invalid ip address, set default ip: {}", ip, DEFAULT_IP);
			ip = DEFAULT_IP;
		}
		logger.error("default ip:{} , port1: {}, port2: {}", ip, DEFAULT_PORT1, DEFAULT_PORT2);

		// Setup properties for the failover
		props.put("client.type", "default_failover");
		// List of hosts (space-separated list of user-chosen host aliases)
		props.put("hosts", "h1 h2");

		// host/port pair for each host alias
		String host1 = ip + ":" + DEFAULT_PORT1;
		String host2 = ip + ":" + DEFAULT_PORT2;
		props.put("hosts.h1", host1);
		props.put("hosts.h2", host2);
		props.put("batch-size", 2000);
		props.put("connect-timeout", 1000);
		props.put("request-timeout", 1000);

		// create the client with failover properties
		client = RpcClientFactory.getInstance(props);

	}

	private static void RunningClient() {
		// Send the event
		while (true) {
			try {
				Thread.sleep(1000);
				List<Event> events = SendData.getCheckpointData(1000);
				if (events.size() > 0) {
					logger.error("================== events size : {}", events.size() );

					long startTime = System.currentTimeMillis();
					
					client.appendBatch(events);
					events.clear();
					SendData.writerCheckpoint();
					
					long time = System.currentTimeMillis() - startTime;
					logger.debug(" write log excuse time =============>：[" + time + "ms]");
				}
			} catch (EventDeliveryException e) {
				// clean up and recreate the client
				client.close();
				client = null;
				client = RpcClientFactory.getInstance(props);
				logger.error("error: {}", e);
			} catch (Exception e) {
				logger.error("error: {}", e);
			}
		}
		
	}

	public static Boolean sendDataToFlume(String data) {
		if (IS_FILE) {
			// 输出日志文件
			long startTime = System.currentTimeMillis();
			flume.info(data);
			long time = System.currentTimeMillis() - startTime;
			logger.debug(" write log excuse time =============>：[" + time + "ms]");
			return true;
		} else {

		}

		long startTime = System.currentTimeMillis();
		logger.debug("sendDataToFlume : " + data);
		// Create a Flume Event object that encapsulates the sample data
		LogMapFile.writeFile(data);
		long time = System.currentTimeMillis() - startTime;
		logger.debug(" Send RPC excuse time =============>：[" + time + "ms]");
		return true;
	}

	public static void cleanUp() {
		// Close the RPC connection
		client.close();
	}

	private static boolean Isipv4(String ipv4) {
		if (ipv4 == null || ipv4.length() == 0) {
			return false;// 字符串为空或者空串
		}
		String[] parts = ipv4.split("\\.");
		if (parts.length != 4) {
			return false;// 分割开的数组根本就不是4个数字
		}
		for (int i = 0; i < parts.length; i++) {
			try {
				int n = Integer.parseInt(parts[i]);
				if (n < 0 || n > 255) {
					return false;// 数字不在正确范围内
				}
			} catch (NumberFormatException e) {
				return false;// 转换数字不正确
			}
		}
		return true;
	}
}