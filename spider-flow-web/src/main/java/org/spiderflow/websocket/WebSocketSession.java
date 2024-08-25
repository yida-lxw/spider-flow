package org.spiderflow.websocket;

/**
 * @author yida
 * @package org.spiderflow.websocket
 * @date 2024-08-25 21:25
 * @description 自定义WebSocket Session
 */
public class WebSocketSession {
	private String sessionId;
	/**最近一次接收到心跳包的时间(毫秒数)*/
	private long lastRecieveHeartbeatTime;

	public WebSocketSession(String sessionId, long lastRecieveHeartbeatTime) {
		this.sessionId = sessionId;
		this.lastRecieveHeartbeatTime = lastRecieveHeartbeatTime;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public long getLastRecieveHeartbeatTime() {
		return lastRecieveHeartbeatTime;
	}

	public void setLastRecieveHeartbeatTime(long lastRecieveHeartbeatTime) {
		this.lastRecieveHeartbeatTime = lastRecieveHeartbeatTime;
	}
}
