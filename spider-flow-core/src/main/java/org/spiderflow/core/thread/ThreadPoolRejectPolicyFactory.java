package org.spiderflow.core.thread;

import java.util.concurrent.RejectedExecutionHandler;

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
	 * @return
	 */
	public static RejectedExecutionHandler buildThreadPoolRejectPolicy(ThreadPoolRejectPolicy threadPoolRejectPolicy) {
		if (null == threadPoolRejectPolicy) {
			threadPoolRejectPolicy = ThreadPoolRejectPolicy.DEFAULT_THREAD_POOL_REJECT_POLICY;
		}
		return threadPoolRejectPolicy.getRejectPolicy();
	}
}
