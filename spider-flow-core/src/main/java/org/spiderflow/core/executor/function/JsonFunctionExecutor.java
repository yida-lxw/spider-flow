package org.spiderflow.core.executor.function;

import com.alibaba.fastjson.JSON;
import org.spiderflow.core.annotation.Comment;
import org.spiderflow.core.annotation.Example;
import org.spiderflow.core.executor.FunctionExecutor;
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

	@Comment("将字符串转为json对象")
	@Example("${json.parse('{code : 1}')}")
	public static Object parse(String jsonString) {
		return jsonString != null ? JSON.parse(jsonString) : null;
	}

	@Comment("将对象转为json字符串")
	@Example("${json.stringify(objVar)}")
	public static String stringify(Object object) {
		return object != null ? JSON.toJSONString(object) : null;
	}
}
