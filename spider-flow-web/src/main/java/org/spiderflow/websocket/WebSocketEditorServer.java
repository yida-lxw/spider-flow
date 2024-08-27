package org.spiderflow.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.Spider;
import org.spiderflow.core.job.id.IdGenerator;
import org.spiderflow.core.job.id.IdGeneratorFactory;
import org.spiderflow.core.job.id.IdGeneratorStrategy;
import org.spiderflow.core.model.SpiderNode;
import org.spiderflow.core.utils.JacksonUtils;
import org.spiderflow.core.utils.SpiderFlowUtils;
import org.spiderflow.model.SpiderWebSocketContext;
import org.spiderflow.model.WebSocketEvent;
import org.spiderflow.utils.XMLUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;

/**
 * WebSocket通讯编辑服务
 *
 * @author Administrator
 */
@ServerEndpoint("/ws")
@Component
public class WebSocketEditorServer {
	private static Logger logger = LoggerFactory.getLogger(WebSocketEditorServer.class);
	private Session session;

	public static Spider spider;

	private SpiderWebSocketContext context;

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		WebSocketManager.sendMessage(session, "connected");
		WebSocketManager.addWebSocketServer(this);
		WebSocketManager.updateHeartBeat(session);
		logger.info("Establish a WebSocket connection with the client with sessionId:[{}] successfully.", session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		if("ping".equals(message)) {
			String sessionId = session.getId();
			logger.info("Recieved the heartbeat packet from the websocket client with sessionId:[{}].", sessionId);
			WebSocketManager.updateHeartBeat(session);
			if(session.isOpen()) {
				WebSocketManager.sendMessage(sessionId, "pong");
			}
			return;
		}
		Map<String, Object> event = JacksonUtils.json2Map(message);
		Object eventTypeObj = event.get("eventType");
		String eventType = null;
		if (null != eventTypeObj) {
			eventType = eventTypeObj.toString();
		}
		boolean isDebug = "debug".equalsIgnoreCase(eventType);
		if ("test".equalsIgnoreCase(eventType) || isDebug) {
			String idGeneratorStrategyName = spider.getIdGeneratorStrategyName();
			long workerId = spider.getWorkerId();
			long dataCenterId = spider.getDataCenterId();
			IdGeneratorStrategy idGeneratorStrategy = IdGeneratorStrategy.of(idGeneratorStrategyName);
			IdGenerator<String> idGenerator = IdGeneratorFactory.build(idGeneratorStrategy, workerId, dataCenterId);
			context = new SpiderWebSocketContext(session);
			context.setDebug(isDebug);
			context.setRunning(true);
			new Thread(() -> {
				Object messageObj = event.get("message");
				String xml = null;
				if (null != messageObj) {
					xml = messageObj.toString();
				}
				if (xml != null && xml.length() > 0) {
					String flowId = XMLUtils.getFlowIdFromXML(xml);
					context.setFlowId(flowId);
					String instanceId = idGenerator.nextId();
					context.setInstanceId(instanceId);
					SpiderNode spiderNode = SpiderFlowUtils.loadXMLFromString(xml);
					String currentNodeId = spiderNode.getNodeId();
					context.setCurrentNodeId(currentNodeId);
					spider.runWithTest(spiderNode, context);
					context.write(new WebSocketEvent<>("finish", null));
				} else {
					context.write(new WebSocketEvent<>("error", "xml不正确！"));
				}
				context.setRunning(false);
			}).start();
		} else if ("stop".equals(eventType) && context != null) {
			context.setRunning(false);
			context.stop();
		} else if ("resume".equalsIgnoreCase(eventType) && context != null) {
			context.resume();
		}
	}

	@OnClose
	public void onClose(Session session) {
		context.setRunning(false);
		context.stop();
		WebSocketManager.removeWebSocketServer(this);
		WebSocketManager.removeWebSocketSession(session);
		logger.info("WebSocket connection was closed.");
	}

	@OnError
	public void onError(Session session, Throwable error) {
		logger.info("Establish a WebSocket connection with the client with sessionId:[{}] failed.", session.getId());
	}

	public Session getSession() {
		return session;
	}

	public String getSessionId() {
		return session.getId();
	}
}
