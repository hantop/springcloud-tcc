package com.tuandai.architecture.component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.service.TccService;
import com.tuandai.architecture.service.TccTaskService;
import com.tuandai.transaction.constant.TCCState;

@Component
public class TccJobs {

	private static final Logger logger = LoggerFactory.getLogger(TccJobs.class);

	// 执行Job操作线程池
	public static final ExecutorService executorJobServicePool = Executors.newFixedThreadPool(4);

	private static final ExecutorService executorTranCCServicePool = Executors.newFixedThreadPool(200);

	public final static long CC_MINUTE = 1000;

	public final static long CHECK_MINUTE = 5000;

	private static Boolean SCHEDULE_CHECK_OPEN = true;

	private static Boolean SCHEDULE_CONFIRM_OPEN = true;

	private static Boolean SCHEDULE_CANCEL_OPEN = true;

	@Autowired
	TccService tccService;

	@Autowired
	TccTaskService tccTaskService;


	/**
	 * ConfirmJob 操作定时任务
	 */
	@Scheduled(fixedDelay = CC_MINUTE)
	public void ConfirmJob() {
		if (!TccJobs.SCHEDULE_CONFIRM_OPEN) {
			return;
		}
		exeConfirmJobThread();
	}

	private synchronized void exeConfirmJobThread() {
		if (!TccJobs.SCHEDULE_CONFIRM_OPEN) {
			return;
		}
		TccJobs.SCHEDULE_CONFIRM_OPEN = false;
		TccJobs.executorJobServicePool.execute(() -> {
			logger.debug("========ConfirmJob Running=========");
			List<String> resultTrans = new ArrayList<String>();
			try {
				// confirm
				List<Trans> confirmTrans = tccTaskService.getCCTrans(TCCState.COMMIT.value());

				final ConcurrentHashMap<String, Boolean> isConfirmMap = new ConcurrentHashMap<>();
				final CountDownLatch latch = new CountDownLatch(confirmTrans.size());
				for (Trans trans : confirmTrans) {
					executorTranCCServicePool.execute(() -> {
						isConfirmMap.put(trans.getTransId(), tccService.ccTrans(trans));
						latch.countDown();
					});
				}

				try {
					latch.await(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					//TODO 告警接入
					logger.error("=================countDownLatch exception============= {}", e.getMessage());
				}

				for (Trans trans : confirmTrans) {
					Boolean isCC = isConfirmMap.get(trans.getTransId());
					if (isCC) {
						resultTrans.add(trans.getTransId());
					} else {
						// 更新阀值
						tccService.decreaseCCThreshold(trans);
					}
				}
				if (resultTrans.size() > 0) {
					tccService.ccOverList(resultTrans);
				}
			} catch (Exception e) {
				logger.error("exeConfirmJobThread Exception: {}", e.getMessage());
			}

			TccJobs.SCHEDULE_CONFIRM_OPEN = true;
		});
	}

	/**
	 * CancelJob 操作定时任务
	 */
	@Scheduled(fixedDelay = CC_MINUTE)
	public void CancelJob() {
		if (!(TccJobs.SCHEDULE_CANCEL_OPEN)) {
			return;
		}
		exeCancelJobThread();
	}

	private synchronized void exeCancelJobThread() {
		if (!TccJobs.SCHEDULE_CANCEL_OPEN) {
			return;
		}
		TccJobs.SCHEDULE_CANCEL_OPEN = false;
		TccJobs.executorJobServicePool.execute(() -> {
			logger.debug("========CancelJob Running=========");
			List<String> resultTrans = new ArrayList<String>();
			try {
				// confirm
				List<Trans> cancelTrans = tccTaskService.getCCTrans(TCCState.CANCEL.value());

				final ConcurrentHashMap<String, Boolean> isCancelMap = new ConcurrentHashMap<>();
				final CountDownLatch latch = new CountDownLatch(cancelTrans.size());
				for (Trans trans : cancelTrans) {
					executorTranCCServicePool.execute(() -> {
						isCancelMap.put(trans.getTransId(), tccService.ccTrans(trans));
						latch.countDown();
					});
				}

				try {
					latch.await(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					//TODO 告警接入
					logger.error("=================countDownLatch exception============= {}", e.getMessage());
				}

				for (Trans trans : cancelTrans) {
					Boolean isCC = isCancelMap.get(trans.getTransId());
					if (isCC) {
						resultTrans.add(trans.getTransId());
					} else {
						// 更新阀值
						tccService.decreaseCCThreshold(trans);
					}
				}
				if (resultTrans.size() > 0) {
					tccService.ccOverList(resultTrans);
				}
			} catch (Exception e) {
				logger.error("exeCancelJobThread Exception: {}", e.getMessage());
			}

			TccJobs.SCHEDULE_CANCEL_OPEN = true;
		});
	}

	/**
	 * Check 操作定时任务
	 */
	@Scheduled(fixedDelay = CHECK_MINUTE)
	public void CheckJob() {
		logger.debug("========CheckJob=========");
		if (!(TccJobs.SCHEDULE_CHECK_OPEN)) {
			return;
		}
		// check
		exeCheckThread();
	}

	private synchronized void exeCheckThread() {
		if (!TccJobs.SCHEDULE_CHECK_OPEN) {
			return;
		}
		TccJobs.SCHEDULE_CHECK_OPEN = false;

		TccJobs.executorJobServicePool.execute(() -> {
			try {
				// check list限流 最大1000个；
				List<Trans> checkTrans = tccTaskService.getCheckTrans();
				logger.debug("========CheckJob checkTrans========= {}", checkTrans.size());
				checkTrans = tccTaskService.checkTrans(checkTrans);
				for (Trans tu : checkTrans) {
					if (TCCState.COMMIT.value() == tu.getTransState()) {
						tccService.confrimMark(tu.getTransId());
					}
					if (TCCState.CANCEL.value() == tu.getTransState()) {
						tccService.cancelMark(tu.getTransId());
					}
				}

			} catch (Exception e) {
				logger.error("exeCheckThread Exception: {}", e.getMessage());
			}

			TccJobs.SCHEDULE_CHECK_OPEN = true;
		});
	}

}
