package org.spiderflow.websocket.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.job.SpiderJobNodeStatusInfo;
import org.spiderflow.core.event.NotifySpiderTaskExecutionStatusEvent;
import org.spiderflow.core.event.NotifySpiderTaskExecutionStatusEventListener;
import org.spiderflow.core.utils.JacksonUtils;
import org.spiderflow.websocket.WebSocketManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author yida
 * @package org.spiderflow.websocket.listener
 * @date 2024-08-26 16:54
 * @description NotifySpiderTaskExecutionStatusEvent事件监听者
 */
@Component
public class NotifySpiderTaskExecutionStatusEventListenerImpl implements NotifySpiderTaskExecutionStatusEventListener {
	private static Logger logger = LoggerFactory.getLogger(NotifySpiderTaskExecutionStatusEventListenerImpl.class);

	@Async("websocketServerAsyncScheduledPool")
	@EventListener
	@Override
	public void sendMessageToNotifyWebSocketClient(NotifySpiderTaskExecutionStatusEvent notifySpiderTaskExecutionStatusEvent) {
		SpiderJobNodeStatusInfo spiderJobNodeStatusInfo = notifySpiderTaskExecutionStatusEvent.getSpiderJobNodeStatusInfo();
		String message = JacksonUtils.toJSONString(spiderJobNodeStatusInfo);
		WebSocketManager.sendMessage(message);
		logger.info("Had sent message:[{}] to all websocket client to notify them to update jobNode style for UI rendeing.", message);
	}
}
