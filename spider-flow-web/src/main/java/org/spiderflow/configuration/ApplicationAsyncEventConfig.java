package org.spiderflow.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;

/**
 * @author yida
 * @package org.spiderflow.configuration
 * @date 2024-08-26 17:55
 * @description Application异步事件配置
 */
@Configuration
public class ApplicationAsyncEventConfig {
	@Bean(name = "applicationEventMulticaster")
	public SimpleApplicationEventMulticaster simpleApplicationEventMulticaster(
			@Qualifier(value="websocketServerAsyncScheduledPool") TaskExecutor websocketServerAsyncScheduledPool) {
		SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
		multicaster.setTaskExecutor(websocketServerAsyncScheduledPool);
		return multicaster;
	}
}
