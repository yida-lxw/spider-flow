package org.spiderflow.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.spiderflow.core.model.SpiderFlow;

import java.util.Date;
import java.util.List;

/**
 * @author yida
 * @package org.spiderflow.core.service
 * @date 2024-08-21 17:03
 * @description Type your description over here.
 */
public interface SpiderFlowService extends IService<SpiderFlow> {
	IPage<SpiderFlow> selectSpiderPage(Page<SpiderFlow> page, String name);

	int executeCountIncrement(String id, Date lastExecuteTime, Date nextExecuteTime);

	List<SpiderFlow> selectOtherFlows(String id);

	List<SpiderFlow> selectFlows();

	List<String> getRecentTriggerTime(String cron, int numTimes);

	List<Long> historyList(String id);

	String readHistory(String id, String timestamp);

	Integer getFlowMaxTaskId(String flowId);

	void resetCornExpression(String id, String cron);

	boolean save(SpiderFlow spiderFlow);

	void stop(String id);

	void copy(String id);

	void start(String id);

	void run(String id);

	void resetExecuteCount(String id);

	void remove(String id);
}
