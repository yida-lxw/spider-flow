package io.undertow.websockets.jsr;

import io.undertow.server.session.SecureRandomSessionIdGenerator;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.util.WorkerUtils;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import org.xnio.ChannelListener;
import org.xnio.IoFuture;
import org.xnio.IoUtils;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yida
 * @package io.undertow.websockets.jsr
 * @date 2024-08-25 21:28
 * @description Type your description over here.
 */
public class UndertowSession implements Session {
	private final String sessionId;
	private WebSocketChannel webSocketChannel;
	private FrameHandler frameHandler;
	private final ServerWebSocketContainer container;
	private final Principal user;
	private final WebSocketSessionRemoteEndpoint remote;
	private final Map<String, Object> attrs;
	private final Map<String, List<String>> requestParameterMap;
	private final URI requestUri;
	private final String queryString;
	private final Map<String, String> pathParameters;
	private final InstanceHandle<Endpoint> endpoint;
	private final Encoding encoding;
	private final AtomicBoolean closed = new AtomicBoolean();
	private final SessionContainer openSessions;
	private final String subProtocol;
	private final List<Extension> extensions;
	private final WebSocketClient.ConnectionBuilder clientConnectionBuilder;
	private final EndpointConfig config;
	private volatile int maximumBinaryBufferSize = 0;
	private volatile int maximumTextBufferSize = 0;
	private volatile boolean localClose;
	private int disconnectCount = 0;
	private int failedCount = 0;

	public UndertowSession(WebSocketChannel webSocketChannel, URI requestUri, Map<String, String> pathParameters, Map<String, List<String>> requestParameterMap, EndpointSessionHandler handler, Principal user, InstanceHandle<Endpoint> endpoint, EndpointConfig config, String queryString, Encoding encoding, SessionContainer openSessions, String subProtocol, List<Extension> extensions, WebSocketClient.ConnectionBuilder clientConnectionBuilder) {
		assert openSessions != null;

		this.webSocketChannel = webSocketChannel;
		this.queryString = queryString;
		this.encoding = encoding;
		this.openSessions = openSessions;
		this.clientConnectionBuilder = clientConnectionBuilder;
		this.container = handler.getContainer();
		this.user = user;
		this.requestUri = requestUri;
		this.requestParameterMap = Collections.unmodifiableMap(requestParameterMap);
		this.pathParameters = Collections.unmodifiableMap(pathParameters);
		this.config = config;
		this.remote = new WebSocketSessionRemoteEndpoint(this, encoding);
		this.endpoint = endpoint;
		this.sessionId = (new SecureRandomSessionIdGenerator()).createSessionId();
		this.attrs = Collections.synchronizedMap(new HashMap(config.getUserProperties()));
		this.extensions = extensions;
		this.subProtocol = subProtocol;
		this.setupWebSocketChannel(webSocketChannel);
	}

	@Override
	public ServerWebSocketContainer getContainer() {
		return this.container;
	}

	@Override
	public synchronized void addMessageHandler(MessageHandler messageHandler) throws IllegalStateException {
		this.frameHandler.addHandler(messageHandler);
	}

