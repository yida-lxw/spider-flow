package org.spiderflow.core.job;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.Spider;
import org.spiderflow.core.model.SpiderFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 爬虫定时执行管理
 *
 * @author Administrator
 */
@Component
public class SpiderJobManager {
	private static Logger logger = LoggerFactory.getLogger(SpiderJobManager.class);
	private final static String JOB_NAME = "SPIDER_TASK_";
	public final static String JOB_PARAM_NAME = "SPIDER_FLOW";

	@Lazy
	@Autowired
	private SpiderScheduledJob spiderScheduledJob;

	/**
	 * 调度器
	 */
	@Autowired
	private Scheduler scheduler;

	private JobKey getJobKey(String id) {
		return JobKey.jobKey(JOB_NAME + id);
	}

	private TriggerKey getTriggerKey(String id) {
		return TriggerKey.triggerKey(JOB_NAME + id);
	}

	/**
	 * 新建定时任务
	 *
	 * @param spiderFlow 爬虫流程图
	 * @return boolean true/false
	 */
	public Date addJob(SpiderFlow spiderFlow) {
		try {
			JobDetail job = JobBuilder.newJob(SpiderScheduledJob.class).withIdentity(getJobKey(spiderFlow.getId())).build();
			job.getJobDataMap().put(JOB_PARAM_NAME, spiderFlow);
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(spiderFlow.getCron()).withMisfireHandlingInstructionDoNothing();
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(spiderFlow.getId())).withSchedule(cronScheduleBuilder).build();
			return scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			logger.error("创建定时任务出错", e);
			return null;
		}
	}

	public void run(String id) {
		Spider.executorInstance.submit(() -> {
			spiderScheduledJob.run(id);
		});
	}

	public boolean remove(String id) {
		try {
			scheduler.deleteJob(getJobKey(id));
			return true;
		} catch (SchedulerException e) {
			logger.error("删除定时任务失败", e);
			return false;
		}
	}
}
