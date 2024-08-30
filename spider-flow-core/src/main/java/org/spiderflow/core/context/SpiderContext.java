package org.spiderflow.core.context;

import org.spiderflow.core.model.SpiderNode;
import org.spiderflow.core.model.SpiderOutput;
import org.spiderflow.core.executor.thread.pool.SubThreadPoolExecutor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 爬虫上下文
 *
 * @author jmxd
 */
public class SpiderContext extends HashMap<String, Object> {
	private static final long serialVersionUID = 8379165378417619790L;

	private String id = UUID.randomUUID().toString().replace("-", "");

	protected  String instanceId;

	/**
	 * 流程ID
	 */
	protected String flowId;

	/**当前执行节点id*/
	protected String currentNodeId;

	/**
	 * 流程执行线程
	 */
	private SubThreadPoolExecutor threadPool;

	/**
	 * 根节点
	 */
	private SpiderNode rootNode;

	/**
	 * 爬虫是否运行中
	 */
	protected volatile boolean running = true;

	/**
	 * Future队列
	 */
	private LinkedBlockingQueue<Future<?>> futureQueue = new LinkedBlockingQueue<>();

	/**
	 * Cookie上下文
	 */
	private CookieContext cookieContext = new CookieContext();

	public List<SpiderOutput> getOutputs() {
		return Collections.emptyList();
	}

	public <T> T get(String key) {
		return (T) super.get(key);
	}

	public <T> T get(String key, T defaultValue) {
		T value = this.get(key);
		return value == null ? defaultValue : value;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public LinkedBlockingQueue<Future<?>> getFutureQueue() {
		return futureQueue;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void addOutput(SpiderOutput output) {

	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getCurrentNodeId() {
		return currentNodeId;
	}

	public void setCurrentNodeId(String currentNodeId) {
		this.currentNodeId = currentNodeId;
	}

	public SubThreadPoolExecutor getThreadPool() {
		return threadPool;
	}

	public void setThreadPool(SubThreadPoolExecutor threadPool) {
		this.threadPool = threadPool;
	}

	public SpiderNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(SpiderNode rootNode) {
		this.rootNode = rootNode;
	}

	public String getId() {
		return id;
	}

	public CookieContext getCookieContext() {
		return cookieContext;
	}

	public void pause(String nodeId, String event, String key, Object value) {
	}

	public void resume() {
	}

	public void stop() {
	}
}
