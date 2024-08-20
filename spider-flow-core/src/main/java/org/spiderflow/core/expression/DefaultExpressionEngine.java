package org.spiderflow.core.expression;

import org.apache.commons.lang3.StringUtils;
import org.spiderflow.ExpressionEngine;
import org.spiderflow.core.expression.interpreter.Reflection;
import org.spiderflow.executor.FunctionExecutor;
import org.spiderflow.executor.FunctionExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class DefaultExpressionEngine implements ExpressionEngine {

	@Autowired
	private List<FunctionExecutor> functionExecutors;

	@Autowired
	private List<FunctionExtension> functionExtensions;

	@PostConstruct
	private void init() {
		for (FunctionExtension extension : functionExtensions) {
			Reflection.getInstance().registerExtensionClass(extension.support(), extension.getClass());
		}
	}

	@Override
	public Object execute(String expression, Map<String, Object> variables) {
		if (StringUtils.isBlank(expression)) {
			return expression;
		}
		ExpressionTemplateContext context = new ExpressionTemplateContext(variables);
		for (FunctionExecutor executor : functionExecutors) {
			context.set(executor.getFunctionPrefix(), executor);
		}
		ExpressionGlobalVariables.getVariables().entrySet().forEach(entry -> {
			context.set(entry.getKey(), ExpressionTemplate.create(entry.getValue()).render(context));
		});
		try {
			ExpressionTemplateContext.set(context);
			return ExpressionTemplate.create(expression).render(context);
		} finally {
			ExpressionTemplateContext.remove();
		}
	}

}
