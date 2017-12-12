package com.tuandai.architecture.componet;

import java.net.InetAddress;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tuandai.architecture.constant.TransState;
import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.service.TccService;
import com.tuandai.architecture.service.TccTaskService;

@Component
public class TccJobs {

	private static final Logger logger = LoggerFactory.getLogger(TccJobs.class);

	// 执行Job操作线程池
	public static final ExecutorService executorJobServicePool = Executors.newFixedThreadPool(5);

	public final static long CC_MINUTE = 5 * 1000;

	public final static long CHECK_MINUTE = 10 * 1000;

	public static Boolean SCHEDULE_OPEN = false;

	private static Boolean SCHEDULE_CHECK_OPEN = true;

	private static Boolean SCHEDULE_CC_OPEN = true;

	@Autowired
	TccService tccService;

	@Autowired
	TccTaskService tccTaskService;

	@Value("${eureka.instance.instanceId}")
	String instanceId;

	@Value("${spring.application.name}")
	String serviceName;

	@Value("${server.port}")
	String serverPort;

	String serverAddress = "";

	@Autowired
	DiscoveryClient discoveryClient;

	/**
	 * 1 min 检测一次 注： 多实例的情况下，并非完全保障只有一个服务运行定时任务；
	 */
	@Scheduled(fixedDelay = 60000)
	public void CheckScheduleInstance() {
		logger.debug("============ CheckScheduleInstance Start=========");
		if (serverAddress.length() < 2) {
			try {
				serverAddress = InetAddress.getLocalHost().getHostAddress();
			} catch (Exception e) {
				logger.error("============get server ip Exception : {} =========", e.getMessage());
				return;
			}
		}
		String localUri = serverAddress + ":" + serverPort;
		logger.debug("============ localUri  {}=========", localUri);

		TreeSet<String> treeset = new TreeSet<String>();
		List<ServiceInstance> listInstance = discoveryClient.getInstances(serviceName);
		for (ServiceInstance si : listInstance) {
			treeset.add(si.getUri().toString());
		}

		if (treeset.size() > 0) {
			logger.debug("============ First Instance {}=========", treeset.first());
			String[] uri = treeset.first().split("//");

			if (2 == uri.length && localUri.equals(uri[1])) {
				TccJobs.SCHEDULE_OPEN = true;
				return;
			}
		}

		TccJobs.SCHEDULE_OPEN = false;
	}

	/**
	 * CC 操作定时任务
	 */
	@Scheduled(fixedDelay = CC_MINUTE)
	public void CCJob() {
		logger.debug("========CCJob=========");
		if (!(TccJobs.SCHEDULE_OPEN && TccJobs.SCHEDULE_CC_OPEN)) {
			return;
		}
		TccJobs.executorJobServicePool.execute(() -> {
			logger.debug("========CCJob Running=========");
			exeCCJobThread();
		});
	}

	private void exeCCJobThread() {
		TccJobs.SCHEDULE_CC_OPEN = false;
		try {
			// confirm
			List<Trans> confirmTrans = tccTaskService.getCCTrans(TransState.CONFIRM.code());
			for (Trans trans : confirmTrans) {
				tccService.confrimTrans(trans.getTransId());
			}
			// cancel
			List<Trans> cancelTrans = tccTaskService.getCCTrans(TransState.CANCEL.code());
			for (Trans trans : cancelTrans) {
				tccService.cancelTrans(trans.getTransId());
			}
		} catch (Exception e) {
			logger.error("exeCCJobThread Exception: {}", e.getMessage());
		}

		TccJobs.SCHEDULE_CC_OPEN = true;
	}

	/**
	 * Check 操作定时任务
	 */
	@Scheduled(fixedDelay = CHECK_MINUTE)
	public void CheckJob() {
		logger.debug("========CheckJob=========");
		if (!(TccJobs.SCHEDULE_OPEN && TccJobs.SCHEDULE_CHECK_OPEN)) {
			return;
		}
		// check
		TccJobs.executorJobServicePool.execute(() -> {
			logger.debug("========CheckJob Running=========");
			exeCheckThread();
		});
	}

	private void exeCheckThread() {
		TccJobs.SCHEDULE_CHECK_OPEN = false;
		
		try {
			//check list限流 最大100个；
			List<Trans> checkTrans = tccTaskService.getCheckTrans();
			checkTrans = tccTaskService.checkTrans(checkTrans);
			for (Trans tu : checkTrans) {
				if (TransState.CONFIRM.code() == tu.getTransState()) {
					tccService.confrimMark(tu.getTransId());
				}
				if (TransState.CANCEL.code() == tu.getTransState()) {
					tccService.cancelMark(tu.getTransId());
				}
			}

		} catch (Exception e) {
			logger.error("exeCheckThread Exception: {}", e.getMessage());
		}

		TccJobs.SCHEDULE_CHECK_OPEN = true;
	}

}
