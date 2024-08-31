package org.spiderflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.spiderflow.core.dto.SpiderJobHistoryDTO;
import org.spiderflow.core.model.SpiderJobHistory;
import org.spiderflow.core.page.PageResult;
import org.spiderflow.core.service.SpiderJobHistoryService;
import org.spiderflow.mapper.SpiderJobHistoryMapper;
import org.spiderflow.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		IPage<SpiderJobHistoryDTO> pageInfo = spiderJobHistoryMapper.selectPage(page, flowId, spiderName, executionStatus, startExecutionTime, endExecutionTime);
		return pageInfo;
	}

	@Override
	@Transactional
	public int insertSpiderJobHistory(SpiderJobHistory spiderJobHistory) {
		return spiderJobHistoryMapper.insertSpiderJobHistory(spiderJobHistory);
	}

	@Override
	@Transactional
	public int updateSpiderJobHistory(SpiderJobHistory spiderJobHistory) {
		return spiderJobHistoryMapper.updateSpiderJobHistory(spiderJobHistory);
	}

	@Override
	@Transactional
	public int updateExecutionStatus(String id, Integer executionStatus) {
		return spiderJobHistoryMapper.updateExecutionStatus(id, executionStatus);
	}

	@Override
	@Transactional
	public int updateStartExecutionTime(String id) {
		return spiderJobHistoryMapper.updateStartExecutionTime(id);
	}

	@Override
	@Transactional
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

	@Override
	@Transactional
	public int deleteById(String id) {
		return spiderJobHistoryMapper.deleteById(id);
	}

	@Override
	@Transactional
	public int deleteByFlowId(String flowId) {
		return spiderJobHistoryMapper.deleteByFlowId(flowId);
	}

	@Override
	@Transactional
	public int batchDeleteByIds(List<String> idList) {
		return spiderJobHistoryMapper.batchDeleteByIds(idList);
	}
}
