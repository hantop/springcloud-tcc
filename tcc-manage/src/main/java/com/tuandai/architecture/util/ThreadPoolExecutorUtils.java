package com.tuandai.architecture.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ThreadPoolExecutorUtils {
	private final static int ANASIS_SIZE = 100;

	private final static ThreadPoolExecutor anasisThreadPoolExecutor;
	static {
		ThreadFactoryBuilder builder = new ThreadFactoryBuilder();

		anasisThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(ANASIS_SIZE,
				builder.setNameFormat("tcc-analysis-worker-%d").build());
	}

	static public ThreadPoolExecutor getAnasisThreadPoolExecutor() {
		return anasisThreadPoolExecutor;
	}
}
