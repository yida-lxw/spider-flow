package org.spiderflow.core.executor.shape;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.executor.ShapeExecutor;
import org.spiderflow.core.model.SpiderNode;
import org.spiderflow.core.utils.ExpressionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 定义变量执行器
 *
 * @author Administrator
 */
@Component
public class VariableExecutor implements ShapeExecutor {

	private static final String VARIABLE_NAME = "variable-name";

	private static final String VARIABLE_VALUE = "variable-value";

	private static final Logger logger = LoggerFactory.getLogger(VariableExecutor.class);

	@Override
	public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
		List<Map<String, String>> variableList = node.getListJsonValue(VARIABLE_NAME, VARIABLE_VALUE);
		for (Map<String, String> nameValue : variableList) {
			Object value = null;
			String variableName = nameValue.get(VARIABLE_NAME);
			String variableValue = nameValue.get(VARIABLE_VALUE);
			try {
				value = ExpressionUtils.execute(variableValue, variables);
				logger.debug("设置变量{}={}", variableName, value);
				context.pause(node.getNodeId(), "common", variableName, value);
			} catch (Exception e) {
				logger.error("设置变量{}出错，异常信息：{}", variableName, e);
				ExceptionUtils.wrapAndThrow(e);
			}
			variables.put(variableName, value);
		}
	}

	@Override
	public String supportShape() {
		return "variable";
	}

	@Override
	public boolean isThread() {
		return false;
	}

}
