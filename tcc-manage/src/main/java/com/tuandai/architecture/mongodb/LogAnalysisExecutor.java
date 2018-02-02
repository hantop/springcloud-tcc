package com.tuandai.architecture.mongodb;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.tuandai.architecture.domain.LogData;
import com.tuandai.architecture.util.CheckPointUtil;
import com.tuandai.architecture.util.CheckPointUtil.CheckPoint;
import com.tuandai.architecture.util.ThreadPoolExecutorUtils;

@Component
public class LogAnalysisExecutor implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(LogAnalysisExecutor.class);

	@Value("${mongodb.sleep.time}")
	private long sleepTime;

	@Value("${checkpoint.path}")
	private String checkPointPath;

	@Value("${checkpoint.diff.time}")
	private long intervalTime;

	@Value("${checkpoint.size}")
	private int size;

	private final static Long SLEEP_TIME = 2000L;

	@Autowired
	private LogDataService logDataService;

	// private static Date checkPoint = new Date(0);

	@Override
	public void run() {
		try {
			process();
		} catch (Exception e) {
			logger.error("process error:{}", e.getCause());
		}
	}

	/**
	 * 分析数据
	 */
	public void analysis() {
		new Thread(this).start();
	}

	/**
	 * 处理逻辑开始
	 */
	private void process() throws Exception {
		CheckPoint.size(size);
		while (true) {
			System.out.println("执行分析逻辑........" + System.currentTimeMillis());
			// 1.获取checkpoint
			CheckPoint startCheckPoint = CheckPointUtil.check();
			CheckPoint checkPoint = (CheckPoint) startCheckPoint.clone();
			CheckPoint endCheckPoint = CheckPointUtil.checkNow(intervalTime);
			List<LogData> datas = logDataService.checkData(startCheckPoint, endCheckPoint);// 获取开始表数据
			// 2.多线程分发处理
			if (!CollectionUtils.isEmpty(datas)) {
				CountDownLatch countDownLatch = new CountDownLatch(datas.size());
				// long lastTime = 0;
				for (LogData data : datas) {
					if (data.getTime().getTime() > checkPoint.getTime()) {
						checkPoint.setTime(data.getTime().getTime());
					}
					ThreadPoolExecutor executor = ThreadPoolExecutorUtils.getAnasisThreadPoolExecutor();
					executor.execute(new LogDataTask(logDataService, data, countDownLatch));
				}
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					logger.error("LogAnalysisExecutor countDownLatch.await() 未知错误");
				}

				// 3.处理完成则修改checkpoint的值
				logger.debug("更新checkPoint为：" + checkPoint);
				CheckPointUtil.update(checkPoint);
			} else {
				try {
					Thread.sleep(getSleepTime());
				} catch (InterruptedException e) {
					logger.error("LogAnalysisExecutor Thread.sleep(sleepTime) 未知错误");
				}
			}
		}
	}

	public long getSleepTime() {
		return sleepTime <= 0 ? SLEEP_TIME : sleepTime;
	}
}
