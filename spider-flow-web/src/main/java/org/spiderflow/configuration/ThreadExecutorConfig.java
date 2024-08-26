package org.spiderflow.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author yida
 * @package org.spiderflow
 * @date 2024-08-25 20:00
 * @description 线程池配置
 */
@Component
public class ThreadExecutorConfig {
	@Value("${websocket.thread-pool.core-pool-size:2}")
	private int corePoolSize;

	@Value("${websocket.thread-pool.max-pool-size:8}")
	private int maxPoolSize;

	@Value("${websocket.thread-pool.queue-capacity:1024}")
	private int queueCapacity;

	@Value("${websocket.thread-pool.thread-pool-name-prefix}")
	private String threadPoolNamePrefix;

	@Bean("websocketServerAsyncScheduledPool")
	public TaskExecutor websocketServerAsyncScheduledPool(){
		ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
		threadPoolExecutor.setCorePoolSize(corePoolSize);
		threadPoolExecutor.setMaxPoolSize(maxPoolSize);
		threadPoolExecutor.setQueueCapacity(queueCapacity);
		threadPoolExecutor.setThreadNamePrefix(threadPoolNamePrefix);
		threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		return threadPoolExecutor;
	}
}
