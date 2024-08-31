package org.spiderflow.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * @author yida
 * @package org.spiderflow.core.model
 * @date 2024-08-28 19:49
 * @description 爬虫任务历史
 */
@TableName("sp_job_history")
public class SpiderJobHistory {
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**爬虫流程id*/
	private String flowId;

	/**爬虫任务执行开始时间*/
	private Date startExecutionTime;

	/**爬虫任务执行结束时间*/
	private Date endExecutionTime;

	/**爬虫任务执行状态,0=未开始,1=运行中,2=已完成,3=异常中断*/
	private Integer executionStatus;
	public SpiderJobHistory() {}

	public SpiderJobHistory(String flowId, Date startExecutionTime, Integer executionStatus) {
		this(flowId, startExecutionTime, null, executionStatus);
	}

	public SpiderJobHistory(String flowId, Date startExecutionTime, Date endExecutionTime, Integer executionStatus) {
		this.flowId = flowId;
		this.startExecutionTime = startExecutionTime;
		this.endExecutionTime = endExecutionTime;
		this.executionStatus = executionStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public Date getStartExecutionTime() {
		return startExecutionTime;
	}

	public void setStartExecutionTime(Date startExecutionTime) {
		this.startExecutionTime = startExecutionTime;
	}

	public Date getEndExecutionTime() {
		return endExecutionTime;
	}

	public void setEndExecutionTime(Date endExecutionTime) {
		this.endExecutionTime = endExecutionTime;
	}

	public Integer getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(Integer executionStatus) {
		this.executionStatus = executionStatus;
	}
}
