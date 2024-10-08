package org.spiderflow.core.executor.function.extension;

import org.spiderflow.core.annotation.Comment;
import org.spiderflow.core.annotation.Example;
import org.spiderflow.core.executor.FunctionExtension;
import org.spiderflow.core.utils.ExtractUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ObjectFunctionExtension implements FunctionExtension {

	@Override
	public Class<?> support() {
		return Object.class;
	}

	@Comment("将对象转为string类型")
	@Example("${objVar.string()}")
	public static String string(Object obj) {
		if (obj instanceof String) {
			return (String) obj;
		}
		return Objects.toString(obj);
	}

	@Comment("根据jsonpath提取内容")
	@Example("${objVar.jsonpath('$.code')}")
	public static Object jsonpath(Object obj, String path) {
		return ExtractUtils.getValueByJsonPath(obj, path);
	}

	@Comment("睡眠等待一段时间")
	@Example("${objVar.sleep(1000)}")
	public static Object sleep(Object obj, int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {
		}
		return obj;
	}
}
