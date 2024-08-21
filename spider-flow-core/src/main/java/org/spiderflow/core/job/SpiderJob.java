package org.spiderflow.core.job;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.Spider;
import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.context.SpiderContextHolder;
import org.spiderflow.core.model.SpiderFlow;
import org.spiderflow.core.model.Task;
import org.spiderflow.core.service.SpiderFlowService;
import org.spiderflow.core.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 爬虫定时执行
 *
 * @author Administrator
 */
@Component
public class SpiderJob extends QuartzJobBean {

	@Autowired
	private Spider spider;

	@Autowired
	private SpiderFlowService spiderFlowService;

	@Autowired
	private TaskService taskService;

	private static Map<Integer, SpiderContext> contextMap = new HashMap<>();

	@Value("${spider.job.enable:true}")
	private boolean spiderJobEnable;

	@Value("${spider.workspace}")
	private String workspace;

	private static Logger logger = LoggerFactory.getLogger(SpiderJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context) {
		if (!spiderJobEnable) {
			return;
		}
		JobDataMap dataMap = context.getMergedJobDataMap();
		SpiderFlow spiderFlow = (SpiderFlow) dataMap.get(SpiderJobManager.JOB_PARAM_NAME);
		if ("1".equalsIgnoreCase(spiderFlow.getEnabled())) {
			run(spiderFlow, context.getNextFireTime());
		}
	}

	public void run(String id) {
		run(spiderFlowService.getById(id), null);
	}

	public void run(SpiderFlow spiderFlow, Date nextExecuteTime) {
		Task task = new Task();
		task.setFlowId(spiderFlow.getId());
		task.setBeginTime(new Date());
		taskService.save(task);
		run(spiderFlow, task, nextExecuteTime);
	}

	public void run(SpiderFlow spiderFlow, Task task, Date nextExecuteTime) {
		SpiderJobContext context = null;
		Date now = new Date();
		try {
			context = SpiderJobContext.create(this.workspace, spiderFlow.getId(), task.getId(), false);
			SpiderContextHolder.set(context);
			contextMap.put(task.getId(), context);
			logger.info("开始执行任务{}", spiderFlow.getName());
			spider.run(spiderFlow, context);
			logger.info("执行任务{}完毕，下次执行时间：{}", spiderFlow.getName(), nextExecuteTime == null ? null : DateFormatUtils.format(nextExecuteTime, "yyyy-MM-dd HH:mm:ss"));
		} catch (Exception e) {
			logger.error("执行任务{}出错", spiderFlow.getName(), e);
		} finally {
			if (context != null) {
				context.close();
			}
			task.setEndTime(new Date());
			taskService.saveOrUpdate(task);
			contextMap.remove(task.getId());
			SpiderContextHolder.remove();
		}
		spiderFlowService.executeCountIncrement(spiderFlow.getId(), now, nextExecuteTime);
	}

	public static SpiderContext getSpiderContext(Integer taskId) {
		return contextMap.get(taskId);
	}
}
