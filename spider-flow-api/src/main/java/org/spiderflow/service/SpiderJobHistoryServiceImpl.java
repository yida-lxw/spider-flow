package org.spiderflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.spiderflow.core.dto.SpiderJobHistoryDTO;
import org.spiderflow.core.model.SpiderJobHistory;
import org.spiderflow.core.service.SpiderJobHistoryService;
import org.spiderflow.mapper.SpiderJobHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author yida
 * @package org.spiderflow.service
 * @date 2024-08-29 07:30
 * @description Type your description over here.
 */
@Service
public class SpiderJobHistoryServiceImpl extends ServiceImpl<SpiderJobHistoryMapper, SpiderJobHistory> implements SpiderJobHistoryService {
	@Autowired
	private SpiderJobHistoryMapper spiderJobHistoryMapper;

	@Override
	public IPage<SpiderJobHistoryDTO> spiderJobHistoryPageQuery(Page<SpiderJobHistoryDTO> page, String flowId, String spiderName,
																Integer executionStatus, Date startExecutionTime, Date endExecutionTime) {
		return spiderJobHistoryMapper.spiderJobHistoryPageQuery(page, flowId, spiderName, executionStatus, startExecutionTime, endExecutionTime);
	}

	@Override
	public String insertSpiderFlow(SpiderJobHistory spiderJobHistory) {
		return spiderJobHistoryMapper.insertSpiderFlow(spiderJobHistory);
	}

	@Override
	public int updateSpiderFlow(SpiderJobHistory spiderJobHistory) {
		return spiderJobHistoryMapper.updateSpiderFlow(spiderJobHistory);
	}

	@Override
	public int updateExecutionStatus(String id, Integer executionStatus) {
		return spiderJobHistoryMapper.updateExecutionStatus(id, executionStatus);
	}

	@Override
	public int updateStartExecutionTime(String id) {
		return spiderJobHistoryMapper.updateStartExecutionTime(id);
	}

	@Override
	public int updateEndExecutionTime(String id) {
		return spiderJobHistoryMapper.updateEndExecutionTime(id);
	}

	@Override
	public SpiderJobHistory queryById(String id) {
		return spiderJobHistoryMapper.queryById(id);
	}

	@Override
	public List<SpiderJobHistory> queryByFlowId(String flowId) {
		return spiderJobHistoryMapper.queryByFlowId(flowId);
	}

	@Override
	public SpiderJobHistory queryLastJobHistory(String flowId) {
		return spiderJobHistoryMapper.queryLastJobHistory(flowId);
	}
}
