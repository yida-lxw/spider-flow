package org.spiderflow.controller;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.Spider;
import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.job.SpiderJob;
import org.spiderflow.core.job.SpiderJobContext;
import org.spiderflow.core.model.SpiderFlow;
import org.spiderflow.core.model.SpiderJobHistory;
import org.spiderflow.core.model.SpiderOutput;
import org.spiderflow.core.service.SpiderJobHistoryService;
import org.spiderflow.core.thread.GlobalThreadPool;
import org.spiderflow.model.JsonBean;
import org.spiderflow.service.SpiderFlowServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class SpiderRestController {

	private static Logger logger = LoggerFactory.getLogger(SpiderRestController.class);

	@Autowired
	private SpiderFlowServiceImpl spiderFlowService;

	@Autowired
	private Spider spider;

	@Value("${spider.workspace}")
	private String workspace;

	@Autowired
	private SpiderJob spiderJob;

	@Autowired
	private SpiderJobHistoryService spiderJobHistoryService;

	/**
	 * 异步运行
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("/runAsync/{id}")
	public JsonBean<String> runAsync(@PathVariable("id") String id) {
		SpiderFlow flow = spiderFlowService.getById(id);
		if (flow == null) {
			return new JsonBean<>(0, "找不到此爬虫信息");
		}
		Object shitObject = new Object();
		IdentifierGenerator identifierGenerator = spiderJob.getIdentifierGenerator();
		String jobHistoryId = identifierGenerator.nextId(shitObject).toString();
		SpiderJobHistory spiderJobHistory = new SpiderJobHistory();
		spiderJobHistory.setId(jobHistoryId);
		spiderJobHistory.setFlowId(id);
		spiderJobHistory.setStartExecutionTime(new Date());
		spiderJobHistory.setExecutionStatus(1);
		GlobalThreadPool.getInstance().execute(() -> {
			int insertResult = spiderJobHistoryService.insertSpiderJobHistory(spiderJobHistory);
			if(insertResult > 0) {
				logger.info("insert SpiderJobHistory:[{}] into sp_job_history table successlly.", jobHistoryId);
			}
		});
		Spider.executorInstance.submit(() -> {
			spiderJob.run(flow, spiderJobHistory, null);
		});
		return new JsonBean<>(jobHistoryId);
	}

	/**
	 * 停止运行任务
	 *
	 * @param jobHistoryId
	 */
	@RequestMapping("/stop/{jobHistoryId}")
	public JsonBean<Void> stop(@PathVariable("jobHistoryId") String jobHistoryId) {
		SpiderContext context = SpiderJob.getSpiderContext(jobHistoryId);
		if (context == null) {
			return new JsonBean<>(0, "任务不存在！");
		}
		context.setRunning(false);
		return new JsonBean<>(1, "停止成功！");
	}

	@RequestMapping("/remove")
	public JsonBean<Boolean> remove(String jobHistoryId) {
		//删除任务记录之前先停止
		SpiderContext context = SpiderJob.getSpiderContext(jobHistoryId);
		if (context != null) {
			context.setRunning(false);
		}
		int deleteResut = spiderJobHistoryService.deleteById(jobHistoryId);
		return new JsonBean<>(deleteResut > 0);
	}

	/**
	 * 查询任务状态
	 *
	 * @param jobHistoryId
	 */
	@RequestMapping("/status/{jobHistoryId}")
	public JsonBean<Integer> status(@PathVariable("jobHistoryId") String jobHistoryId) {
		SpiderContext context = SpiderJob.getSpiderContext(jobHistoryId);
		if (context == null) {
			return new JsonBean<>(0);
		}
		return new JsonBean<>(1);
	}

	/**
	 * 同步运行
	 *
	 * @param id
	 * @param params
	 * @return
	 */
	@RequestMapping("/run/{id}")
	public JsonBean<List<SpiderOutput>> run(@PathVariable("id") String id, @RequestBody(required = false) Map<String, Object> params) {
		SpiderFlow flow = spiderFlowService.getById(id);
		if (flow == null) {
			return new JsonBean<>(0, "找不到此爬虫信息");
		}
		String flowId = flow.getId();
		List<SpiderOutput> outputs;
		Object shitObject = new Object();
		IdentifierGenerator identifierGenerator = spiderJob.getIdentifierGenerator();
		String jobHistoryId = identifierGenerator.nextId(shitObject).toString();
		SpiderJobHistory spiderJobHistory = new SpiderJobHistory();
		spiderJobHistory.setId(jobHistoryId);
		spiderJobHistory.setFlowId(flowId);
		spiderJobHistory.setStartExecutionTime(new Date());
		spiderJobHistory.setExecutionStatus(1);
		GlobalThreadPool.getInstance().execute(() -> {
			int insertResult = spiderJobHistoryService.insertSpiderJobHistory(spiderJobHistory);
			if(insertResult > 0) {
				logger.info("insert SpiderJobHistory:[{}] into sp_job_history table successlly.", jobHistoryId);
			}
		});

		SpiderJobContext spiderJobContext = SpiderJobContext.create(workspace, id, jobHistoryId, true);
		spiderJobContext.setFlowId(flowId);
		try {
			outputs = spider.run(flow, spiderJobContext, params);
		} catch (Exception e) {
			logger.error("执行爬虫失败", e);
			return new JsonBean<>(-1, "执行失败");
		} finally {
			spiderJobContext.close();
		}
		return new JsonBean<>(outputs);
	}

}
