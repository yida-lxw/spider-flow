package org.spiderflow.core.executor.shape;

import org.apache.commons.lang3.StringUtils;
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
 * 函数执行器
 *
 * @author Administrator
 */
@Component
public class FunctionExecutor implements ShapeExecutor {

	public static final String FUNCTION = "function";

	private static final Logger logger = LoggerFactory.getLogger(FunctionExecutor.class);

	@Override
	public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
		List<Map<String, String>> functions = node.getListJsonValue(FUNCTION);
		for (Map<String, String> item : functions) {
			String function = item.get(FUNCTION);
			if (StringUtils.isNotBlank(function)) {
				try {
					logger.debug("执行函数{}", function);
					ExpressionUtils.execute(function, variables);
				} catch (Exception e) {
					logger.error("执行函数{}失败,异常信息:{}", function, e);
					ExceptionUtils.wrapAndThrow(e);
				}
			}
		}
	}

	@Override
	public String supportShape() {
		return "function";
	}

}
