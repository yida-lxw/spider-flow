package org.spiderflow.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.Spider;
import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.executor.ShapeExecutor;
import org.spiderflow.core.job.id.IdGenerator;
import org.spiderflow.core.model.SpiderFlow;
import org.spiderflow.core.model.SpiderNode;
import org.spiderflow.core.utils.SpiderFlowUtils;
import org.spiderflow.service.SpiderFlowServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 子流程执行器
 *
 * @author Administrator
 */
@Component
public class ProcessExecutor implements ShapeExecutor {

	public static final String FLOW_ID = "flowId";

	private static Logger logger = LoggerFactory.getLogger(ProcessExecutor.class);

	@Autowired
	private SpiderFlowServiceImpl spiderFlowService;

	@Autowired
	private Spider spider;

	@Override
	public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables, IdGenerator<String> idGenerator) {
		String flowId = node.getStringJsonValue("flowId");
		SpiderFlow spiderFlow = spiderFlowService.getById(flowId);
		if (spiderFlow != null) {
			logger.info("执行子流程:{}", spiderFlow.getName());
			SpiderNode root = SpiderFlowUtils.loadXMLFromString(spiderFlow.getXml());
			spider.executeNode(null, root, context, variables, idGenerator);
		} else {
			logger.info("执行子流程:{}失败，找不到该子流程", flowId);
		}
	}

	@Override
	public String supportShape() {
		return "process";
	}

}
