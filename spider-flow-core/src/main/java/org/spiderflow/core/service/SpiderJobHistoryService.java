package org.spiderflow.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.Page;
import org.spiderflow.core.dto.SpiderJobHistoryDTO;
import org.spiderflow.core.model.SpiderJobHistory;
import org.spiderflow.core.page.PageResult;

import java.util.Date;
import java.util.List;

/**
 * @author yida
 * @package org.spiderflow.core.service
 * @date 2024-08-29 07:26
 * @description Type your description over here.
 */
public interface SpiderJobHistoryService  extends IService<SpiderJobHistory> {
	PageResult<SpiderJobHistoryDTO> spiderJobHistoryPageQuery(Page<SpiderJobHistoryDTO> page, String flowId,
															  String spiderName,
															  Integer executionStatus,
															  Date startExecutionTime,
															  Date endExecutionTime);

	String insertSpiderFlow(SpiderJobHistory spiderJobHistory);

	int updateSpiderFlow(SpiderJobHistory spiderJobHistory);

	int updateExecutionStatus(String id, Integer executionStatus);

	int updateStartExecutionTime(String id);

	int updateEndExecutionTime(String id);

	SpiderJobHistory queryById(String id);

	List<SpiderJobHistory> queryByFlowId(String flowId);

	SpiderJobHistory queryLastJobHistory(String flowId);
}