package org.spiderflow.core.job;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.Spider;
import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.context.SpiderContextHolder;
import org.spiderflow.core.job.id.IdGenerator;
import org.spiderflow.core.job.id.IdGeneratorFactory;
import org.spiderflow.core.job.id.IdGeneratorStrategy;
import org.spiderflow.core.model.SpiderFlow;
import org.spiderflow.core.model.SpiderJobHistory;
import org.spiderflow.core.service.SpiderFlowService;
import org.spiderflow.core.service.SpiderJobHistoryService;
import org.spiderflow.core.thread.GlobalThreadPool;
import org.spiderflow.core.utils.StringUtils;
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
	private static final Logger logger = LoggerFactory.getLogger(SpiderJob.class);

	private static Map<String, SpiderContext> contextMap = new HashMap<>();

	private IdentifierGenerator identifierGenerator = new DefaultIdentifierGenerator();

	@Autowired
	private Spider spider;


	@Autowired
	private SpiderFlowService spiderFlowService;

	@Autowired
	private SpiderJobHistoryService spiderJobHistoryService;


	@Value("${spider.job.enable:true}")
	private boolean spiderJobEnable;

	@Value("${spider.workspace}")
	private String workspace;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		if (!spiderJobEnable) {
			return;
		}
		JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
		SpiderFlow spiderFlow = (SpiderFlow) dataMap.get(SpiderJobManager.JOB_PARAM_NAME);
		if ("1".equalsIgnoreCase(spiderFlow.getEnabled())) {
			run(spiderFlow, jobExecutionContext.getNextFireTime());
		}
	}

	public void run(String id) {
		run(spiderFlowService.getById(id), null);
	}

	public void run(SpiderFlow spiderFlow, Date nextExecuteTime) {
		//MyBatis需要这个 shit Object
		Object shitObject = new Object();
		String jobHistoryId = identifierGenerator.nextId(shitObject).toString();
		SpiderJobHistory spiderJobHistory = new SpiderJobHistory();
		spiderJobHistory.setId(jobHistoryId);
		spiderJobHistory.setFlowId(spiderFlow.getId());
		spiderJobHistory.setStartExecutionTime(new Date());
		spiderJobHistory.setExecutionStatus(1);
		GlobalThreadPool.getInstance().execute(() -> {
			int insertResult = spiderJobHistoryService.insertSpiderJobHistory(spiderJobHistory);
			if(insertResult > 0) {
				logger.info("insert SpiderJobHistory:[{}] into sp_job_history table successlly.", jobHistoryId);
			}
		});
		run(spiderFlow, spiderJobHistory, nextExecuteTime);
	}

	public void run(SpiderFlow spiderFlow, SpiderJobHistory spiderJobHistory, Date nextExecuteTime) {
		SpiderJobContext context = null;
		Date now = new Date();
		try {
			String jobHistoryId = spiderJobHistory.getId();
			context = SpiderJobContext.create(this.workspace, spiderFlow.getId(), jobHistoryId, false);
			context.setJobHistoryId(jobHistoryId);
			SpiderContextHolder.set(context);
			contextMap.put(spiderJobHistory.getId(), context);
			logger.info("开始执行任务{}", spiderFlow.getName());
			spider.run(spiderFlow, context);
			logger.info("执行任务{}完毕，下次执行时间：{}", spiderFlow.getName(), nextExecuteTime == null ? null : DateFormatUtils.format(nextExecuteTime, "yyyy-MM-dd HH:mm:ss"));
			GlobalThreadPool.getInstance().execute(() -> {
				spiderJobHistory.setEndExecutionTime(new Date());
				spiderJobHistory.setExecutionStatus(2);
				spiderJobHistoryService.updateSpiderJobHistory(spiderJobHistory);
			});
		} catch (Exception e) {
			logger.error("执行任务{}出错", spiderFlow.getName(), e);
			GlobalThreadPool.getInstance().execute(() -> {
				spiderJobHistory.setEndExecutionTime(new Date());
				spiderJobHistory.setExecutionStatus(3);
				spiderJobHistoryService.updateSpiderJobHistory(spiderJobHistory);
			});
		} finally {
			if (context != null) {
				context.close();
			}
			contextMap.remove(spiderJobHistory.getId());
			SpiderContextHolder.remove();
		}
		spiderFlowService.executeCountIncrement(spiderFlow.getId(), now, nextExecuteTime);
	}

	private String buildInstanceId(String idGeneratorStrategyName, long workerId, long dataCenterId) {
		IdGeneratorStrategy idGeneratorStrategy = IdGeneratorStrategy.of(idGeneratorStrategyName);
		IdGenerator<String> idGenerator = IdGeneratorFactory.build(idGeneratorStrategy, workerId, dataCenterId);
		String instanceId = idGenerator.nextId();
		return instanceId;
	}

	public static SpiderContext getSpiderContext(String historyId) {
		return contextMap.get(historyId);
	}

	public IdentifierGenerator getIdentifierGenerator() {
		return identifierGenerator;
	}
}
