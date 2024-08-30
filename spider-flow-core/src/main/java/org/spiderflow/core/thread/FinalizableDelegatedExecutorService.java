package org.spiderflow.core.thread;

import java.util.concurrent.ExecutorService;

/**
 * @author yida
 * @date 2024-08-30 16:36
 * @description 保证ExecutorService对象在被内存回收时能正常关闭
 */
public class FinalizableDelegatedExecutorService extends DelegatedExecutorService {
	public FinalizableDelegatedExecutorService(ExecutorService executor) {
		super(executor);
	}

	@Override
	protected void finalize() {
		super.shutdown();
	}
}
