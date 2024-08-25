package org.spiderflow.core.executor.shape;

import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.executor.ShapeExecutor;
import org.spiderflow.core.job.id.IdGenerator;
import org.spiderflow.core.model.SpiderNode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommentExecutor implements ShapeExecutor {

	@Override
	public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables, IdGenerator<String> idGenerator) {

	}

	@Override
	public String supportShape() {
		return "comment";
	}

}
