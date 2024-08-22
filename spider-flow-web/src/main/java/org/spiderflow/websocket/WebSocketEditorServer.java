package org.spiderflow.websocket;

import org.spiderflow.core.Spider;
import org.spiderflow.core.utils.JacksonUtils;
import org.spiderflow.core.utils.SpiderFlowUtils;
import org.spiderflow.model.SpiderWebSocketContext;
import org.spiderflow.model.WebSocketEvent;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
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

	public static Spider spider;

	private SpiderWebSocketContext context;

	@OnMessage
	public void onMessage(String message, Session session) {
		Map<String, Object> event = JacksonUtils.json2Map(message);
		Object eventTypeObj = event.get("eventType");
		String eventType = null;
		if (null != eventTypeObj) {
			eventType = eventTypeObj.toString();
		}
		boolean isDebug = "debug".equalsIgnoreCase(eventType);
		if ("test".equalsIgnoreCase(eventType) || isDebug) {
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
					spider.runWithTest(SpiderFlowUtils.loadXMLFromString(xml), context);
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
	}
}
