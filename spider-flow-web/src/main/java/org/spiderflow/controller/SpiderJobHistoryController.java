package org.spiderflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.dto.SpiderJobHistoryDTO;
import org.spiderflow.core.service.SpiderJobHistoryService;
import org.spiderflow.core.utils.JacksonUtils;
import org.spiderflow.core.utils.StringUtils;
import org.spiderflow.model.JsonBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yida
 * @package org.spiderflow.controller
 * @date 2024-08-29 18:44
 * @description 爬虫任务历史Controller
 */
@RestController
@RequestMapping("/spider_job_history")
public class SpiderJobHistoryController {
	private static final Logger logger = LoggerFactory.getLogger(SpiderJobHistoryController.class);

	@Autowired
	private SpiderJobHistoryService spiderJobHistoryService;

	@PostMapping("/page")
	@ResponseBody
	public IPage<SpiderJobHistoryDTO> list(@RequestBody SpiderJobHistoryDTO spiderJobHistoryDTO) {
		Date startExecutionDate = spiderJobHistoryDTO.getStartExecutionTime();
		Date endExecutionDate = spiderJobHistoryDTO.getEndExecutionTime();
		Integer pageNum = spiderJobHistoryDTO.getPageNum();
		if(null == pageNum || pageNum <= 0) {
			pageNum = 1;
		}
		Integer pageSize = spiderJobHistoryDTO.getPageSize();
		if(null == pageSize || pageSize <= 0) {
			pageSize = 10;
		}
		Page<SpiderJobHistoryDTO> page = new Page(pageNum, pageSize, true);
		try {
			IPage<SpiderJobHistoryDTO> pageResult = spiderJobHistoryService.spiderJobHistoryPageQuery(page,
					spiderJobHistoryDTO.getFlowId(), spiderJobHistoryDTO.getSpiderFlowName(),
					spiderJobHistoryDTO.getExecutionStatus(), startExecutionDate, endExecutionDate);
			return pageResult;
		} catch (Exception e) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("flowId", spiderJobHistoryDTO.getFlowId());
			paramMap.put("spiderName", spiderJobHistoryDTO.getSpiderFlowName());
			paramMap.put("executionStatus", spiderJobHistoryDTO.getExecutionStatus());
			paramMap.put("startExecutionTime", spiderJobHistoryDTO.getStartExecutionTime());
			paramMap.put("endExecutionTime", spiderJobHistoryDTO.getEndExecutionTime());
			paramMap.put("pageNum", spiderJobHistoryDTO.getPageNum());
			paramMap.put("pageSize", spiderJobHistoryDTO.getPageSize());
			logger.error("As accessing the /spider_job_history/page interface with request parameters:{}, we occured exception:\n{}.",
					JacksonUtils.toJSONString(paramMap), e.getMessage());
			return page;
		}
	}

	@PostMapping("/delete")
	@ResponseBody
	public JsonBean delete(@RequestParam("id") String id) {
		if(StringUtils.isEmpty(id)) {
			return JsonBean.error("id MUST not be NULL or empty");
		}
		JsonBean jsonBean = null;
		try {
			int updaResult = spiderJobHistoryService.deleteById(id);
			jsonBean = (updaResult > 0)?JsonBean.success() : JsonBean.error();
		} catch (Exception e) {
			logger.error("As accessing the /spider_job_history/delete interface with request parameters id:{}, we occured exception:\n{}.",
					id, e.getMessage());
			jsonBean = JsonBean.error("server error");
		} finally {
		    return jsonBean;
		}
	}

	@PostMapping("/batch_delete")
	@ResponseBody
	public JsonBean batchDelete(@RequestBody List<String> idList) {
		if(null == idList || idList.size() <= 0) {
			return JsonBean.error("idList MUST not be NULL or empty");
		}
		JsonBean jsonBean = null;
		try {
			int updaResult = spiderJobHistoryService.batchDeleteByIds(idList);
			jsonBean = (updaResult > 0)?JsonBean.success() : JsonBean.error();
		} catch (Exception e) {
			logger.error("As accessing the /spider_job_history/batch_delete interface with request parameters idList:{}, we occured exception:\n{}.",
					JacksonUtils.toJSONString(idList), e.getMessage());
			jsonBean = JsonBean.error("server error");
		} finally {
			return jsonBean;
		}
	}
}
