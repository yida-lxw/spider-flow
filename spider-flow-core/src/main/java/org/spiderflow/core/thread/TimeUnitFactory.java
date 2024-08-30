package org.spiderflow.core.thread;

import java.util.concurrent.TimeUnit;

/**
 * @author yida
 * @date 2023-08-08 10:50
 * @description TimeUnit工厂
 */
public class TimeUnitFactory {
	/**
	 * @param timeUnitTag
	 * @return {@link TimeUnit}
	 * @description 根据传入的字符串构建TimeUnit枚举
	 * @author yida
	 * @date 2023-08-08 11:01:07
	 */
	public static TimeUnit buildTimeUnit(String timeUnitTag) {
		if (null == timeUnitTag) {
			timeUnitTag = TimeUnitEnum.MILLISECONDS.getVal();
		}
		if (!TimeUnitEnum.containsKey(timeUnitTag)) {
			throw new IllegalArgumentException("timeUnitTag MUST be one of the timeUnitSet of TimeUnitEnum.");
		}
		if (timeUnitTag.equals(TimeUnitEnum.NANOSECONDS_TAG)) {
			return TimeUnit.NANOSECONDS;
		}
		if (timeUnitTag.equals(TimeUnitEnum.MICROSECONDS_TAG)) {
			return TimeUnit.MICROSECONDS;
		}
		if (timeUnitTag.equals(TimeUnitEnum.MILLISECONDS_TAG)) {
			return TimeUnit.MILLISECONDS;
		}
		if (timeUnitTag.equals(TimeUnitEnum.SECONDS_TAG)) {
			return TimeUnit.SECONDS;
		}
		if (timeUnitTag.equals(TimeUnitEnum.MINUTES_TAG)) {
			return TimeUnit.MINUTES;
		}
		if (timeUnitTag.equals(TimeUnitEnum.HOURS_TAG)) {
			return TimeUnit.HOURS;
		}
		return TimeUnit.DAYS;
	}

	/**
	 * @param timeUnitEnum
	 * @return {@link TimeUnit}
	 * @description 根据传入的TimeUnitEnum枚举构建TimeUnit枚举
	 * @author yida
	 * @date 2023-08-08 11:01:07
	 */
	public static TimeUnit buildTimeUnit(TimeUnitEnum timeUnitEnum) {
		if (null == timeUnitEnum) {
			timeUnitEnum = TimeUnitEnum.MILLISECONDS;
		}
		if (timeUnitEnum.equals(TimeUnitEnum.NANOSECONDS)) {
			return TimeUnit.NANOSECONDS;
		}
		if (timeUnitEnum.equals(TimeUnitEnum.MICROSECONDS)) {
			return TimeUnit.MICROSECONDS;
		}
		if (timeUnitEnum.equals(TimeUnitEnum.MILLISECONDS)) {
			return TimeUnit.MILLISECONDS;
		}
		if (timeUnitEnum.equals(TimeUnitEnum.SECONDS)) {
			return TimeUnit.SECONDS;
		}
		if (timeUnitEnum.equals(TimeUnitEnum.MINUTES)) {
			return TimeUnit.MINUTES;
		}
		if (timeUnitEnum.equals(TimeUnitEnum.HOURS)) {
			return TimeUnit.HOURS;
		}
		return TimeUnit.DAYS;
	}
}
