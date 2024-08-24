package org.spiderflow.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yida
 * @package org.spiderflow.core.utils
 * @date 2024-08-24 14:43
 * @description Map操作工具类
 */
public class MapUtils {
	public static <K, V> Map<K, V> sortByKey(Map<K, V> sourceMap) {
		List<K> keyList = new ArrayList<>(sourceMap.keySet());
		// 对键进行排序
		Collections.sort((List) keyList);
		Map<K, V> sortedMap = new LinkedHashMap<>();
		for (K key : keyList) {
			sortedMap.put(key, sourceMap.get(key));
		}
		return sortedMap;
	}

	public static <K, V> Map<K, V> copy(Map<K, V> sourceMap) {
		Map<K, V> targetMap = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : sourceMap.entrySet()) {
			K key = entry.getKey();
			V val = entry.getValue();
			targetMap.put(key, val);
		}
		return targetMap;
	}
}
