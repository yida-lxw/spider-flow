package org.spiderflow.core.executor.shape;

import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.executor.ShapeExecutor;
import org.spiderflow.core.model.SpiderNode;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 开始执行器
 *
 * @author Administrator
 */
@Component
public class StartExecutor implements ShapeExecutor {
	@Override
	public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {

	}

	@Override
	public String supportShape() {
		return "start";
	}
}
