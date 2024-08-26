package org.spiderflow.core.event;

import org.spiderflow.core.job.SpiderJobNodeStatusInfo;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * @author yida
 * @package org.spiderflow.websocket.event
 * @date 2024-08-26 16:32
 * @description 通知爬虫任务执行状态事件
 */
public class NotifySpiderTaskExecutionStatusEvent extends ApplicationEvent {
	private SpiderJobNodeStatusInfo spiderJobNodeStatusInfo;

	public NotifySpiderTaskExecutionStatusEvent(SpiderJobNodeStatusInfo spiderJobNodeStatusInfo) {
		super(spiderJobNodeStatusInfo);
		this.spiderJobNodeStatusInfo = spiderJobNodeStatusInfo;
	}

	public NotifySpiderTaskExecutionStatusEvent(SpiderJobNodeStatusInfo spiderJobNodeStatusInfo, Clock clock) {
		super(spiderJobNodeStatusInfo, clock);
		this.spiderJobNodeStatusInfo = spiderJobNodeStatusInfo;
	}

	public SpiderJobNodeStatusInfo getSpiderJobNodeStatusInfo() {
		return spiderJobNodeStatusInfo;
	}

	public void setSpiderJobNodeStatusInfo(SpiderJobNodeStatusInfo spiderJobNodeStatusInfo) {
		this.spiderJobNodeStatusInfo = spiderJobNodeStatusInfo;
	}
}
