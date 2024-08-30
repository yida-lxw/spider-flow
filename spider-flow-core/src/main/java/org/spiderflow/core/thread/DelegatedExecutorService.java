package org.spiderflow.core.thread;

import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author yida
 * @package org.spiderflow.core.thread
 * @date 2024-08-30 16:37
 * @description ExecutorService代理
 */
public class DelegatedExecutorService extends AbstractExecutorService {
	private final ExecutorService executorService;

	public DelegatedExecutorService(ExecutorService executorService) {
		Assert.notNull(executorService, "executorService must be not null !");
		this.executorService = executorService;
	}

	@Override
	public void execute(Runnable command) {
		executorService.execute(command);
	}

	@Override
	public void shutdown() {
		executorService.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return executorService.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return executorService.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return executorService.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException {
		return executorService.awaitTermination(timeout, timeUnit);
	}

	@Override
	public Future<?> submit(Runnable runnable) {
		return executorService.submit(runnable);
	}

	@Override
	public <T> Future<T> submit(Callable<T> callable) {
		return executorService.submit(callable);
	}

	@Override
	public <T> Future<T> submit(Runnable runnable, T result) {
		return executorService.submit(runnable, result);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> callables) throws InterruptedException {
		return executorService.invokeAll(callables);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> callables, long timeout, TimeUnit unit)
			throws InterruptedException {
		return executorService.invokeAll(callables, timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> callables)
			throws InterruptedException, ExecutionException {
		return executorService.invokeAny(callables);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> callables, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return executorService.invokeAny(callables, timeout, unit);
	}
}
