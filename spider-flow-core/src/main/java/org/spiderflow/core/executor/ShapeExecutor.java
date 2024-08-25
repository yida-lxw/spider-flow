package org.spiderflow.core.executor;


import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.job.id.IdGenerator;
import org.spiderflow.core.model.Shape;
import org.spiderflow.core.model.SpiderNode;

import java.util.Map;

/**
 * 执行器接口
 *
 * @author jmxd
 */
public interface ShapeExecutor {

	String LOOP_VARIABLE_NAME = "loopVariableName";

	String LOOP_COUNT = "loopCount";

	String THREAD_COUNT = "threadCount";

	default Shape shape() {
		return null;
	}

	/**
	 * 节点形状
	 *
	 * @return 节点形状名称
	 */
	String supportShape();

	/**
	 * 执行器具体的功能实现
	 *
	 * @param node      当前要执行的爬虫节点
	 * @param context   爬虫上下文
	 * @param variables 节点流程的全部变量的集合
	 */
	void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables, IdGenerator<String> idGenerator);

	default boolean allowExecuteNext(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
		return true;
	}

	default boolean isThread() {
		return true;
	}
}
