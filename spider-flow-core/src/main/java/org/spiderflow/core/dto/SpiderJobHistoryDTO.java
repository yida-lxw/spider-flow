package org.spiderflow.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author yida
 * @package org.spiderflow.core.dto
 * @date 2024-08-28 20:31
 * @description Type your description over here.
 */
public class SpiderJobHistoryDTO {
	private String id;

	/**爬虫流程名称*/
	private String spiderFlowName;

	/**爬虫流程id*/
	private String flowId;

	/**爬虫任务执行开始时间*/
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startExecutionTime;

	/**爬虫任务执行结束时间*/
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endExecutionTime;

	/**爬虫任务执行状态,0=未开始,1=运行中,2=已完成,3=异常中断*/
	private Integer executionStatus;

	/**当前页码(从1开始计算)*/
	private Integer pageNum;

	/**每页显示条数*/
	private Integer pageSize;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpiderFlowName() {
		return spiderFlowName;
	}

	public void setSpiderFlowName(String spiderFlowName) {
		this.spiderFlowName = spiderFlowName;
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

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
