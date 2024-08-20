package org.spiderflow.concurrent;

import org.spiderflow.model.SpiderNode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SpiderFlowThreadPoolExecutor {

	/**
	 * 最大线程数
	 */
	private int maxThreads;

	/**
	 * 真正线程池
	 */
	private ThreadPoolExecutor executor;

	/**
	 * 线程number计数器
	 */
	private final AtomicInteger poolNumber = new AtomicInteger(1);

	/**
	 * ThreadGroup
	 */
	private static final ThreadGroup SPIDER_FLOW_THREAD_GROUP = new ThreadGroup("spider-flow-group");

	/**
	 * 线程名称前缀
	 */
	private static final String THREAD_POOL_NAME_PREFIX = "spider-flow-";

	public SpiderFlowThreadPoolExecutor(int maxThreads) {
		super();
		this.maxThreads = maxThreads;
		//创建线程池实例
		this.executor = new ThreadPoolExecutor(maxThreads, maxThreads, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), runnable -> {
			//重写线程名称
			return new Thread(SPIDER_FLOW_THREAD_GROUP, runnable, THREAD_POOL_NAME_PREFIX + poolNumber.getAndIncrement());
		});
	}

	public Future<?> submit(Runnable runnable) {
		return this.executor.submit(runnable);
	}


	/**
	 * 创建子线程池
	 *
	 * @param threads 线程池大小
	 * @return
	 */
	public SubThreadPoolExecutor createSubThreadPoolExecutor(int threads, ThreadSubmitStrategy submitStrategy) {
		return new SubThreadPoolExecutor(Math.min(maxThreads, threads), submitStrategy);
	}

	/**
	 * 子线程池
	 */
	public class SubThreadPoolExecutor {

		/**
		 * 线程池大小
		 */
		private int threads;

		/**
		 * 正在执行中的任务
		 */
		private Future<?>[] futures;

		/**
		 * 执行中的数量
		 */
		private AtomicInteger executing = new AtomicInteger(0);

		/**
		 * 是否运行中
		 */
		private volatile boolean running = true;

		/**
		 * 是否提交任务中
		 */
		private volatile boolean submitting = false;

		private ThreadSubmitStrategy submitStrategy;

		public SubThreadPoolExecutor(int threads, ThreadSubmitStrategy submitStrategy) {
			super();
			this.threads = threads;
			this.futures = new Future[threads];
			this.submitStrategy = submitStrategy;
		}

		/**
		 * 等待所有线程执行完毕
		 */
		public void awaitTermination() {
			while (executing.get() > 0) {
				removeDoneFuture();
			}
			running = false;
			//当停止时,唤醒提交任务线程使其结束
			synchronized (submitStrategy) {
				submitStrategy.notifyAll();
			}
		}

		private int index() {
			for (int i = 0; i < threads; i++) {
				if (futures[i] == null || futures[i].isDone()) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * 清除已完成的任务
		 */
		private void removeDoneFuture() {
			for (int i = 0; i < threads; i++) {
				try {
					if (futures[i] != null && futures[i].get(10, TimeUnit.MILLISECONDS) == null) {
						futures[i] = null;
					}
				} catch (Throwable t) {
					//忽略异常
				}
			}
		}

		/**
		 * 等待有空闲线程
		 */
		private void await() {
			while (index() == -1) {
				removeDoneFuture();
			}
		}

		/**
		 * 异步提交任务
		 */
		public <T> Future<T> submitAsync(Runnable runnable, T value, SpiderNode node) {
			SpiderFutureTask<T> future = new SpiderFutureTask<>(() -> {
				try {
					//执行任务
					runnable.run();
				} finally {
					//正在执行的线程数-1
					executing.decrementAndGet();
				}
			}, value, node, this);

			submitStrategy.add(future);
			//如果是第一次调用submitSync方法，则启动提交任务线程
			if (!submitting) {
				submitting = true;
				CompletableFuture.runAsync(this::submit);
			}
			synchronized (submitStrategy) {
				//通知继续从集合中取任务提交到线程池中
				submitStrategy.notifyAll();

			}
			return future;
		}

		private void submit() {
			while (running) {
				try {
					synchronized (submitStrategy) {
						//如果集合是空的，则等待提交
						if (submitStrategy.isEmpty()) {
							submitStrategy.wait();    //等待唤醒
						}
					}
					//当该线程被唤醒时，把集合中所有任务都提交到线程池中
					while (!submitStrategy.isEmpty()) {
						//从提交策略中获取任务提交到线程池中
						SpiderFutureTask<?> futureTask = submitStrategy.get();
						//如果没有空闲线程且在线程池中提交，则直接运行
						if (index() == -1 && Thread.currentThread().getThreadGroup() == SPIDER_FLOW_THREAD_GROUP) {
							futureTask.run();
						} else {
							//等待有空闲线程时在提交
							await();
							//提交任务至线程池中
							futures[index()] = executor.submit(futureTask);
						}
					}
				} catch (InterruptedException ignored) {
				}
			}
		}
	}
}
