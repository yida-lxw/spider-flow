package org.spiderflow.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.utils.DateUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author yida
 * @package org.spiderflow.websocket
 * @date 2024-08-25 20:08
 * @description WebSocket定时任务
 */
@Component
public class WebSocketScheduledTask {
	private static Logger logger = LoggerFactory.getLogger(WebSocketScheduledTask.class);

	@Async("websocketServerAsyncScheduledPool")
	@Scheduled(cron = "${websocket.scheduled-cron}")
	public void checkWebsocketClientIfAlive() {
		logger.info("Start to check the survival status of websocket sessions at {}.", DateUtils.format(System.currentTimeMillis()));
		WebSocketManager.checkWebsocketClientIfAlive();
	}
}
