package org.spiderflow.core.executor.function;

import org.spiderflow.core.annotation.Comment;
import org.spiderflow.core.annotation.Example;
import org.spiderflow.core.executor.FunctionExecutor;
import org.spiderflow.core.utils.JacksonUtils;
import org.springframework.stereotype.Component;

/**
 * Json和String互相转换 工具类 防止NPE
 *
 * @author Administrator
 */
@Component
@Comment("json常用方法")
public class JsonFunctionExecutor implements FunctionExecutor {

	@Override
	public String getFunctionPrefix() {
		return "json";
	}

	@Comment("将json字符串转为Map对象")
	@Example("${json.toMap('{code : 1}')}")
	public static Object toMap(String jsonString) {
		return JacksonUtils.json2Map(jsonString);
	}

	@Comment("将对象转为json字符串")
	@Example("${json.stringify(objVar)}")
	public static String stringify(Object object) {
		return JacksonUtils.toJSONString(object);
	}
}
