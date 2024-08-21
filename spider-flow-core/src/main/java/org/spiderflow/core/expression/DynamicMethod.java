package org.spiderflow.core.expression;

import java.util.List;

public interface DynamicMethod {
	Object execute(String methodName, List<Object> parameters);
}
