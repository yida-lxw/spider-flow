package org.spiderflow.core.event;

/**
 * @author yida
 * @package org.spiderflow.core.listener
 * @date 2024-08-26 18:02
 * @description NotifySpiderTaskExecutionStatusEvent事件监听者接口
 */
public interface NotifySpiderTaskExecutionStatusEventListener {
	void sendMessageToNotifyWebSocketClient(NotifySpiderTaskExecutionStatusEvent notifySpiderTaskExecutionStatusEvent);
}
