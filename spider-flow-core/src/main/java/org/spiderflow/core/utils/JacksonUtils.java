package org.spiderflow.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author yida
 * @package com.wdph.backup.utils
 * @date 2023-05-24 14:32
 * @description Jackson操作工具类
 */
public final class JacksonUtils {
	private static final Logger log = LoggerFactory.getLogger(JacksonUtils.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";

	static {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		//只包含非空字段
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_FORMAT));
		objectMapper.setLocale(Locale.CHINA);
		objectMapper.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		//map按key进行排序
		objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
	}

	private JacksonUtils() {
	}

	/**
	 * @param content
	 * @return {@link JsonNode}
	 * @description 将JSON字符串转换成JsonNode对象
	 * @author yida
	 * @date 2023-05-25 09:39:19
	 */
	public static JsonNode readTree(String content) throws IOException {
		return objectMapper.readTree(content);
	}

	/**
	 * @param content
	 * @param valueType
	 * @return {@link T}
	 * @description 将JSON字符串转换成指定类型的对象
	 * @author yida
	 * @date 2023-05-25 09:39:41
	 */
	public static <T> T readValue(String content, Class<T> valueType) {
		try {
			return objectMapper.readValue(content, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * @param content
	 * @param valueTypeRef
	 * @return {@link T}
	 * @description 将JSON字符串转换成指定类型的对象
	 * @author yida
	 * @date 2023-05-25 09:39:59
	 */
	public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
		try {
			return objectMapper.readValue(content, valueTypeRef);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * @param jsonString
	 * @description 将JSON字符串转成List<T>
	 * @author yida
	 * @date 2023-09-09 10:08:18
	 */
	public static <T> List<T> json2ListBean(String jsonString) {
		if (null == jsonString || jsonString.length() == 0) {
			return null;
		}
		try {
			//Type type = TypeToken.getParameterized(clazz1, clazz2).getType();
			TypeReference<List<T>> typeReference = new TypeReference<List<T>>() {
			};
			return objectMapper.readValue(jsonString, typeReference);
		} catch (IOException e) {
			log.error("parse the jsonstring into List<T> occur exception:\n{}.", e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * @param jsonString
	 * @description 将JSON字符串转成List<Map < K, V>>
	 * @author yida
	 * @date 2023-09-09 10:08:18
	 */
	public static <K, V> List<Map<K, V>> json2ListMap(String jsonString) {
		if (null == jsonString || jsonString.length() == 0) {
			return null;
		}
		try {
			TypeReference<List<Map<K, V>>> typeReference = new TypeReference<List<Map<K, V>>>() {
			};
			return objectMapper.readValue(jsonString, typeReference);
		} catch (IOException e) {
			log.error("parse the jsonstring into List<Map<K, V>> occur exception:\n{}.", e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * @param jsonString
	 * @description 将JSON字符串转成List<List < E>>
	 * @author yida
	 * @date 2023-09-09 10:08:18
	 */
	public static <E> List<List<E>> json2ListWithElementList(String jsonString) {
		if (null == jsonString || jsonString.length() == 0) {
			return null;
		}
		try {
			TypeReference<List<List<E>>> typeReference = new TypeReference<List<List<E>>>() {
			};
			return objectMapper.readValue(jsonString, typeReference);
		} catch (IOException e) {
			log.error("parse the jsonstring into List<List<E>> occur exception:\n{}.", e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * @param jsonString
	 * @description 将JSON字符串转成Map
	 * @author yida
	 * @date 2023-09-02 19:54:46
	 */
	public static <K, V> Map<K, V> json2Map(String jsonString) {
		Map<K, V> map = null;
		if (null == jsonString || jsonString.length() == 0) {
			return map;
		}
		try {
			TypeReference<Map<K, V>> typeReference = new TypeReference<Map<K, V>>() {
			};
			map = objectMapper.readValue(jsonString, typeReference);
		} catch (Exception e) {
		}
		return map;
	}

	/**
	 * @param src
	 * @param valueType
	 * @return {@link T}
	 * @description 将JSON字符串的字节数组转换成指定类型的对象
	 * @author yida
	 * @date 2023-05-25 09:40:15
	 */
	public static <T> T readValue(byte[] src, Class<T> valueType) {
		try {
			return objectMapper.readValue(src, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * @param value
	 * @return String
	 * @description 将任意对象转换为JSON字符串
	 * @author yida
	 * @date 2023-05-25 09:40:39
	 */
	public static String toJSONString(Object value) {
		if (null == value) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(value);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}

