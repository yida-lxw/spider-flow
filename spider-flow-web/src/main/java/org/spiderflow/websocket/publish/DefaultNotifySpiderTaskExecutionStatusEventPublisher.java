package org.spiderflow.websocket.publish;

import org.spiderflow.core.event.NotifySpiderTaskExecutionStatusEventPublisher;
import org.spiderflow.core.event.NotifySpiderTaskExecutionStatusEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author yida
 * @package org.spiderflow.websocket.publish
 * @date 2024-08-26 16:46
 * @description NotifySpiderTaskExecutionStatusEvent事件发布者
 */
@Component
public class DefaultNotifySpiderTaskExecutionStatusEventPublisher implements NotifySpiderTaskExecutionStatusEventPublisher {
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void notifySpiderTaskExecutionStatusChange(NotifySpiderTaskExecutionStatusEvent notifySpiderTaskExecutionStatusEvent) {
		applicationEventPublisher.publishEvent(notifySpiderTaskExecutionStatusEvent);
	}
}
