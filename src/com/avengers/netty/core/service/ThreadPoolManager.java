package com.avengers.netty.core.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.avengers.netty.core.om.ServerConfig;
import com.avengers.netty.core.util.CoreTracer;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author LamHa
 *
 */
public class ThreadPoolManager {
	private ExecutorService logPools;
	private ExecutorService apiPools;
	private ThreadPoolExecutor boardPools;

	private static ThreadPoolManager _instance = new ThreadPoolManager();

	public static ThreadPoolManager getInstance() {
		return _instance;
	}

	public ThreadPoolManager() {
		logPools = Executors.newFixedThreadPool(ServerConfig.threadPoolLogSize,
				new ThreadFactoryBuilder().setNameFormat("LogPool-%d").build());

		apiPools = Executors.newFixedThreadPool(ServerConfig.threadPoolAPISize,
				new ThreadFactoryBuilder().setNameFormat("APIPool-%d").build());

		boardPools = new ThreadPoolExecutor(ServerConfig.threadPoolBoardInsertSize, Integer.MAX_VALUE,
				ServerConfig.threadPoolBoardInsert_CacheTime, TimeUnit.MINUTES, new SynchronousQueue<Runnable>(),
				new ThreadFactoryBuilder().setNameFormat("BoardPool-%d").build());

		CoreTracer.info(ThreadPoolManager.class,
				"Init Pool Manager done, LogPool: " + ServerConfig.threadPoolLogSize + ", APIPool: "
						+ ServerConfig.threadPoolAPISize + ", BoardPool: " + ServerConfig.threadPoolBoardInsertSize);
	}

	public void shutdown() {
		logPools.shutdown();
		apiPools.shutdown();
		boardPools.shutdown();

		CoreTracer.info(ThreadPoolManager.class, "-------------SHUTDOWN ALL THREAD POOLS-------------");
	}

	public ExecutorService getApiPool() {
		return apiPools;
	}

	public ThreadPoolExecutor getBoardMoneyPool() {
		return boardPools;
	}

	public void executeLogGeneral(Runnable logTask) {
		logPools.execute(logTask);
	}

	public void executeAPI(Runnable apiTask) {
		apiPools.execute(apiTask);
	}

	public void executeBoardMoneyLog(Runnable logTask) {
		boardPools.execute(logTask);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("ThreadPoolManager: ").append("logPools: ")
				.append(logPools.toString()).append(", apiPools: ").append(apiPools.toString()).append(", boardPools: ")
				.append(boardPools.toString());
		return builder.toString();
	}

}
