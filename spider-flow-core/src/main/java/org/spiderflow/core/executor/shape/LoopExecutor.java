package org.spiderflow.core.executor.shape;

import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.executor.ShapeExecutor;
import org.spiderflow.core.model.SpiderNode;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 循环执行器
 *
 * @author Administrator
 */
@Component
public class LoopExecutor implements ShapeExecutor {

	public static final String LOOP_ITEM = "loopItem";

	public static final String LOOP_START = "loopStart";

	public static final String LOOP_END = "loopEnd";

	@Override
	public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
	}

	@Override
	public String supportShape() {
		return "loop";
	}
}
