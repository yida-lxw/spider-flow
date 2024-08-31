package org.spiderflow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.dto.SpiderJobHistoryDTO;
import org.spiderflow.core.page.PageResult;
import org.spiderflow.core.service.SpiderJobHistoryService;
import org.spiderflow.core.utils.DateUtils;
import org.spiderflow.core.utils.JacksonUtils;
import org.spiderflow.core.utils.StringUtils;
import org.spiderflow.model.JsonBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	@GetMapping("/page")
	@ResponseBody
	public JsonBean<PageResult> list(@RequestParam(name="flowId", required = false) String flowId,
										   @RequestParam(name="spiderName") String spiderName,
										   @RequestParam(name="executionStatus", required = false) Integer executionStatus,
										   @RequestParam(name="startExecutionTime", required = false) String startExecutionTime,
										   @RequestParam(name="endExecutionTime", required = false) String endExecutionTime,
										   @RequestParam(name="pageNum", defaultValue = "1") Integer pageNum,
									       @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize) {
		JsonBean jsonBean = null;
		try {
			Date startExecutionDate = null;
			Date endExecutionDate = null;
			if(StringUtils.isNotEmpty(startExecutionTime)) {
				startExecutionDate = DateUtils.parseDate(startExecutionTime, DateUtils.PATTERN_YYYY_MM_DD_HH_MM_SS);
			}
			if(StringUtils.isNotEmpty(endExecutionTime)) {
				endExecutionDate = DateUtils.parseDate(endExecutionTime, DateUtils.PATTERN_YYYY_MM_DD_HH_MM_SS);
			}
			Page<SpiderJobHistoryDTO> page = new Page(pageNum, pageSize, true);
			PageResult<SpiderJobHistoryDTO> pageResult = spiderJobHistoryService.spiderJobHistoryPageQuery(page, flowId, spiderName,
					executionStatus, startExecutionDate, endExecutionDate);
			jsonBean = JsonBean.success(pageResult);
		} catch (Exception e) {
			jsonBean = JsonBean.error("server error");
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("flowId", flowId);
			paramMap.put("spiderName", spiderName);
			paramMap.put("executionStatus", executionStatus);
			paramMap.put("startExecutionTime", startExecutionTime);
			paramMap.put("endExecutionTime", endExecutionTime);
			paramMap.put("pageNum", pageNum);
			paramMap.put("pageSize", pageSize);
			logger.error("As accessing the /spider_job_history/page interface with request parameters:{}, we occured exception:\n{}.",
					JacksonUtils.toJSONString(paramMap), e.getMessage());
		} finally {
			return jsonBean;
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
	public JsonBean batchDelete(@RequestParam("idList") List<String> idList) {
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
