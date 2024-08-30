package org.spiderflow.core.utils;

import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author yida
 * @package org.spiderflow.core.utils
 * @date 2024-08-30 16:00
 * @description 数字相关操作工具类
 */
public class NumberUtils {
	/**
	 * 比较大小，参数1 &gt; 参数2 返回true
	 *
	 * @param bigNum1 数字1
	 * @param bigNum2 数字2
	 * @return 是否大于
	 */
	public static boolean isGreater(BigDecimal bigNum1, BigDecimal bigNum2) {
		Assert.notNull(bigNum1);
		Assert.notNull(bigNum2);
		return bigNum1.compareTo(bigNum2) > 0;
	}

	/**
	 * 比较大小，参数1 &gt;= 参数2 返回true
	 *
	 * @param bigNum1 数字1
	 * @param bigNum2 数字2
	 * @return 是否大于等于
	 */
	public static boolean isGreaterOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
		Assert.notNull(bigNum1);
		Assert.notNull(bigNum2);
		return bigNum1.compareTo(bigNum2) >= 0;
	}

	/**
	 * 比较大小，参数1 &lt; 参数2 返回true
	 *
	 * @param bigNum1 数字1
	 * @param bigNum2 数字2
	 * @return 是否小于
	 */
	public static boolean isLess(BigDecimal bigNum1, BigDecimal bigNum2) {
		Assert.notNull(bigNum1);
		Assert.notNull(bigNum2);
		return bigNum1.compareTo(bigNum2) < 0;
	}

	/**
	 * 比较大小，参数1&lt;=参数2 返回true
	 *
	 * @param bigNum1 数字1
	 * @param bigNum2 数字2
	 * @return 是否小于等于
	 */
	public static boolean isLessOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
		Assert.notNull(bigNum1);
		Assert.notNull(bigNum2);
		return bigNum1.compareTo(bigNum2) <= 0;
	}

	/**
	 * 检查值是否在指定范围内
	 *
	 * @param value      值
	 * @param minInclude 最小值（包含）
	 * @param maxInclude 最大值（包含）
	 * @return 经过检查后的值
	 */
	public static boolean isIn(final BigDecimal value, final BigDecimal minInclude, final BigDecimal maxInclude) {
		Assert.notNull(value);
		Assert.notNull(minInclude);
		Assert.notNull(maxInclude);
		return isGreaterOrEqual(value, minInclude) && isLessOrEqual(value, maxInclude);
	}

	/**
	 * 比较大小，值相等 返回true<br>
	 * 此方法通过调用{@link Double#doubleToLongBits(double)}方法来判断是否相等<br>
	 * 此方法判断值相等时忽略精度的，即0.00 == 0
	 *
	 * @param num1 数字1
	 * @param num2 数字2
	 * @return 是否相等
	 */
	public static boolean equals(double num1, double num2) {
		return Double.doubleToLongBits(num1) == Double.doubleToLongBits(num2);
	}

	/**
	 * 比较大小，值相等 返回true<br>
	 * 此方法通过调用{@link Float#floatToIntBits(float)}方法来判断是否相等<br>
	 * 此方法判断值相等时忽略精度的，即0.00 == 0
	 *
	 * @param num1 数字1
	 * @param num2 数字2
	 * @return 是否相等
	 */
	public static boolean equals(float num1, float num2) {
		return Float.floatToIntBits(num1) == Float.floatToIntBits(num2);
	}

	/**
	 * 比较大小，值相等 返回true<br>
	 * 此方法修复传入long型数据由于没有本类型重载方法,导致数据精度丢失
	 *
	 * @param num1 数字1
	 * @param num2 数字2
	 * @return 是否相等
	 */
	public static boolean equals(long num1, long num2) {
		return num1 == num2;
	}

	/**
	 * 比较数字值是否相等，相等返回{@code true}<br>
	 * 需要注意的是{@link BigDecimal}需要特殊处理<br>
	 * BigDecimal使用compareTo方式判断，因为使用equals方法也判断小数位数，如2.0和2.00就不相等，<br>
	 * 此方法判断值相等时忽略精度的，即0.00 == 0
	 *
	 * <ul>
	 *     <li>如果用户提供两个Number都是{@link BigDecimal}，则通过调用{@link BigDecimal#compareTo(BigDecimal)}方法来判断是否相等</li>
	 *     <li>其他情况调用{@link Number#equals(Object)}比较</li>
	 * </ul>
	 *
	 * @param number1 数字1
	 * @param number2 数字2
	 * @return 是否相等
	 */
	public static boolean equals(final Number number1, final Number number2) {
		if (number1 instanceof BigDecimal && number2 instanceof BigDecimal) {
			return equals((BigDecimal) number1, (BigDecimal) number2);
		}
		return Objects.equals(number1, number2);
	}

	/**
	 * 比较大小，值相等 返回true<br>
	 * 此方法通过调用{@link BigDecimal#compareTo(BigDecimal)}方法来判断是否相等<br>
	 * 此方法判断值相等时忽略精度的，即0.00 == 0
	 *
	 * @param bigNum1 数字1
	 * @param bigNum2 数字2
	 * @return 是否相等
	 */
	public static boolean equals(BigDecimal bigNum1, BigDecimal bigNum2) {
		if (bigNum1 == bigNum2) {
			return true;
		}
		if (bigNum1 == null || bigNum2 == null) {
			return false;
		}
		return 0 == bigNum1.compareTo(bigNum2);
	}

	/**
	 * 比较两个字符是否相同
	 *
	 * @param c1         字符1
	 * @param c2         字符2
	 * @param ignoreCase 是否忽略大小写
	 * @return 是否相同
	 */
	public static boolean equals(char c1, char c2, boolean ignoreCase) {
		if (ignoreCase) {
			return Character.toLowerCase(c1) == Character.toLowerCase(c2);
		}
		return c1 == c2;
	}
}
