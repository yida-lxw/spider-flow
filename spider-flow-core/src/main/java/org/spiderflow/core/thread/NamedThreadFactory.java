package org.spiderflow.core.thread;

import org.spiderflow.core.constants.Constants;
import org.spiderflow.core.utils.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可设置线程池名称的线程池工厂
 */
public class NamedThreadFactory implements ThreadFactory {
	private final AtomicInteger threadNumber;
	protected final String threadPoolNamePrefix;
	protected final boolean daemon;
	protected final ThreadGroup threadGroup;
	/**统一异常处理器*/
	private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

	/**默认非守护线程*/
	private static final boolean DAEMON_DEFAULT = false;

	public static final String THREAD_POOL_NAME_PREFIX = Constants.DEFAULT_THREAD_POOL_PREFIX;

	public NamedThreadFactory() {
		this(THREAD_POOL_NAME_PREFIX);
	}

	public NamedThreadFactory(String threadPoolNamePrefix) {
		this(threadPoolNamePrefix, DAEMON_DEFAULT);
	}

	public NamedThreadFactory(String threadPoolNamePrefix, boolean daemon) {
		this(threadPoolNamePrefix, null, daemon);
	}

	public NamedThreadFactory(String threadPoolNamePrefix, ThreadGroup threadGroup) {
		this(threadPoolNamePrefix, threadGroup, DAEMON_DEFAULT);
	}

	public NamedThreadFactory(String threadPoolNamePrefix, ThreadGroup threadGroup, boolean daemon) {
		this(threadPoolNamePrefix, threadGroup, daemon, null);
	}

	public NamedThreadFactory(String threadPoolNamePrefix, ThreadGroup threadGroup, boolean daemon, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.threadPoolNamePrefix = StringUtils.isEmpty(threadPoolNamePrefix) ? THREAD_POOL_NAME_PREFIX : threadPoolNamePrefix;
		if (null == threadGroup) {
			final SecurityManager securityManager = System.getSecurityManager();
			threadGroup = (null != securityManager) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
		}
		this.threadGroup = threadGroup;
		this.daemon = daemon;
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
		this.threadNumber = new AtomicInteger(1);
	}

	public Thread newThread(Runnable runnable) {
		String threadPoolNamePrefix =  this.threadPoolNamePrefix + threadNumber.getAndIncrement();
		Thread thread = new Thread(this.threadGroup, runnable, threadPoolNamePrefix, 0L);
		thread.setDaemon(this.daemon);
		//设置统一异常处理器
		if(null != this.uncaughtExceptionHandler) {
			thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		}
		//优先级
		if (Thread.NORM_PRIORITY != thread.getPriority()) {
			thread.setPriority(Thread.NORM_PRIORITY);
		}
		return thread;
	}
}
