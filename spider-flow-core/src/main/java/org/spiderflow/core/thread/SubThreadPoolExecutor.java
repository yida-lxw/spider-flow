package org.spiderflow.core.thread;

import org.spiderflow.core.constants.Constants;
import org.spiderflow.core.model.SpiderNode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yida
 * @package org.spiderflow.core.thread
 * @date 2024-08-21 15:55
 * @description Type your description over here.
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

	/**
	 * 真正的线程池
	 */
	private ThreadPoolExecutor threadPoolExecutor;

	public SubThreadPoolExecutor(int threads, ThreadSubmitStrategy submitStrategy, ThreadPoolExecutor threadPoolExecutor) {
		super();
		this.threads = threads;
		this.futures = new Future[threads];
		this.submitStrategy = submitStrategy;
		this.threadPoolExecutor = threadPoolExecutor;
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

	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}

	public ThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
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
					if (index() == -1 && Thread.currentThread().getThreadGroup() == Constants.SPIDER_FLOW_THREAD_GROUP) {
						futureTask.run();
					} else {
						//等待有空闲线程时在提交
						await();
						//提交任务至线程池中
						futures[index()] = threadPoolExecutor.submit(futureTask);
					}
				}
			} catch (InterruptedException ignored) {
			}
		}
	}
}
