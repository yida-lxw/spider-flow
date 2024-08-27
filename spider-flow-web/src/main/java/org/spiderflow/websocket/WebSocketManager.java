package org.spiderflow.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.constants.Constants;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yida
 * @package org.spiderflow.websocket
 * @date 2024-08-25 19:41
 * @description WebSocket管理者
 */
public class WebSocketManager {
	private static Logger logger = LoggerFactory.getLogger(WebSocketManager.class);

	private static final CopyOnWriteArraySet<WebSocketEditorServer> webSocketServerSet = new CopyOnWriteArraySet<>();
	private static final Map<String, WebSocketEditorServer> webSocketServerMap = new ConcurrentHashMap<>();
	private static final Map<String, WebSocketSession> webSocketSessionMap = new ConcurrentHashMap<>();

	private static final ReentrantLock lock = new ReentrantLock();

	/**
	 * @description 检测缓存的Websocket Session是否仍存活，若已不存活，则将其从缓存中移除,避免JVM内存不断膨胀
	 * @author yida
	 * @date 2024-08-25 20:51:33
	 */
	public static void checkWebsocketClientIfAlive() {
		if(!webSocketServerSet.isEmpty()) {
			try {
				lock.lock();
				for (WebSocketEditorServer webSocketServer : webSocketServerSet) {
					String sessionId = webSocketServer.getSessionId();
					Session session = webSocketServer.getSession();
					if(!session.isOpen()) {
						boolean removeResult = webSocketServerSet.remove(webSocketServer);
						if(removeResult) {
							webSocketServerMap.remove(sessionId);
							session.close();
						}
					} else {
						WebSocketSession webSocketSession = webSocketSessionMap.get(sessionId);
						if(null != webSocketSession) {
							long lastRecieveHeartbeatTime = webSocketSession.getLastRecieveHeartbeatTime();
							long currentTimeMills = System.currentTimeMillis();
							if(currentTimeMills - lastRecieveHeartbeatTime > Constants.MAX_INTERVAL_OF_NOT_RECIEVING_HEARTBEAT_PACKET) {
								boolean removeResult = webSocketServerSet.remove(webSocketServer);
								if(removeResult) {
									webSocketServerMap.remove(sessionId);
									webSocketSessionMap.remove(sessionId);
									session.close();
								}
							}
						}
					}
				}
			} catch (Exception e) {
			    logger.error("while check the websocket session was opened, we occur exception:\n{}.", e.getMessage());
			} finally {
			    lock.unlock();
			}
		}
	}

	/**
	 * @description 更新当前WebSocket Session的心跳信息
	 * @author yida
	 * @date 2024-08-25 21:45:02
	 * @param session
	 */
	public static void updateHeartBeat(Session session){
		if (session != null && session.isOpen()){
			String sessionId = session.getId();
			long currentTimeMills = System.currentTimeMillis();
			WebSocketSession webSocketSession = webSocketSessionMap.get(sessionId);
			if(null == webSocketSession) {
				webSocketSession = new WebSocketSession(sessionId, currentTimeMills);
			} else {
				webSocketSession.setSessionId(sessionId);
				webSocketSession.setLastRecieveHeartbeatTime(currentTimeMills);
			}
			webSocketSessionMap.put(sessionId, webSocketSession);
		}
	}

	public static void addWebSocketServer(WebSocketEditorServer webSocketServer){
		if (webSocketServer != null){
			webSocketServerSet.add(webSocketServer);
			webSocketServerMap.put(webSocketServer.getSessionId(), webSocketServer);
		}
	}

	public static void removeWebSocketServer(WebSocketEditorServer webSocketServer){
		webSocketServerSet.remove(webSocketServer);
		webSocketServerMap.remove(webSocketServer.getSessionId());
	}

	public static void removeWebSocketSession(Session session) {
		String sessionId = session.getId();
		WebSocketSession webSocketSession = webSocketSessionMap.get(sessionId);
		if(null == webSocketSession) {
			return;
		}
		webSocketSessionMap.remove(sessionId);
	}

	/**
	 * 通过SessionId发送消息
	 * @param
	 * @param msg
	 */
	public static void sendMessage(String sessionId, String msg){
		Session session = webSocketServerMap.get(sessionId).getSession();
		sendMessage(session, msg);
	}

	/**
	 * 通过Session发送消息
	 * @param session
	 * @param msg
	 */
	public static void sendMessage(Session session, String msg){
		if (session == null){
			logger.error("The session doesn't exists，so we can't send message.");
			return;
		}
		if(!session.isOpen()) {
			logger.error("The sesion with sessionId:[{}] was closed, so we can't send any message to websocket client.", session.getId());
			return;
		}
		try {
			session.getBasicRemote().sendText(msg);
		} catch (Exception e) {}
	}

	/**
	 * 发送消息
	 * @param msg
	 */
	public static void sendMessage(String msg){
		for (WebSocketEditorServer webSocketServer : webSocketServerSet) {
			sendMessage(webSocketServer.getSession(), msg);
		}
		logger.info("send message:[{}] to all websocket client successfully.", msg);
	}
}
