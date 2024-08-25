package org.spiderflow.core.job;

/**
 * @author yida
 * @package org.spiderflow.core.job
 * @date 2024-08-25 23:57
 * @description 爬虫节点执行状态Bean
 */
public class SpiderJobNodeStatusInfo {
	/**爬虫任务流程id*/
	private String flowId;
	/**爬虫任务实例id*/
	private String instanceId;
	/**当前节点id*/
	private String nodeId;
	/**是否正在执行*/
	private boolean running;
	/**是否已执行完成*/
	private boolean hadCompleted;

	public SpiderJobNodeStatusInfo() {}

	public SpiderJobNodeStatusInfo(String flowId, String instanceId, String nodeId, boolean running, boolean hadCompleted) {
		this.flowId = flowId;
		this.instanceId = instanceId;
		this.nodeId = nodeId;
		this.running = running;
		this.hadCompleted = hadCompleted;
	}

	public SpiderJobNodeStatusInfo(String flowId, String instanceId, String nodeId) {
		this.flowId = flowId;
		this.instanceId = instanceId;
		this.nodeId = nodeId;
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

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isHadCompleted() {
		return hadCompleted;
	}

	public void setHadCompleted(boolean hadCompleted) {
		this.hadCompleted = hadCompleted;
	}
}
