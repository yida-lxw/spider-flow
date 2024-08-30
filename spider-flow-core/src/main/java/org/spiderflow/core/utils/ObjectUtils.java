package org.spiderflow.core.utils;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author yida
 * @package org.spiderflow.core.utils
 * @date 2024-08-30 16:08
 * @description Object对象操作工具类
 */
public class ObjectUtils {
	/**
	 * 比较两个对象是否相等，此方法是 {@link #equal(Object, Object)}的别名方法。<br>
	 * 相同的条件有两个，满足其一即可：<br>
	 * <ol>
	 * <li>obj1 == null &amp;&amp; obj2 == null</li>
	 * <li>obj1.equals(obj2)</li>
	 * <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)</li>
	 * </ol>
	 *
	 * @param obj1 对象1
	 * @param obj2 对象2
	 * @return 是否相等
	 */
	public static boolean equals(Object obj1, Object obj2) {
		return equal(obj1, obj2);
	}

	/**
	 * 比较两个对象是否相等。<br>
	 * 相同的条件有两个，满足其一即可：<br>
	 * <ol>
	 * <li>obj1 == null &amp;&amp; obj2 == null</li>
	 * <li>obj1.equals(obj2)</li>
	 * <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)</li>
	 * </ol>
	 *
	 * @param obj1 对象1
	 * @param obj2 对象2
	 * @return 是否相等
	 */
	public static boolean equal(Object obj1, Object obj2) {
		if (obj1 instanceof Number && obj2 instanceof Number) {
			return NumberUtils.equals((Number) obj1, (Number) obj2);
		}
		return Objects.equals(obj1, obj2);
	}

	/**
	 * 比较两个对象是否不相等。<br>
	 *
	 * @param obj1 对象1
	 * @param obj2 对象2
	 * @return 是否不等
	 */
	public static boolean notEqual(Object obj1, Object obj2) {
		return false == equal(obj1, obj2);
	}