	@Override
	public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) {
		this.frameHandler.addHandler(clazz, handler);
	}

	@Override
	public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) {
		this.frameHandler.addHandler(clazz, handler);
	}

	@Override
	public synchronized Set<MessageHandler> getMessageHandlers() {
		return this.frameHandler.getHandlers();
	}

	@Override
	public synchronized void removeMessageHandler(MessageHandler messageHandler) {
		this.frameHandler.removeHandler(messageHandler);
	}

	public void setReceiveListener(ChannelListener<WebSocketChannel> handler) {
		this.webSocketChannel.getReceiveSetter().set(handler);
	}

	@Override
	public String getProtocolVersion() {
		return this.webSocketChannel.getVersion().toHttpHeaderValue();
	}

	@Override
	public String getNegotiatedSubprotocol() {
		return this.subProtocol == null ? "" : this.subProtocol;
	}

	@Override
	public boolean isSecure() {
		return this.webSocketChannel.isSecure();
	}

	@Override
	public boolean isOpen() {
		return this.webSocketChannel.isOpen();
	}

	@Override
	public long getMaxIdleTimeout() {
		return this.webSocketChannel.getIdleTimeout();
	}

	@Override
	public void setMaxIdleTimeout(long milliseconds) {
		this.webSocketChannel.setIdleTimeout(milliseconds);
	}

	@Override
	public String getId() {
		return this.sessionId;
	}

	@Override
	public void close() throws IOException {
		this.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, (String)null));
	}

	@Override
	public void close(CloseReason closeReason) throws IOException {
		this.localClose = true;
		this.closeInternal(closeReason);
	}

	public void closeInternal() throws IOException {
		this.closeInternal(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, (String)null));
	}

	public void closeInternal(CloseReason closeReason) throws IOException {
		if (this.closed.compareAndSet(false, true)) {
			boolean var15 = false;

			try {
				boolean var20 = false;

				try {
					var20 = true;
					var15 = true;
					if (!this.webSocketChannel.isCloseFrameReceived()) {
						if (!this.webSocketChannel.isCloseFrameSent()) {
							if (closeReason != null && closeReason.getCloseCode().getCode() != CloseReason.CloseCodes.NO_STATUS_CODE.getCode()) {
								WebSockets.sendClose((new CloseMessage(closeReason.getCloseCode().getCode(), closeReason.getReasonPhrase())).toByteBuffer(), this.webSocketChannel, (WebSocketCallback)null);
								var20 = false;
							} else {
								this.webSocketChannel.sendClose();
								var20 = false;
							}
						} else {
							var20 = false;
						}
					} else {
						var20 = false;
					}
				} finally {
					if (var20) {
						try {
							String reason = null;
							CloseReason.CloseCode code = CloseReason.CloseCodes.NO_STATUS_CODE;
							if (this.webSocketChannel.getCloseCode() != -1) {
								reason = this.webSocketChannel.getCloseReason();
								code = CloseReason.CloseCodes.getCloseCode(this.webSocketChannel.getCloseCode());
							} else if (closeReason != null) {
								reason = closeReason.getReasonPhrase();
								code = closeReason.getCloseCode();
							}

							if (!this.webSocketChannel.isCloseInitiatedByRemotePeer() && !this.localClose && ((CloseReason.CloseCode)code).getCode() != CloseReason.CloseCodes.TOO_BIG.getCode() && ((CloseReason.CloseCode)code).getCode() != CloseReason.CloseCodes.VIOLATED_POLICY.getCode()) {
								code = CloseReason.CloseCodes.CLOSED_ABNORMALLY;
							}

							((Endpoint)this.endpoint.getInstance()).onClose(this, new CloseReason((CloseReason.CloseCode)code, reason));
						} catch (Exception var21) {
							Exception e = var21;
							((Endpoint)this.endpoint.getInstance()).onError(this, e);
						}

					}
				}

				try {
					String reason = null;
					CloseReason.CloseCode code = CloseReason.CloseCodes.NO_STATUS_CODE;
					if (this.webSocketChannel.getCloseCode() != -1) {
						reason = this.webSocketChannel.getCloseReason();
						code = CloseReason.CloseCodes.getCloseCode(this.webSocketChannel.getCloseCode());
					} else if (closeReason != null) {
						reason = closeReason.getReasonPhrase();
						code = closeReason.getCloseCode();
					}

					if (!this.webSocketChannel.isCloseInitiatedByRemotePeer() && !this.localClose && ((CloseReason.CloseCode)code).getCode() != CloseReason.CloseCodes.TOO_BIG.getCode() && ((CloseReason.CloseCode)code).getCode() != CloseReason.CloseCodes.VIOLATED_POLICY.getCode()) {
						code = CloseReason.CloseCodes.CLOSED_ABNORMALLY;
					}

					((Endpoint)this.endpoint.getInstance()).onClose(this, new CloseReason((CloseReason.CloseCode)code, reason));
					var15 = false;
				} catch (Exception var22) {
					Exception e = var22;
					((Endpoint)this.endpoint.getInstance()).onError(this, e);
					var15 = false;
				}
			} finally {
				if (var15) {
					this.close0();
					if (this.clientConnectionBuilder != null && !this.localClose) {
						WebSocketReconnectHandler webSocketReconnectHandler = this.container.getWebSocketReconnectHandler();
						if (webSocketReconnectHandler != null) {
							JsrWebSocketLogger.REQUEST_LOGGER.debugf("Calling reconnect handler for %s", this);
							long reconnect = webSocketReconnectHandler.disconnected(closeReason, this.requestUri, this, ++this.disconnectCount);
							if (reconnect >= 0L) {
								this.handleReconnect(reconnect);
							}
						}
					}

				}
			}

			this.close0();
			if (this.clientConnectionBuilder != null && !this.localClose) {
				WebSocketReconnectHandler webSocketReconnectHandler = this.container.getWebSocketReconnectHandler();
				if (webSocketReconnectHandler != null) {
					JsrWebSocketLogger.REQUEST_LOGGER.debugf("Calling reconnect handler for %s", this);
					long reconnect = webSocketReconnectHandler.disconnected(closeReason, this.requestUri, this, ++this.disconnectCount);
					if (reconnect >= 0L) {
						this.handleReconnect(reconnect);
					}
				}
			}
		}

	}

	private void handleReconnect(long reconnect) {
		JsrWebSocketLogger.REQUEST_LOGGER.debugf("Attempting reconnect in %s ms for session %s", reconnect, this);
		WorkerUtils.executeAfter(this.webSocketChannel.getIoThread(), new Runnable() {
			public void run() {
				UndertowSession.this.clientConnectionBuilder.connect().addNotifier(new IoFuture.HandlingNotifier<WebSocketChannel, Object>() {
					public void handleDone(WebSocketChannel data, Object attachment) {
						UndertowSession.this.closed.set(false);
						UndertowSession.this.webSocketChannel = data;
						UndertowSession.this.setupWebSocketChannel(data);
						UndertowSession.this.localClose = false;
						((Endpoint)UndertowSession.this.endpoint.getInstance()).onOpen(UndertowSession.this, UndertowSession.this.config);
						UndertowSession.this.webSocketChannel.resumeReceives();
					}

					public void handleFailed(IOException exception, Object attachment) {
						long timeout = UndertowSession.this.container.getWebSocketReconnectHandler().reconnectFailed(exception, UndertowSession.this.getRequestURI(), UndertowSession.this, ++UndertowSession.this.failedCount);
						if (timeout >= 0L) {
							UndertowSession.this.handleReconnect(timeout);
						}

					}
				}, (Object)null);
			}
		}, reconnect, TimeUnit.MILLISECONDS);
	}

	public void forceClose() {
		IoUtils.safeClose(this.webSocketChannel);
	}

	@Override
	public URI getRequestURI() {
		return this.requestUri;
	}

	@Override
	public Map<String, List<String>> getRequestParameterMap() {
		return this.requestParameterMap;
	}

	@Override
	public String getQueryString() {
		return this.queryString;
	}

	@Override
	public Map<String, String> getPathParameters() {
		return this.pathParameters;
	}

	@Override
	public Map<String, Object> getUserProperties() {
		return this.attrs;
	}

	@Override
	public Principal getUserPrincipal() {
		return this.user;
	}

	@Override
	public void setMaxBinaryMessageBufferSize(int i) {
		this.maximumBinaryBufferSize = i;
	}

	@Override
	public int getMaxBinaryMessageBufferSize() {
		return this.maximumBinaryBufferSize;
	}

	@Override
	public void setMaxTextMessageBufferSize(int i) {
		this.maximumTextBufferSize = i;
	}

	@Override
	public int getMaxTextMessageBufferSize() {
		return this.maximumTextBufferSize;
	}

	@Override
	public RemoteEndpoint.Async getAsyncRemote() {
		return this.remote.getAsync();
	}

	@Override
	public RemoteEndpoint.Basic getBasicRemote() {
		return this.remote.getBasic();
	}

	@Override
	public Set<Session> getOpenSessions() {
		return new HashSet(this.openSessions.getOpenSessions());
	}

	@Override
	public List<Extension> getNegotiatedExtensions() {
		return this.extensions;
	}

	private void close0() {
		this.getExecutor().execute(new Runnable() {
			public void run() {
				try {
					UndertowSession.this.endpoint.release();
				} finally {
					try {
						UndertowSession.this.encoding.close();
					} finally {
						UndertowSession.this.openSessions.removeOpenSession(UndertowSession.this);
					}
				}

			}
		});
	}

	public Encoding getEncoding() {
		return this.encoding;
	}

	public WebSocketChannel getWebSocketChannel() {
		return this.webSocketChannel;
	}

	private void setupWebSocketChannel(WebSocketChannel webSocketChannel) {
		this.frameHandler = new FrameHandler(this, (Endpoint)this.endpoint.getInstance());
		webSocketChannel.getReceiveSetter().set(this.frameHandler);
		webSocketChannel.addCloseTask(new ChannelListener<WebSocketChannel>() {
			public void handleEvent(WebSocketChannel channel) {
				Runnable task = new Runnable() {
					public void run() {
						try {
							UndertowSession.this.closeInternal(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, (String)null));
						} catch (IOException var2) {
						}

					}
				};

				try {
					channel.getIoThread().execute(task);
				} catch (RejectedExecutionException var4) {
					task.run();
				}

			}
		});
	}

	public Executor getExecutor() {
		return this.frameHandler.getExecutor();
	}

	public boolean isSessionClosed() {
		return this.closed.get();
	}
}
