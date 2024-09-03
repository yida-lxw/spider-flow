package org.spiderflow.core.job;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yida
 * @package org.spiderflow.core.job
 * @date 2024-09-03 20:03
 * @description 爬虫节点执行状态(用于爬虫中断后恢复)
 */
public class SpiderJobNodeExecutionStatus {
	/**
	 * 爬虫任务流程id
	 */
	private String flowId;
	/**
	 * 爬虫任务实例id
	 */
	private String instanceId;
	/**
	 * 当前节点id
	 */
	private String nodeId;
	/**
	 * 下一节点id
	 */
	private String nextNodeId;
	/**
	 * 当前节点是否已经执行完毕
	 */
	private boolean completed;
	/**
	 * 需要循环执行的次数
	 */
	private int loopTimes;
	/**
	 * 当前节点已循环执行的次数
	 */
	private AtomicInteger hadLoopTimes;

	/**
	 * 当前节点的Json属性
	 */
	private Map<String, Object> jsonProperty;

	/**
	 * 当前节点的流转条件
	 */
	private Map<String, String> condition;

	/**
	 * 当前节点的异常流转
	 */
	private Map<String, String> exception;

	/**
	 * 当前节点需要往下一节点传递的变量
	 */
	private Map<String, String> transmitVariable;

	public SpiderJobNodeExecutionStatus() {
		this.hadLoopTimes = new AtomicInteger(0);
		this.jsonProperty = new HashMap<>();
		this.condition = new HashMap<>();
		this.exception = new HashMap<>();
		this.transmitVariable = new HashMap<>();
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNextNodeId() {
		return nextNodeId;
	}

	public void setNextNodeId(String nextNodeId) {
		this.nextNodeId = nextNodeId;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public int getLoopTimes() {
		return loopTimes;
	}

	public void setLoopTimes(int loopTimes) {
		this.loopTimes = loopTimes;
	}

	public AtomicInteger getHadLoopTimes() {
		return hadLoopTimes;
	}

	public void setHadLoopTimes(AtomicInteger hadLoopTimes) {
		this.hadLoopTimes = hadLoopTimes;
	}

	public Map<String, Object> getJsonProperty() {
		return jsonProperty;
	}

	public void setJsonProperty(Map<String, Object> jsonProperty) {
		this.jsonProperty = jsonProperty;
	}

	public Map<String, String> getCondition() {
		return condition;
	}

	public void setCondition(Map<String, String> condition) {
		this.condition = condition;
	}

	public Map<String, String> getException() {
		return exception;
	}

	public void setException(Map<String, String> exception) {
		this.exception = exception;
	}

	public Map<String, String> getTransmitVariable() {
		return transmitVariable;
	}

	public void setTransmitVariable(Map<String, String> transmitVariable) {
		this.transmitVariable = transmitVariable;
	}
}
