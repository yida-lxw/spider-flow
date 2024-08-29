package org.spiderflow.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.dto.SpiderJobHistoryDTO;
import org.spiderflow.core.page.PageResult;
import org.spiderflow.core.service.SpiderJobHistoryService;
import org.spiderflow.core.utils.DateUtils;
import org.spiderflow.core.utils.StringUtils;
import org.spiderflow.model.JsonBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author yida
 * @package org.spiderflow.controller
 * @date 2024-08-29 18:44
 * @description Type your description over here.
 */
@RestController
@RequestMapping("/spider_job_history")
public class SpiderJobHistoryController {
	private static final Logger logger = LoggerFactory.getLogger(SpiderJobHistoryController.class);

	@Autowired
	private SpiderJobHistoryService spiderJobHistoryService;

	@GetMapping("/page")
	@ResponseBody
	public JsonBean<PageResult> list(@RequestParam("flowId") String flowId,
										   @RequestParam("spiderName") String spiderName,
										   @RequestParam("executionStatus") Integer executionStatus,
										   @RequestParam("startExecutionTime") String startExecutionTime,
										   @RequestParam("endExecutionTime") String endExecutionTime,
										   @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
		Date startExecutionDate = null;
		Date endExecutionDate = null;
		if(StringUtils.isNotEmpty(startExecutionTime)) {
			startExecutionDate = DateUtils.parseDate(startExecutionTime, DateUtils.PATTERN_YYYY_MM_DD_HH_MM_SS);
		}
		if(StringUtils.isNotEmpty(endExecutionTime)) {
			endExecutionDate = DateUtils.parseDate(endExecutionTime, DateUtils.PATTERN_YYYY_MM_DD_HH_MM_SS);
		}
		Page<SpiderJobHistoryDTO> page = PageHelper.startPage(pageNum, pageSize);
		PageResult<SpiderJobHistoryDTO> pageResult = spiderJobHistoryService.spiderJobHistoryPageQuery(page, flowId, spiderName,
				executionStatus, startExecutionDate, endExecutionDate);
		JsonBean jsonBean = JsonBean.success(pageResult);
		return jsonBean;
	}
}
