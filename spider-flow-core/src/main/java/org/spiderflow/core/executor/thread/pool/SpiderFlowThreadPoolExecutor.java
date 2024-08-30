package org.spiderflow.core.executor.thread.pool;

import org.spiderflow.core.constants.Constants;
import org.spiderflow.core.executor.submit.strategy.ThreadSubmitStrategy;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yida
 * @package org.spiderflow.core.thread
 * @date 2024-08-21 16:04
 * @description Type your description over here.
 */
public class SpiderFlowThreadPoolExecutor {
	/**
	 * 最大线程数
	 */
	private int maxThreads;

	/**
	 * 真正线程池
	 */
	private ThreadPoolExecutor threadPoolExecutor;

	/**
	 * 线程number计数器
	 */
	private final AtomicInteger poolNumber = new AtomicInteger(1);


	public SpiderFlowThreadPoolExecutor(int maxThreads) {
		super();
		this.maxThreads = maxThreads;
		//创建线程池实例
		this.threadPoolExecutor = new ThreadPoolExecutor(maxThreads, maxThreads, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), runnable -> {
			//重写线程名称
			return new Thread(Constants.SPIDER_FLOW_THREAD_GROUP, runnable, Constants.THREAD_POOL_NAME_PREFIX + poolNumber.getAndIncrement());
		});
	}

	public Future<?> submit(Runnable runnable) {
		return this.threadPoolExecutor.submit(runnable);
	}

	/**
	 * 创建子线程池
	 *
	 * @param threads 线程池大小
	 * @return
	 */
	public SubThreadPoolExecutor createSubThreadPoolExecutor(int threads, ThreadSubmitStrategy submitStrategy) {
		return new SubThreadPoolExecutor(Math.min(maxThreads, threads), submitStrategy, threadPoolExecutor);
	}
}
