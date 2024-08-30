package org.spiderflow.core.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池拒绝策略类工厂
 */
public class ThreadPoolRejectPolicyFactory {
	/**
	 * 创建线程池拒绝策略
	 *
	 * @return
	 */
	public static RejectedExecutionHandler buildThreadPoolRejectPolicy(String threadPoolRejectPolicyTag) {
		ThreadPoolRejectPolicy threadPoolRejectPolicy = ThreadPoolRejectPolicy.of(threadPoolRejectPolicyTag);
		return buildThreadPoolRejectPolicy(threadPoolRejectPolicy);
	}

	/**
	 * 创建线程池拒绝策略
	 *
	 * @return
	 */
	public static RejectedExecutionHandler buildThreadPoolRejectPolicy(ThreadPoolRejectPolicy threadPoolRejectPolicy) {
		if (null == threadPoolRejectPolicy) {
			threadPoolRejectPolicy = ThreadPoolRejectPolicy.BLOCKING;
		}
		RejectedExecutionHandler rejectedExecutionHandler = null;
		if (ThreadPoolRejectPolicy.CALLER_RUNS.equals(threadPoolRejectPolicy)) {
			rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
		} else if (ThreadPoolRejectPolicy.ABORT.equals(threadPoolRejectPolicy)) {
			rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
		} else if (ThreadPoolRejectPolicy.DISCARD.equals(threadPoolRejectPolicy)) {
			rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
		} else if (ThreadPoolRejectPolicy.DISCARD_OLDEST.equals(threadPoolRejectPolicy)) {
			rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
		} else if (ThreadPoolRejectPolicy.BLOCKING.equals(threadPoolRejectPolicy)) {
			rejectedExecutionHandler = new BlockingPolicy();
		} else {
			throw new IllegalArgumentException("Unsupport the current threadPoolRejectPolicy:[" + threadPoolRejectPolicy +
					"] to build ThreadPool Reject Policy.");
		}
		return rejectedExecutionHandler;
	}
}
