package org.spiderflow.core.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author yida
 * 当队列容量已满时，对任务入队操作进行阻塞，直到队列容量未满为止
 * 而不是像JDK默认采取AbortPolicy拒绝策略，AbortPolicy策略会
 * 导致超出队列容量的任务被丢弃，而采取BlockingPolicy策略能保证
 * 所有任务最终都能够被执行
 */
public class BlockingPolicy implements RejectedExecutionHandler {
	/**
	 * 队列满了时，入队操作最大阻塞等待超时时间，默认值为10， 单位：秒
	 */
	public static final long DEFAULT_BLOCKING_WAIT_TIMEOUT = 10;

	private final Lock lock = new ReentrantLock();

	// 队列未满条件变量
	private final Condition notFull = lock.newCondition();

	public BlockingPolicy() {
	}

	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
		// 如果线程池已经关闭，则直接抛出异常
		if (executor.isShutdown()) {
			throw new RejectedExecutionException("ThreadPoolExecutor has been shutdown");
		} else {
			BlockingQueue<Runnable> queue = executor.getQueue();
			int queueSize = queue.size();
			int remainingCapacity = queue.remainingCapacity();
			// 如果在队列元素不足时，可以开启新的线程来执行任务，不阻塞入队操作
			if (queueSize < executor.getMaximumPoolSize() && remainingCapacity == 0) {
				executor.execute(runnable);
			} else {
				// 队列已满，等待队列未满条件变量，直到有任务被取出使得队列未满为止
				lock.lock();
				try {
					while (queue.remainingCapacity() == 0) {
						// 每次等待 10 秒钟，避免无限等待造成线程阻塞
						notFull.await(DEFAULT_BLOCKING_WAIT_TIMEOUT, TimeUnit.SECONDS);
					}
					// 任务入列
					queue.put(runnable);
					// 通知其他等待的线程，队列未满了
					notFull.signalAll();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					lock.unlock();
				}
			}
		}
	}
}

