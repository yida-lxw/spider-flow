package org.spiderflow.core.event;

/**
 * @author yida
 * @package org.spiderflow.core.event
 * @date 2024-08-26 18:11
 * @description NotifySpiderTaskExecutionStatusEvent事件发布者接口
 */
public interface NotifySpiderTaskExecutionStatusEventPublisher {
	void notifySpiderTaskExecutionStatusChange(NotifySpiderTaskExecutionStatusEvent notifySpiderTaskExecutionStatusEvent);
}
