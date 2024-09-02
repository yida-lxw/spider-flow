package org.spiderflow.core.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.constants.Constants;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yida
 * @package org.spiderflow.core.thread
 * @date 2024-08-30 16:50
 * @description 全局线程池
 */
public class GlobalThreadPool {
	private static final Logger logger = LoggerFactory.getLogger(GlobalThreadPool.class);
	private ExecutorService executorService;

	private final ReentrantLock lock = new ReentrantLock();

	private static class SingletonHolder {
		private static final GlobalThreadPool INSTANCE = new GlobalThreadPool();
	}

	private GlobalThreadPool() {
		init();
	}

	public static GlobalThreadPool getInstance(){
		return SingletonHolder.INSTANCE;
	}

	/**
	 * @description 初始化全局线程池
	 * @author yida
	 * @date 2024-08-30 17:08:32
	 */
	public void init() {
		if (null != executorService) {
			executorService.shutdownNow();
		}
		executorService = ThreadExecutorBuilder.create()
				.setThreadFactory(new NamedThreadFactory("Global-Thread-Pool-", false))
				.useArrayBlockingQueue(Constants.DEFAULT_QUEUE_CAPACITY)
				.useBlockingRejectPolicy()
				.build();
	}

	public void shutdownNow() {
		shutdown(true);
	}

	public void shutdown() {
		shutdown(false);
	}

	public void shutdown(boolean isNow) {
		try {
			lock.lock();
			if (null != executorService) {
				if (isNow) {
					executorService.shutdownNow();
				} else {
					executorService.shutdown();
				}
			}
		} catch (Exception e) {
			logger.error("As shut down the Thread Pool occur exception:\n{}.", e.getMessage());
		} finally {
			lock.unlock();
		}
	}

	public void execute(Runnable runnable) {
		if(null == executorService || executorService.isTerminated()) {
			logger.warn("The global thread pool had been shut down. so we can't execute any runnable task.");
			return;
		}
		try {
			executorService.execute(runnable);
		} catch (Exception e) {
			throw new IllegalStateException("As running thread task occur exception.", e);
		}
	}

	public Future<?> submit(Runnable runnable) {
		if(null == executorService || executorService.isTerminated()) {
			logger.warn("The global thread pool had been shut down. so we can't submit any runnable task.");
			return null;
		}
		return executorService.submit(runnable);
	}

	public <T> Future<T> submit(Callable<T> callable) {
		if(null == executorService || executorService.isTerminated()) {
			logger.warn("The global thread pool had been shut down. so we can't submit any callable task.");
			return null;
		}
		return executorService.submit(callable);
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}
}
