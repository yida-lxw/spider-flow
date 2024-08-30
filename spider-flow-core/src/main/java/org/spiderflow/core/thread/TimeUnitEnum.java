package org.spiderflow.core.thread;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yida
 * @date 2023-08-24 15:33
 * @description TimeUnit枚举
 */
public enum TimeUnitEnum {
	NANOSECONDS(0, TimeUnitEnum.NANOSECONDS_TAG),
	MICROSECONDS(1, TimeUnitEnum.MICROSECONDS_TAG),
	MILLISECONDS(2, TimeUnitEnum.MILLISECONDS_TAG),
	SECONDS(3, TimeUnitEnum.SECONDS_TAG),
	MINUTES(4, TimeUnitEnum.MINUTES_TAG),
	HOURS(5, TimeUnitEnum.HOURS_TAG),
	DAYS(6, TimeUnitEnum.DAYS_TAG);

	private int code;
	private String val;

	TimeUnitEnum(int code, String val) {
		this.code = code;
		this.val = val;
	}

	public static final String NANOSECONDS_TAG = "nano-seconds";
	public static final String MICROSECONDS_TAG = "micro-seconds";
	public static final String MILLISECONDS_TAG = "milli-seconds";
	public static final String SECONDS_TAG = "seconds";
	public static final String MINUTES_TAG = "minutes";
	public static final String HOURS_TAG = "hours";
	public static final String DAYS_TAG = "days";

	public static final Set<String> timeUnitEnumSet = new HashSet<>();

	static {
		timeUnitEnumSet.add(NANOSECONDS_TAG);
		timeUnitEnumSet.add(MICROSECONDS_TAG);
		timeUnitEnumSet.add(MILLISECONDS_TAG);
		timeUnitEnumSet.add(SECONDS_TAG);
		timeUnitEnumSet.add(MINUTES_TAG);
		timeUnitEnumSet.add(HOURS_TAG);
		timeUnitEnumSet.add(DAYS_TAG);
	}

	public static TimeUnitEnum of(String timeUnitValue) {
		if (null == timeUnitValue || "".equals(timeUnitValue)) {
			throw new IllegalArgumentException("timeUnitValue MUST NOT be null or empty.");
		}
		if (!timeUnitEnumSet.contains(timeUnitValue)) {
			throw new UnsupportedOperationException("timeUnitValue MUST BE in the timeUnitEnumSet.");
		}
		if (NANOSECONDS_TAG.equals(timeUnitValue)) {
			return NANOSECONDS;
		}
		if (MICROSECONDS_TAG.equals(timeUnitValue)) {
			return MICROSECONDS;
		}
		if (MILLISECONDS_TAG.equals(timeUnitValue)) {
			return MILLISECONDS;
		}
		if (SECONDS_TAG.equals(timeUnitValue)) {
			return SECONDS;
		}
		if (MINUTES_TAG.equals(timeUnitValue)) {
			return MINUTES;
		}
		if (HOURS_TAG.equals(timeUnitValue)) {
			return HOURS;
		}
		return DAYS;
	}

	public static boolean containsKey(String timeUnitValue) {
		return timeUnitEnumSet.contains(timeUnitValue);
	}

	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
}