	/**
	 * 计算对象长度，如果是字符串调用其length函数，集合类调用其size函数，数组调用其length属性，其他可遍历对象遍历计算长度<br>
	 * 支持的类型包括：
	 * <ul>
	 * <li>CharSequence</li>
	 * <li>Map</li>
	 * <li>Iterator</li>
	 * <li>Enumeration</li>
	 * <li>Array</li>
	 * </ul>
	 *
	 * @param obj 被计算长度的对象
	 * @return 长度
	 */
	public static int length(Object obj) {
		if (obj == null) {
			return 0;
		}
		if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length();
		}
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).size();
		}
		if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size();
		}

		int count;
		if (obj instanceof Iterator) {
			final Iterator<?> iter = (Iterator<?>) obj;
			count = 0;
			while (iter.hasNext()) {
				count++;
				iter.next();
			}
			return count;
		}
		if (obj instanceof Enumeration) {
			final Enumeration<?> enumeration = (Enumeration<?>) obj;
			count = 0;
			while (enumeration.hasMoreElements()) {
				count++;
				enumeration.nextElement();
			}
			return count;
		}
		if (obj.getClass().isArray() == true) {
			return Array.getLength(obj);
		}
		return -1;
	}

	/**
	 * 对象中是否包含元素<br>
	 * 支持的对象类型包括：
	 * <ul>
	 * <li>String</li>
	 * <li>Collection</li>
	 * <li>Map</li>
	 * <li>Iterator</li>
	 * <li>Enumeration</li>
	 * <li>Array</li>
	 * </ul>
	 *
	 * @param obj     对象
	 * @param element 元素
	 * @return 是否包含
	 */
	public static boolean contains(Object obj, Object element) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			if (element == null) {
				return false;
			}
			return ((String) obj).contains(element.toString());
		}
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).contains(element);
		}
		if (obj instanceof Map) {
			return ((Map<?, ?>) obj).containsValue(element);
		}

		if (obj instanceof Iterator) {
			final Iterator<?> iter = (Iterator<?>) obj;
			while (iter.hasNext()) {
				final Object o = iter.next();
				if (equal(o, element)) {
					return true;
				}
			}
			return false;
		}
		if (obj instanceof Enumeration) {
			final Enumeration<?> enumeration = (Enumeration<?>) obj;
			while (enumeration.hasMoreElements()) {
				final Object o = enumeration.nextElement();
				if (equal(o, element)) {
					return true;
				}
			}
			return false;
		}
		if (obj.getClass().isArray() == true) {
			final int len = Array.getLength(obj);
			for (int i = 0; i < len; i++) {
				final Object o = Array.get(obj, i);
				if (equal(o, element)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检查对象是否为null<br>
	 * 判断标准为：
	 *
	 * <pre>
	 * 1. == null
	 * 2. equals(null)
	 * </pre>
	 *
	 * @param obj 对象
	 * @return 是否为null
	 */
	public static boolean isNull(Object obj) {
		return null == obj || obj.equals(null);
	}

	/**
	 * 检查对象是否不为null
	 * <pre>
	 * 1. != null
	 * 2. not equals(null)
	 * </pre>
	 *
	 * @param obj 对象
	 * @return 是否为非null
	 */
	public static boolean isNotNull(Object obj) {
		return null != obj && false == obj.equals(null);
	}

	/**
	 * 判断指定对象是否为空，支持：
	 *
	 * <pre>
	 * 1. CharSequence
	 * 2. Map
	 * 3. Iterable
	 * 4. Iterator
	 * 5. Array
	 * </pre>
	 *
	 * @param obj 被判断的对象
	 * @return 是否为空，如果类型不支持，返回false
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj) {
		if (null == obj) {
			return true;
		}
		if (obj instanceof CharSequence || obj instanceof String) {
			return StringUtils.isEmpty((String) obj);
		} else if (obj instanceof Map) {
			Map map = (Map)obj;
			return null == map || map.isEmpty();
		} else if (obj instanceof Iterable) {
			Iterable iterable = (Iterable)obj;
			Iterator iterator = iterable.iterator();
			return null == iterator || !iterator.hasNext();
		} else if (obj instanceof Iterator) {
			Iterator iterator = (Iterator)obj;
			return !iterator.hasNext();
		} else if (obj.getClass().isArray()) {
			Object[] array = (Object[])obj;
			return array == null || array.length == 0;
		}
		return false;
	}

	/**
	 * 判断指定对象是否为非空，支持：
	 *
	 * <pre>
	 * 1. CharSequence
	 * 2. Map
	 * 3. Iterable
	 * 4. Iterator
	 * 5. Array
	 * </pre>
	 *
	 * @param obj 被判断的对象
	 * @return 是否为空，如果类型不支持，返回true
	 */
	public static boolean isNotEmpty(Object obj) {
		return false == isEmpty(obj);
	}

	/**
	 * 如果给定对象为{@code null}返回默认值
	 *
	 * <pre>
	 * ObjectUtil.defaultIfNull(null, null)      = null
	 * ObjectUtil.defaultIfNull(null, "")        = ""
	 * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
	 * ObjectUtil.defaultIfNull("abc", *)        = "abc"
	 * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
	 * </pre>
	 *
	 * @param <T>          对象类型
	 * @param object       被检查对象，可能为{@code null}
	 * @param defaultValue 被检查对象为{@code null}返回的默认值，可以为{@code null}
	 * @return 被检查对象为{@code null}返回默认值，否则返回原值
	 */
	public static <T> T defaultIfNull(final T object, final T defaultValue) {
		return isNull(object) ? defaultValue : object;
	}

	/**
	 * 如果被检查对象为 {@code null}， 返回默认值（由 defaultValueSupplier 提供）；否则直接返回
	 *
	 * @param source               被检查对象
	 * @param defaultValueSupplier 默认值提供者
	 * @param <T>                  对象类型
	 * @return 被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
	 * @throws NullPointerException {@code defaultValueSupplier == null} 时，抛出
	 */
	public static <T> T defaultIfNull(T source, Supplier<? extends T> defaultValueSupplier) {
		if (isNull(source)) {
			return defaultValueSupplier.get();
		}
		return source;
	}

	/**
	 * 如果被检查对象为 {@code null}， 返回默认值（由 defaultValueSupplier 提供）；否则直接返回
	 *
	 * @param source               被检查对象
	 * @param defaultValueSupplier 默认值提供者
	 * @param <T>                  对象类型
	 * @return 被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
	 * @throws NullPointerException {@code defaultValueSupplier == null} 时，抛出
	 */
	public static <T> T defaultIfNull(T source, Function<T, ? extends T> defaultValueSupplier) {
		if (isNull(source)) {
			return defaultValueSupplier.apply(null);
		}
		return source;
	}

	/**
	 * 如果给定对象为{@code null} 返回默认值, 如果不为null 返回自定义handle处理后的返回值
	 *
	 * @param source       Object 类型对象
	 * @param handle       非空时自定义的处理方法
	 * @param defaultValue 默认为空的返回值
	 * @param <T>          被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
	 * @return 处理后的返回值
	 */
	@Deprecated
	public static <T> T defaultIfNull(Object source, Supplier<? extends T> handle, final T defaultValue) {
		if (isNotNull(source)) {
			return handle.get();
		}
		return defaultValue;
	}

	/**
	 * 如果给定对象为{@code null} 返回默认值, 如果不为null 返回自定义handle处理后的返回值
	 *
	 * @param <T>          被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
	 * @param <R>          被检查的对象类型
	 * @param source       Object 类型对象
	 * @param handle       非空时自定义的处理方法
	 * @param defaultValue 默认为空的返回值
	 * @return 处理后的返回值
	 */
	public static <T, R> T defaultIfNull(R source, Function<R, ? extends T> handle, final T defaultValue) {
		if (isNotNull(source)) {
			return handle.apply(source);
		}
		return defaultValue;
	}

	/**
	 * 如果给定对象为{@code null}或者""返回默认值, 否则返回自定义handle处理后的返回值
	 *
	 * @param str          String 类型
	 * @param handle       自定义的处理方法
	 * @param defaultValue 默认为空的返回值
	 * @param <T>          被检查对象为{@code null}或者 ""返回默认值，否则返回自定义handle处理后的返回值
	 * @return 处理后的返回值
	 */
	@Deprecated
	public static <T> T defaultIfEmpty(String str, Supplier<? extends T> handle, final T defaultValue) {
		if (StringUtils.isEmpty(str)) {
			return handle.get();
		}
		return defaultValue;
	}

	/**
	 * 如果给定对象为{@code null}或者""返回默认值, 否则返回自定义handle处理后的返回值
	 *
	 * @param str          String 类型
	 * @param handle       自定义的处理方法
	 * @param defaultValue 默认为空的返回值
	 * @param <T>          被检查对象为{@code null}或者 ""返回默认值，否则返回自定义handle处理后的返回值
	 * @return 处理后的返回值
	 */
	public static <T> T defaultIfEmpty(String str, Function<CharSequence, ? extends T> handle, final T defaultValue) {
		if (StringUtils.isEmpty(str)) {
			return handle.apply(str);
		}
		return defaultValue;
	}

	/**
	 * 如果给定对象为{@code null}或者 "" 返回默认值
	 *
	 * <pre>
	 * ObjectUtil.defaultIfEmpty(null, null)      = null
	 * ObjectUtil.defaultIfEmpty(null, "")        = ""
	 * ObjectUtil.defaultIfEmpty("", "zz")      = "zz"
	 * ObjectUtil.defaultIfEmpty(" ", "zz")      = " "
	 * ObjectUtil.defaultIfEmpty("abc", *)        = "abc"
	 * </pre>
	 *
	 * @param <T>          对象类型（必须实现CharSequence接口）
	 * @param str          被检查对象，可能为{@code null}
	 * @param defaultValue 被检查对象为{@code null}或者 ""返回的默认值，可以为{@code null}或者 ""
	 * @return 被检查对象为{@code null}或者 ""返回默认值，否则返回原值
	 */
	public static <T extends CharSequence> T defaultIfEmpty(final T str, final T defaultValue) {
		return StringUtils.isEmpty(str) ? defaultValue : str;
	}

	/**
	 * 如果被检查对象为 {@code null} 或 "" 时，返回默认值（由 defaultValueSupplier 提供）；否则直接返回
	 *
	 * @param str                  被检查对象
	 * @param defaultValueSupplier 默认值提供者
	 * @param <T>                  对象类型（必须实现CharSequence接口）
	 * @return 被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
	 * @throws NullPointerException {@code defaultValueSupplier == null} 时，抛出
	 */
	public static <T extends CharSequence> T defaultIfEmpty(T str, Supplier<? extends T> defaultValueSupplier) {
		if (StringUtils.isEmpty(str)) {
			return defaultValueSupplier.get();
		}
		return str;
	}

	/**
	 * 如果被检查对象为 {@code null} 或 "" 时，返回默认值（由 defaultValueSupplier 提供）；否则直接返回
	 *
	 * @param str                  被检查对象
	 * @param defaultValueSupplier 默认值提供者
	 * @param <T>                  对象类型（必须实现CharSequence接口）
	 * @return 被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
	 * @throws NullPointerException {@code defaultValueSupplier == null} 时，抛出
	 */
	public static <T extends CharSequence> T defaultIfEmpty(T str, Function<T, ? extends T> defaultValueSupplier) {
		if (StringUtils.isEmpty(str)) {
			return defaultValueSupplier.apply(null);
		}
		return str;
	}

	/**
	 * 如果给定对象为{@code null}或者""或者空白符返回默认值
	 *
	 * <pre>
	 * ObjectUtil.defaultIfBlank(null, null)      = null
	 * ObjectUtil.defaultIfBlank(null, "")        = ""
	 * ObjectUtil.defaultIfBlank("", "zz")      = "zz"
	 * ObjectUtil.defaultIfBlank(" ", "zz")      = "zz"
	 * ObjectUtil.defaultIfBlank("abc", *)        = "abc"
	 * </pre>
	 *
	 * @param <T>          对象类型（必须实现CharSequence接口）
	 * @param str          被检查对象，可能为{@code null}
	 * @param defaultValue 被检查对象为{@code null}或者 ""或者空白符返回的默认值，可以为{@code null}或者 ""或者空白符
	 * @return 被检查对象为{@code null}或者 ""或者空白符返回默认值，否则返回原值
	 */
	public static <T extends CharSequence> T defaultIfBlank(final T str, final T defaultValue) {
		return StringUtils.isEmpty(str) ? defaultValue : str;
	}

	/**
	 * 如果被检查对象为 {@code null} 或 "" 或 空白字符串时，返回默认值（由 defaultValueSupplier 提供）；否则直接返回
	 *
	 * @param str                  被检查对象
	 * @param defaultValueSupplier 默认值提供者
	 * @param <T>                  对象类型（必须实现CharSequence接口）
	 * @return 被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
	 * @throws NullPointerException {@code defaultValueSupplier == null} 时，抛出
	 */
	public static <T extends CharSequence> T defaultIfBlank(T str, Supplier<? extends T> defaultValueSupplier) {
		if (StringUtils.isEmpty(str)) {
			return defaultValueSupplier.get();
		}
		return str;
	}

	/**
	 * 如果被检查对象为 {@code null} 或 "" 或 空白字符串时，返回默认值（由 defaultValueSupplier 提供）；否则直接返回
	 *
	 * @param str                  被检查对象
	 * @param defaultValueSupplier 默认值提供者
	 * @param <T>                  对象类型（必须实现CharSequence接口）
	 * @return 被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
	 * @throws NullPointerException {@code defaultValueSupplier == null} 时，抛出
	 */
	public static <T extends CharSequence> T defaultIfBlank(T str, Function<T, ? extends T> defaultValueSupplier) {
		if (StringUtils.isEmpty(str)) {
			return defaultValueSupplier.apply(null);
		}
		return str;
	}
}
