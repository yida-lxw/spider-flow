package org.spiderflow.core.thread;

import org.spiderflow.core.constants.Constants;
import org.spiderflow.core.utils.Builder;
import org.spiderflow.core.utils.ObjectUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yida
 * @package org.spiderflow.core.thread
 * @date 2024-08-30 16:25
 * @description Type your description over here.
 */
public class ThreadExecutorBuilder implements Builder<ThreadPoolExecutor> {

	/**线程池核心线程数*/
	private int corePoolSize;
	/**线程池最大线程数*/
	private int maxPoolSize = Integer.MAX_VALUE;
	/**
	 * 线程存活时间，即当池中线程多于初始大小时，多出的线程保留的时长
	 */
	private long keepAliveTime = TimeUnit.SECONDS.toNanos(60);
	/**
	 * 队列，用于存放未执行的线程
	 */
	private BlockingQueue<Runnable> workQueue;
	/**
	 * 线程工厂，用于自定义线程创建
	 */
	private ThreadFactory threadFactory;
	/**
	 * 线程池拒绝策略处理器
	 */
	private RejectedExecutionHandler rejectedExecutionHandler;
	/**
	 * 是否允许核心线程超时
	 */
	private Boolean allowCoreThreadTimeOut;

	/**
	 * 设置初始池大小，默认0
	 *
	 * @param corePoolSize 初始池大小
	 * @return this
	 */
	public ThreadExecutorBuilder setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
		return this;
	}

	/**
	 * 设置最大池大小（允许同时执行的最大线程数）
	 *
	 * @param maxPoolSize 最大池大小（允许同时执行的最大线程数）
	 * @return this
	 */
	public ThreadExecutorBuilder setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
		return this;
	}

	/**
	 * 设置线程存活时间，即当池中线程多于初始大小时，多出的线程保留的时长
	 *
	 * @param keepAliveTime 线程存活时间
	 * @param unit          单位
	 * @return this
	 */
	public ThreadExecutorBuilder setKeepAliveTime(long keepAliveTime, TimeUnit unit) {
		return setKeepAliveTime(unit.toNanos(keepAliveTime));
	}

	/**
	 * 设置线程存活时间，即当池中线程多于初始大小时，多出的线程保留的时长，单位纳秒
	 *
	 * @param keepAliveTime 线程存活时间，单位纳秒
	 * @return this
	 */
	public ThreadExecutorBuilder setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
		return this;
	}

	/**
	 * 设置队列，用于存在未执行的线程<br>
	 * 可选队列有：
	 *
	 * <pre>
	 * 1. {@link SynchronousQueue}    它将任务直接提交给线程而不保持它们。当运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
	 * 2. {@link LinkedBlockingQueue} 默认无界队列，当运行线程大于corePoolSize时始终放入此队列，此时maxPoolSize无效。
	 *                        当构造LinkedBlockingQueue对象时传入参数，变为有界队列，队列满时，运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
	 * 3. {@link ArrayBlockingQueue}  有界队列，相对无界队列有利于控制队列大小，队列满时，运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
	 * </pre>
	 *
	 * @param workQueue 队列
	 * @return this
	 */
	public ThreadExecutorBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
		this.workQueue = workQueue;
		return this;
	}

	public ThreadExecutorBuilder useArrayBlockingQueue() {
		return useArrayBlockingQueue(Constants.DEFAULT_QUEUE_CAPACITY);
	}

	/**
	 * 使用{@link ArrayBlockingQueue} 做为等待队列<br>
	 * 有界队列，相对无界队列有利于控制队列大小，队列满时，运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
	 *
	 * @param capacity 队列容量
	 * @return this
	 */
	public ThreadExecutorBuilder useArrayBlockingQueue(int capacity) {
		return setWorkQueue(new ArrayBlockingQueue<>(capacity));
	}

	public ThreadExecutorBuilder useLinkedBlockingQueue() {
		return useLinkedBlockingQueue(Constants.DEFAULT_QUEUE_CAPACITY);
	}

	/**
	 * 使用{@link LinkedBlockingQueue} 做为等待队列<br>
	 * 有界队列，相对无界队列有利于控制队列大小，队列满时，运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
	 *
	 * @param capacity 队列容量
	 * @return this
	 */
	public ThreadExecutorBuilder useLinkedBlockingQueue(int capacity) {
		return setWorkQueue(new LinkedBlockingQueue<>(capacity));
	}

	/**
	 * 使用{@link SynchronousQueue} 做为等待队列（非公平策略）<br>
	 * 它将任务直接提交给线程而不保持它们。当运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
	 *
	 * @return this
	 */
	public ThreadExecutorBuilder useSynchronousQueue() {
		return useSynchronousQueue(false);
	}

	/**
	 * 使用{@link SynchronousQueue} 做为等待队列<br>
	 * 它将任务直接提交给线程而不保持它们。当运行线程小于maxPoolSize时会创建新线程，否则触发异常策略
	 *
	 * @param fair 是否使用公平访问策略
	 * @return this
	 */
	public ThreadExecutorBuilder useSynchronousQueue(boolean fair) {
		return setWorkQueue(new SynchronousQueue<>(fair));
	}

	public ThreadExecutorBuilder setRejectPolicy(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
		return this;
	}

	/**
	 * @description 使用abort拒绝策略
	 * @author yida
	 * @date 2024-08-30 17:19:03
	 */
	public ThreadExecutorBuilder useAbortRejectPolicy() {
		return setRejectPolicy(ThreadPoolRejectPolicyFactory.buildThreadPoolRejectPolicy(ThreadPoolRejectPolicy.ABORT));
	}

	/**
	 * @description 使用discard拒绝策略
	 * @author yida
	 * @date 2024-08-30 17:19:03
	 */
	public ThreadExecutorBuilder useDiscardRejectPolicy() {
		return setRejectPolicy(ThreadPoolRejectPolicyFactory.buildThreadPoolRejectPolicy(ThreadPoolRejectPolicy.DISCARD));
	}

	/**
	 * @description 使用discard_oldest拒绝策略
	 * @author yida
	 * @date 2024-08-30 17:19:03
	 */
	public ThreadExecutorBuilder useDiscardOldestRejectPolicy() {
		return setRejectPolicy(ThreadPoolRejectPolicyFactory.buildThreadPoolRejectPolicy(ThreadPoolRejectPolicy.DISCARD_OLDEST));
	}

	/**
	 * @description 使用caller_runs拒绝策略
	 * @author yida
	 * @date 2024-08-30 17:19:03
	 */
	public ThreadExecutorBuilder useCallerRunRejectPolicy() {
		return setRejectPolicy(ThreadPoolRejectPolicyFactory.buildThreadPoolRejectPolicy(ThreadPoolRejectPolicy.CALLER_RUNS));
	}

	/**
	 * @description 使用blocking拒绝策略
	 * @author yida
	 * @date 2024-08-30 17:19:03
	 */
	public ThreadExecutorBuilder useBlockingRejectPolicy() {
		return setRejectPolicy(ThreadPoolRejectPolicyFactory.buildThreadPoolRejectPolicy(ThreadPoolRejectPolicy.BLOCKING));
	}

	/**
	 * 设置线程工厂，用于自定义线程创建
	 *
	 * @param threadFactory 线程工厂
	 * @return this
	 */
	public ThreadExecutorBuilder setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
		return this;
	}

	/**
	 * 设置当线程阻塞（block）时的异常处理器，所谓线程阻塞即线程池和等待队列已满，无法处理线程时采取的策略
	 * <p>
	 *
	 * @param rejectedExecutionHandler
	 * @return this
	 */
	public ThreadExecutorBuilder setHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
		return this;
	}

	/**
	 * 设置线程执行超时后是否回收线程
	 *
	 * @param allowCoreThreadTimeOut 线程执行超时后是否回收线程
	 * @return this
	 */
	public ThreadExecutorBuilder setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
		this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
		return this;
	}

	/**
	 * 创建ExecutorBuilder，开始构建
	 *
	 * @return this
	 */
	public static ThreadExecutorBuilder create() {
		return new ThreadExecutorBuilder();
	}

	/**
	 * 构建ThreadPoolExecutor
	 */
	@Override
	public ThreadPoolExecutor build() {
		return build(this);
	}

	/**
	 * 创建有回收关闭功能的ExecutorService
	 *
	 * @return 创建有回收关闭功能的ExecutorService
	 */
	public ExecutorService buildFinalizable() {
		return new FinalizableDelegatedExecutorService(build());
	}

	/**
	 * 构建ThreadPoolExecutor
	 *
	 * @param threadExecutorBuilder this
	 * @return {@link ThreadPoolExecutor}
	 */
	private static ThreadPoolExecutor build(ThreadExecutorBuilder threadExecutorBuilder) {
		final int corePoolSize = threadExecutorBuilder.corePoolSize <= 0? Constants.DEFAULT_CORE_SIZE : threadExecutorBuilder.corePoolSize;
		final int maxPoolSize = threadExecutorBuilder.maxPoolSize <= 0?Constants.DEFAULT_MAX_POOL_SIZE : threadExecutorBuilder.maxPoolSize;
		final long keepAliveTime = threadExecutorBuilder.keepAliveTime;
		final BlockingQueue<Runnable> workQueue;
		if (null != threadExecutorBuilder.workQueue) {
			workQueue = threadExecutorBuilder.workQueue;
		} else {
			workQueue = (corePoolSize <= 0) ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(Constants.DEFAULT_QUEUE_CAPACITY);
		}
		final ThreadFactory threadFactory = (null != threadExecutorBuilder.threadFactory) ? threadExecutorBuilder.threadFactory : Executors.defaultThreadFactory();
		RejectedExecutionHandler rejectedExecutionHandler = ObjectUtils.defaultIfNull(threadExecutorBuilder.rejectedExecutionHandler, ThreadPoolRejectPolicy.ABORT.getRejectPolicy());
		final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				corePoolSize,
				maxPoolSize,
				keepAliveTime,
				TimeUnit.NANOSECONDS,
				workQueue,
				threadFactory,
				rejectedExecutionHandler
		);
		if (null != threadExecutorBuilder.allowCoreThreadTimeOut) {
			threadPoolExecutor.allowCoreThreadTimeOut(threadExecutorBuilder.allowCoreThreadTimeOut);
		}
		return threadPoolExecutor;
	}
}
