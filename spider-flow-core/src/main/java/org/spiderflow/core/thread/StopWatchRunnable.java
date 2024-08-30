package org.spiderflow.core.thread;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author yida
 * @date 2023-08-26 16:58
 * @description 带StopWatch计时功能的Runnable
 */
public abstract class StopWatchRunnable implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(StopWatchRunnable.class);
	protected StopWatch stopWatch;

	public StopWatchRunnable(StopWatch stopWatch) {
		if (null == stopWatch) {
			throw new IllegalArgumentException("The stopWatch MUST NOT be null in StopWatchRunnable.");
		}
		this.stopWatch = stopWatch;
	}

	/**
	 * @description 线程执行之前先开始StopWatch计时
	 * @author yida
	 * @date 2023-08-26 17:07:28
	 */
	protected void beforeRun() {
		this.stopWatch.start();
	}

	/**
	 * @description 线程实际需要执行的业务逻辑(供子类重写)
	 * @author yida
	 * @date 2023-08-26 17:06:51
	 */
	protected abstract void actualRun();

	/**
	 * @description 线程执行结束之前，结束StopWatch计时
	 * @author yida
	 * @date 2023-08-26 17:08:00
	 */
	protected void afterRun() {
		this.stopWatch.stop();
	}


	@Override
	public void run() {
		try {
			this.beforeRun();
			this.actualRun();
		} catch (Exception e) {
			log.error("As Calling the run() in the StopWatchRunnable, we occur exception:[{}]", e.getMessage());
		} finally {
			this.afterRun();
		}
	}

	/**
	 * @param timeUnit (若不指定单位,则默认为毫秒)
	 * @description 获取当前线程的执行耗时
	 * @author yida
	 * @date 2023-08-26 17:10:27
	 */
	protected long getExecutionTime(TimeUnit timeUnit) {
		return this.stopWatch.getTime((null == timeUnit) ? TimeUnit.MILLISECONDS : timeUnit);
	}

	/**
	 * @description 获取当前线程的执行耗时(若不指定单位, 则默认为毫秒)
	 * @author yida
	 * @date 2023-08-26 17:10:27
	 */
	protected long getExecutionTime() {
		return getExecutionTime(null);
	}

	public StopWatch getStopWatch() {
		return stopWatch;
	}

	public void setStopWatch(StopWatch stopWatch) {
		this.stopWatch = stopWatch;
	}
}
