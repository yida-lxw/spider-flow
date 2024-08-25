package org.spiderflow.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.constants.Constants;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author yida
 * @package org.spiderflow.core.utils
 * @date 2024-08-25 20:33
 * @description 日期操作工具类
 */
public class DateUtils {
	private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

	public static final String datetimeFormatText = "yyyy-MM-dd HH:mm:ss";
	public static final String datetimeFormatNosepText = "yyyyMMddHHmmssSSS";
	public static final String dateFormatText = "yyyy-MM-dd";
	public static final String timeFormatText = "HH:mm:ss";

	public static final String PATTERN_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final String PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	public static final String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

	public static final String PATTERN_YYYYMMDD = "yyyyMMdd";

	public static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";

	public static final String PATTERN_HHMMDD = "HHmmss";

	public static final String PATTERN_HH_MM_DD = "HH:mm:ss";

	public static final ZoneId DEFAULT_USER_TIMEZONE = ZoneId.of("Asia/Shanghai");

	public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSS = DateTimeFormatter.ofPattern(PATTERN_YYYY_MM_DD_HH_MM_SS_SSS).withZone(DEFAULT_USER_TIMEZONE);

	public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern(PATTERN_YYYY_MM_DD_HH_MM_SS).withZone(DEFAULT_USER_TIMEZONE);

	public static final DateTimeFormatter YYYYMMDDHHMMSS = DateTimeFormatter.ofPattern(PATTERN_YYYYMMDDHHMMSS).withZone(DEFAULT_USER_TIMEZONE);

	public static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern(PATTERN_YYYYMMDD).withZone(DEFAULT_USER_TIMEZONE);

	public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern(PATTERN_YYYY_MM_DD).withZone(DEFAULT_USER_TIMEZONE);

	public static final DateTimeFormatter HHMMDD = DateTimeFormatter.ofPattern(PATTERN_HHMMDD).withZone(DEFAULT_USER_TIMEZONE);

	public static final DateTimeFormatter HH_MM_DD = DateTimeFormatter.ofPattern(PATTERN_HH_MM_DD).withZone(DEFAULT_USER_TIMEZONE);

	public static final Locale DEFAULT_LOCAL = Locale.CHINA;

	public static final LocalDate GREENWICH_MEAN_TIME = LocalDate.of(1970, 1, 1);

	public static Date currentDate() {
		return new Date(System.currentTimeMillis());
	}

	public static String currentDateText() {
		return currentDateText(null);
	}

	public static String currentDateText(String pattern) {
		Date currentDate = currentDate();
		if (null == pattern || Constants.EMPTY_STRING.equals(pattern)) {
			pattern = PATTERN_YYYY_MM_DD_HH_MM_SS;
		}
		return format(currentDate, pattern);
	}

	/**
	 * Instant 转 java.util.Date
	 *
	 * @param instant
	 * @return
	 */
	public static Date instantToDateConverter(Instant instant) {
		Optional.ofNullable(instant).orElseThrow(() -> new RuntimeException("instant can not be null"));
		return Date.from(instant);
	}

	/**
	 * LocalDateTime 转 java.util.Date
	 *
	 * @param localDateTime
	 * @return
	 */
	public static Date localDateTimeToDateConverter(LocalDateTime localDateTime) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		Instant instant = localDateTime.atZone(DEFAULT_USER_TIMEZONE).toInstant();
		return instantToDateConverter(instant);
	}

	/**
	 * LocalDate 转 java.util.Date
	 *
	 * @param localDate
	 * @return
	 */
	public static Date localDateToDateConverter(LocalDate localDate) {
		Optional.ofNullable(localDate).orElseThrow(() -> new RuntimeException("localDate can not be null"));
		Instant instant = localDate.atStartOfDay(DEFAULT_USER_TIMEZONE).toInstant();
		return instantToDateConverter(instant);
	}

	/**
	 * LocalTime 转 java.util.Date
	 *
	 * @param localTime
	 * @return
	 */
	public static Date localTimeToDateConverter(LocalTime localTime) {
		Optional.ofNullable(localTime).orElseThrow(() -> new RuntimeException("localTime can not be null"));
		LocalDate currentDate = LocalDate.now();
		LocalDateTime localDateTime = localTime.atDate(currentDate);
		ZonedDateTime zonedDateTime = localDateTime.atZone(DEFAULT_USER_TIMEZONE);
		Instant instant = zonedDateTime.toInstant();
		return Date.from(instant);
	}


	/**
	 * LocalDate 转 LocalDateTime
	 *
	 * @param localDate
	 * @return
	 */
	public static LocalDateTime localDateToLocalDateTimeConverter(LocalDate localDate) {
		Optional.ofNullable(localDate).orElseThrow(() -> new RuntimeException("localDate can not be null"));
		return localDate.atStartOfDay();
	}

	/**
	 * LocalTime 转 LocalDateTime
	 *
	 * @param localTime
	 * @return
	 */
	public static LocalDateTime localTimeToLocalDateTimeConverter(LocalTime localTime) {
		Optional.ofNullable(localTime).orElseThrow(() -> new RuntimeException("localTime can not be null"));
		return localTime.atDate(GREENWICH_MEAN_TIME);
	}

	/**
	 * java.util.Date 转 Instant
	 *
	 * @param date
	 * @return
	 */
	public static Instant dateToInstantConverter(Date date) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		return date.toInstant();
	}

	/**
	 * java.util.Date 转 LocalDateTime
	 *
	 * @param date
	 * @return
	 */
	public static LocalDateTime dateToLocalDateTimeConverter(Date date) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		return LocalDateTime.ofInstant(dateToInstantConverter(date), DEFAULT_USER_TIMEZONE);
	}

	/**
	 * java.util.Date 转 LocalDate
	 *
	 * @param date
	 * @return
	 */
	public static LocalDate dateToLocalDateConverter(Date date) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		return dateToLocalDateTimeConverter(date).toLocalDate();
	}

	/**
	 * java.util.Date 转 LocalTime
	 *
	 * @param date
	 * @return
	 */
	public static LocalTime dateToLocalTimeConverter(Date date) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		return dateToLocalDateTimeConverter(date).toLocalTime();
	}

	/**
	 * LocalDateTime 转 Instant
	 *
	 * @param localDateTime
	 * @return
	 */
	public static Instant localDateTimeToInstantConverter(LocalDateTime localDateTime) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		return localDateTime.atZone(DEFAULT_USER_TIMEZONE).toInstant();
	}

	/**
	 * Instant 转 LocalDateTime
	 *
	 * @param instant
	 * @return
	 */
	public static LocalDateTime instantToLocalDateTimeConverter(Instant instant) {
		Optional.ofNullable(instant).orElseThrow(() -> new RuntimeException("instant can not be null"));
		return instant.atZone(DEFAULT_USER_TIMEZONE).toLocalDateTime();
	}

	/**
	 * LocalDateTime 转 LocalDate
	 *
	 * @param localDateTime
	 * @return
	 */
	public static LocalDate localDateTimeToLocalDateConverter(LocalDateTime localDateTime) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		return localDateTime.atZone(DEFAULT_USER_TIMEZONE).toLocalDate();
	}

	/**
	 * LocalDateTime 转 LocalTime
	 *
	 * @param localDateTime
	 * @return
	 */
	public static LocalTime localDateTimeToLocalTimeConverter(LocalDateTime localDateTime) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		return localDateTime.atZone(DEFAULT_USER_TIMEZONE).toLocalTime();
	}

	/**
	 * java.util.Date格式化
	 *
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, DateTimeFormatter pattern) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return format(dateToLocalDateTimeConverter(date), pattern);
	}

	/**
	 * java.util.Date格式化
	 *
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return format(dateToLocalDateTimeConverter(date), dateTimeFormatter);
	}

	/**
	 * java.util.Date的毫秒数格式化为指定格式的字符串
	 *
	 * @param dateMills
	 * @return
	 */
	public static String format(Long dateMills) {
		return format(dateMills, PATTERN_YYYY_MM_DD_HH_MM_SS);
	}

	/**
	 * java.util.Date的毫秒数格式化为指定格式的字符串
	 *
	 * @param dateMills
	 * @param pattern
	 * @return
	 */
	public static String format(Long dateMills, String pattern) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		Optional.ofNullable(dateMills).orElseThrow(() -> new RuntimeException("dateMills can not be null"));
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return format(dateToLocalDateTimeConverter(new Date(dateMills)), dateTimeFormatter);
	}

	/**
	 * Instant格式化
	 *
	 * @param instant
	 * @param pattern
	 * @return
	 */
	public static String format(Instant instant, DateTimeFormatter pattern) {
		Optional.ofNullable(instant).orElseThrow(() -> new RuntimeException("instant can not be null"));
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return pattern.format(instant);
	}

	/**
	 * Instant格式化
	 *
	 * @param instant
	 * @param pattern
	 * @return
	 */
	public static String format(Instant instant, String pattern) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		Optional.ofNullable(instant).orElseThrow(() -> new RuntimeException("instant can not be null"));
		Optional.ofNullable(dateTimeFormatter).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return dateTimeFormatter.format(instant);
	}

	/**
	 * LocalDateTime格式化
	 *
	 * @param localDateTime
	 * @param pattern
	 * @return
	 */
	public static String format(LocalDateTime localDateTime, DateTimeFormatter pattern) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return pattern.format(localDateTime);
	}

	/**
	 * LocalDateTime格式化
	 *
	 * @param localDateTime
	 * @param pattern
	 * @return
	 */
	public static String format(LocalDateTime localDateTime, String pattern) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		Optional.ofNullable(dateTimeFormatter).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return dateTimeFormatter.format(localDateTime);
	}

	/**
	 * LocalDate格式化
	 *
	 * @param localDate
	 * @param pattern
	 * @return
	 */
	public static String format(LocalDate localDate, DateTimeFormatter pattern) {
		Optional.ofNullable(localDate).orElseThrow(() -> new RuntimeException("localDate can not be null"));
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return format(localDateToLocalDateTimeConverter(localDate), pattern);
	}

	/**
	 * LocalDate格式化
	 *
	 * @param localDate
	 * @param pattern
	 * @return
	 */
	public static String format(LocalDate localDate, String pattern) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		Optional.ofNullable(localDate).orElseThrow(() -> new RuntimeException("localDate can not be null"));
		Optional.ofNullable(dateTimeFormatter).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return format(localDateToLocalDateTimeConverter(localDate), dateTimeFormatter);
	}

	/**
	 * LocalTime格式化
	 *
	 * @param localTime
	 * @param pattern
	 * @return
	 */
	public static String format(LocalTime localTime, DateTimeFormatter pattern) {
		Optional.ofNullable(localTime).orElseThrow(() -> new RuntimeException("localTime can not be null"));
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return format(localTimeToLocalDateTimeConverter(localTime), pattern);
	}

	/**
	 * LocalTime格式化
	 *
	 * @param localTime
	 * @param pattern
	 * @return
	 */
	public static String format(LocalTime localTime, String pattern) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		Optional.ofNullable(localTime).orElseThrow(() -> new RuntimeException("localTime can not be null"));
		Optional.ofNullable(dateTimeFormatter).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return format(localTimeToLocalDateTimeConverter(localTime), dateTimeFormatter);
	}

	/**
	 * 反格式化为java.util.Date
	 *
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static Date formatDate(String text, DateTimeFormatter pattern) {
		if (StringUtils.isEmpty(text)) {
			throw new RuntimeException("can not format String :" + text);
		}
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return localDateTimeToDateConverter(formatLocalDateTime(text, pattern));
	}

	/**
	 * 反格式化为java.util.Date
	 *
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static Date formatDate(String text, String pattern) {
		if (StringUtils.isEmpty(text)) {
			throw new RuntimeException("can not format String :" + text);
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		Optional.ofNullable(dateTimeFormatter).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return localDateTimeToDateConverter(formatLocalDateTime(text, dateTimeFormatter));
	}

	/**
	 * 反格式化为Instant
	 *
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static Instant formatInstant(String text, DateTimeFormatter pattern) {
		if (StringUtils.isEmpty(text)) {
			throw new RuntimeException("can not format String :" + text);
		}
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return localDateTimeToInstantConverter(formatLocalDateTime(text, pattern));
	}

	/**
	 * 反格式化为Instant
	 *
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static Instant formatInstant(String text, String pattern) {
		if (StringUtils.isEmpty(text)) {
			throw new RuntimeException("can not format String :" + text);
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		Optional.ofNullable(dateTimeFormatter).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		return localDateTimeToInstantConverter(formatLocalDateTime(text, dateTimeFormatter));
	}

	/**
	 * 反格式化LocalDateTime
	 *
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static LocalDateTime formatLocalDateTime(String text, DateTimeFormatter pattern) {
		if (StringUtils.isEmpty(text)) {
			throw new RuntimeException("can not format String :" + text);
		}
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		TemporalAccessor temporalAccessor = pattern.parse(text);
		try {
			Field date = temporalAccessor.getClass().getDeclaredField("date");
			date.setAccessible(true);
			if (!Optional.ofNullable(date.get(temporalAccessor)).isPresent()) {
				date.set(temporalAccessor, GREENWICH_MEAN_TIME);
			}
			Field time = temporalAccessor.getClass().getDeclaredField("time");
			time.setAccessible(true);
			if (!Optional.ofNullable(time.get(temporalAccessor)).isPresent()) {
				time.set(temporalAccessor, LocalTime.MIN);
			}
			return LocalDateTime.from(temporalAccessor);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("format error :" + e.getMessage());
		}
	}

	/**
	 * 反格式化LocalDateTime
	 *
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static LocalDateTime formatLocalDateTime(String text, String pattern) {
		if (StringUtils.isEmpty(text)) {
			throw new RuntimeException("can not format String :" + text);
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		return formatLocalDateTime(text, dateTimeFormatter);
	}


	/**
	 * 反格式化LocalDate
	 *
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static LocalDate formatLocalDate(String text, DateTimeFormatter pattern) {
		if (StringUtils.isEmpty(text)) {
			throw new RuntimeException("can not format String :" + text);
		}
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		try {
			return LocalDate.parse(text, pattern);
		} catch (DateTimeParseException exception) {
			throw new RuntimeException("why pattern does not have yyyymmdd? [" + pattern + "]");
		}
	}

	/**
	 * 反格式化LocalDate
	 *
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static LocalDate formatLocalDate(String text, String pattern) {
		if (StringUtils.isEmpty(text)) {
			throw new RuntimeException("can not format String :" + text);
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		return formatLocalDate(text, dateTimeFormatter);
	}

	/**
	 * 反格式化LocalTime
	 *
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static LocalTime formatLocalTime(String text, DateTimeFormatter pattern) {
		if (StringUtils.isEmpty(text)) {
			throw new RuntimeException("can not format String :" + text);
		}
		Optional.ofNullable(pattern).orElseThrow(() -> new RuntimeException("pattern can not be null"));
		try {
			return LocalTime.parse(text, pattern);
		} catch (DateTimeParseException exception) {
			throw new RuntimeException("why pattern does not have hhmmdd? [" + pattern + "]");
		}
	}

	/**
	 * 反格式化LocalTime
	 *
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static LocalTime formatLocalTime(String text, String pattern) {
		if (StringUtils.isEmpty(text)) {
			throw new RuntimeException("can not format String :" + text);
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, DEFAULT_LOCAL);
		return formatLocalTime(text, dateTimeFormatter);
	}

	/**
	 * 从java.util.Date获得年份
	 *
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		return getYear(dateToLocalDateTimeConverter(date));
	}

	/**
	 * 从Instant获得年份
	 *
	 * @param instant
	 * @return
	 */
	public static int getYear(Instant instant) {
		Optional.ofNullable(instant).orElseThrow(() -> new RuntimeException("instant can not be null"));
		return getYear(instantToLocalDateTimeConverter(instant));
	}

	/**
	 * 从LocalDateTime获得年份
	 *
	 * @param localDateTime
	 * @return
	 */
	public static int getYear(LocalDateTime localDateTime) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		return localDateTime.getYear();
	}

	/**
	 * 从LocalDate获得年份
	 *
	 * @param localDate
	 * @return
	 */
	public static int getYear(LocalDate localDate) {
		Optional.ofNullable(localDate).orElseThrow(() -> new RuntimeException("localDate can not be null"));
		return localDate.getYear();
	}

	/**
	 * 从java.util.Date获得月份
	 *
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		return getMonth(dateToLocalDateTimeConverter(date));
	}

	/**
	 * 从Instant获得月份
	 *
	 * @param instant
	 * @return
	 */
	public static int getMonth(Instant instant) {
		Optional.ofNullable(instant).orElseThrow(() -> new RuntimeException("instant can not be null"));
		return getMonth(instantToLocalDateTimeConverter(instant));
	}

	/**
	 * 从LocalDateTime获得月份
	 *
	 * @param localDateTime
	 * @return
	 */
	public static int getMonth(LocalDateTime localDateTime) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		return localDateTime.getMonth().getValue();
	}

	/**
	 * 从LocalDate获得月份
	 *
	 * @param localDate
	 * @return
	 */
	public static int getMonth(LocalDate localDate) {
		Optional.ofNullable(localDate).orElseThrow(() -> new RuntimeException("localDate can not be null"));
		return localDate.getMonth().getValue();
	}

	/**
	 * 从java.util.Date获得是当月几号
	 *
	 * @param date
	 * @return
	 */
	public static int getDayOfMonth(Date date) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		return getDayOfMonth(dateToLocalDateTimeConverter(date));
	}

	/**
	 * 从Instant获得是当月几号
	 *
	 * @param instant
	 * @return
	 */
	public static int getDayOfMonth(Instant instant) {
		Optional.ofNullable(instant).orElseThrow(() -> new RuntimeException("instant can not be null"));
		return getDayOfMonth(instantToLocalDateTimeConverter(instant));
	}

	/**
	 * 从LocalDateTime获得是当月几号
	 *
	 * @param localDateTime
	 * @return
	 */
	public static int getDayOfMonth(LocalDateTime localDateTime) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		return localDateTime.getDayOfMonth();
	}

	/**
	 * 从LocalDate获得是当月几号
	 *
	 * @param localDate
	 * @return
	 */
	public static int getDayOfMonth(LocalDate localDate) {
		Optional.ofNullable(localDate).orElseThrow(() -> new RuntimeException("localDate can not be null"));
		return localDate.getDayOfMonth();
	}

	/**
	 * 从java.util.Date获取小时
	 *
	 * @param date
	 * @return
	 */
	public static int getHour(Date date) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		return getHour(dateToLocalDateTimeConverter(date));
	}

	/**
	 * 从Instant获取小时
	 *
	 * @param instant
	 * @return
	 */
	public static int getHour(Instant instant) {
		Optional.ofNullable(instant).orElseThrow(() -> new RuntimeException("instant can not be null"));
		return getHour(instantToLocalDateTimeConverter(instant));
	}

	/**
	 * 从LocalDateTime获取小时
	 *
	 * @param localDateTime
	 * @return
	 */
	public static int getHour(LocalDateTime localDateTime) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		return localDateTime.getHour();
	}

	/**
	 * 从LocalTime获取小时
	 *
	 * @param localTime
	 * @return
	 */
	public static int getHour(LocalTime localTime) {
		Optional.ofNullable(localTime).orElseThrow(() -> new RuntimeException("localTime can not be null"));
		return localTime.getHour();
	}

	/**
	 * 从java.util.Date获取分钟
	 *
	 * @param date
	 * @return
	 */
	public static int getMinute(Date date) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		return getMinute(dateToLocalDateTimeConverter(date));
	}

	/**
	 * 从Instant获取分钟
	 *
	 * @param instant
	 * @return
	 */
	public static int getMinute(Instant instant) {
		Optional.ofNullable(instant).orElseThrow(() -> new RuntimeException("instant can not be null"));
		return getMinute(instantToLocalDateTimeConverter(instant));
	}

	/**
	 * 从LocalDateTime获取分钟
	 *
	 * @param localDateTime
	 * @return
	 */
	public static int getMinute(LocalDateTime localDateTime) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		return localDateTime.getMinute();
	}

	/**
	 * 从LocalTime获取分钟
	 *
	 * @param localTime
	 * @return
	 */
	public static int getMinute(LocalTime localTime) {
		Optional.ofNullable(localTime).orElseThrow(() -> new RuntimeException("localTime can not be null"));
		return localTime.getMinute();
	}

	/**
	 * 从java.util.Date获取秒数
	 *
	 * @param date
	 * @return
	 */
	public static int getSecond(Date date) {
		Optional.ofNullable(date).orElseThrow(() -> new RuntimeException("date can not be null"));
		return getMinute(dateToLocalDateTimeConverter(date));
	}

	/**
	 * 从Instant获取秒数
	 *
	 * @param instant
	 * @return
	 */
	public static int getSecond(Instant instant) {
		Optional.ofNullable(instant).orElseThrow(() -> new RuntimeException("instant can not be null"));
		return getSecond(instantToLocalDateTimeConverter(instant));
	}

	/**
	 * 从LocalDateTime获取秒数
	 *
	 * @param localDateTime
	 * @return
	 */
	public static int getSecond(LocalDateTime localDateTime) {
		Optional.ofNullable(localDateTime).orElseThrow(() -> new RuntimeException("localDateTime can not be null"));
		return localDateTime.getSecond();
	}

	/**
	 * 从LocalTime获取秒数
	 *
	 * @param localTime
	 * @return
	 */
	public static int getSecond(LocalTime localTime) {
		Optional.ofNullable(localTime).orElseThrow(() -> new RuntimeException("localTime can not be null"));
		return localTime.getSecond();
	}

	/**
	 * 获取两个日期的差值
	 *
	 * @param source
	 * @param target
	 * @param timeUnit
	 * @return
	 */
	public static long between(Date source, Date target, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(target).orElseThrow(() -> new RuntimeException("target can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return doBetween(dateToLocalDateTimeConverter(source), dateToLocalDateTimeConverter(target), timeUnit);
	}

	/**
	 * 获取两个日期的差值
	 *
	 * @param source
	 * @param target
	 * @param timeUnit
	 * @return
	 */
	public static long between(Instant source, Instant target, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(target).orElseThrow(() -> new RuntimeException("target can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return doBetween(instantToLocalDateTimeConverter(source), instantToLocalDateTimeConverter(target), timeUnit);
	}

	/**
	 * 获取两个日期的差值
	 *
	 * @param source
	 * @param target
	 * @param timeUnit
	 * @return
	 */
	public static long between(LocalDateTime source, LocalDateTime target, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(target).orElseThrow(() -> new RuntimeException("target can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return doBetween(source, target, timeUnit);
	}

	/**
	 * 获取两个日期的差值
	 *
	 * @param source
	 * @param target
	 * @param timeUnit
	 * @return
	 */
	public static long between(LocalDate source, LocalDate target, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(target).orElseThrow(() -> new RuntimeException("target can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		if (TimeUnit.DAYS != timeUnit) {
			throw new RuntimeException("LocalDate can not get between with :" + timeUnit.name());
		}
		return doBetween(localDateToLocalDateTimeConverter(source), localDateToLocalDateTimeConverter(target), timeUnit);
	}

	/**
	 * 获取两个日期的差值
	 *
	 * @param source
	 * @param target
	 * @param timeUnit
	 * @return
	 */
	public static long between(LocalTime source, LocalTime target, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(target).orElseThrow(() -> new RuntimeException("target can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		if (TimeUnit.DAYS == timeUnit) {
			throw new RuntimeException("LocalDate can not get between with :" + timeUnit.name());
		}
		return doBetween(localTimeToLocalDateTimeConverter(source), localTimeToLocalDateTimeConverter(target), timeUnit);
	}

	/**
	 * 获取日期距离当前的差值
	 *
	 * @param source
	 * @param timeUnit
	 * @return
	 */
	public static long betweenNow(Date source, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return between(dateToLocalDateTimeConverter(source), LocalDateTime.now(), timeUnit);
	}

	/**
	 * 获取日期距离当前的差值
	 *
	 * @param source
	 * @param timeUnit
	 * @return
	 */
	public static long betweenNow(Instant source, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return between(source, Instant.now(), timeUnit);
	}

	/**
	 * 获取日期距离当前的差值
	 *
	 * @param source
	 * @param timeUnit
	 * @return
	 */
	public static long betweenNow(LocalDateTime source, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return between(source, LocalDateTime.now(), timeUnit);
	}

	/**
	 * 获取日期距离当前的差值
	 *
	 * @param source
	 * @param timeUnit
	 * @return
	 */
	public static long betweenNow(LocalDate source, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return between(source, LocalDate.now(), timeUnit);
	}

	/**
	 * 获取日期距离当前的差值
	 *
	 * @param source
	 * @param timeUnit
	 * @return
	 */
	public static long betweenNow(LocalTime source, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return between(source, LocalTime.now(), timeUnit);
	}

	/**
	 * 获取两个日期的差值
	 *
	 * @param source
	 * @param target
	 * @param timeUnit
	 * @return
	 */
	private static long doBetween(LocalDateTime source, LocalDateTime target, TimeUnit timeUnit) {
		Duration duration = Duration.between(source, target);
		switch (timeUnit) {
			case DAYS:
				return Period.between(source.toLocalDate(), target.toLocalDate()).getDays();
			case HOURS:
				return duration.toHours();
			case MINUTES:
				return duration.toMinutes();
			case MILLISECONDS:
				return duration.toMillis();
			case SECONDS:
				return duration.toMillis() / 1000;
			case NANOSECONDS:
				return duration.toNanos();
			case MICROSECONDS:
				return duration.toMillis() * 1000;
			default:
				throw new RuntimeException("unknown timeUnit:" + timeUnit.name());
		}
	}

	/**
	 * 在基础时间上增加
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static Date plus(Date source, long amount, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		LocalDateTime localDateTime = plus(dateToLocalDateTimeConverter(source), amount, timeUnit);
		return localDateTimeToDateConverter(localDateTime);
	}

	/**
	 * 在基础时间上增加
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static Instant plus(Instant source, long amount, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return (Instant) doPlus(source, amount, timeUnit);
	}

	/**
	 * 在基础时间上增加
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static LocalDateTime plus(LocalDateTime source, long amount, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return (LocalDateTime) doPlus(source, amount, timeUnit);
	}

	/**
	 * 在基础时间上增加
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static LocalDate plus(LocalDate source, long amount, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		if (TimeUnit.DAYS != timeUnit) {
			throw new RuntimeException("LocalDate can not plus with :" + timeUnit.name());
		}
		return (LocalDate) doPlus(source, amount, timeUnit);
	}

	/**
	 * 在基础时间上增加
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static LocalTime plus(LocalTime source, long amount, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		if (TimeUnit.DAYS == timeUnit) {
			throw new RuntimeException("LocalDate can not plus with :" + timeUnit.name());
		}
		return (LocalTime) doPlus(source, amount, timeUnit);
	}

	/**
	 * 在当前时间上增加
	 *
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static <T> T plusNow(long amount, TimeUnit timeUnit, Class<T> tClass) {
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		Optional.ofNullable(tClass).orElseThrow(() -> new RuntimeException("tClass can not be null"));
		if (tClass.getName().equals(Date.class.getName())) {
			return (T) plus(new Date(), amount, timeUnit);
		} else if (tClass.getName().equals(Instant.class.getName())) {
			return (T) plus(Instant.now(), amount, timeUnit);
		} else if (tClass.getName().equals(LocalDateTime.class.getName())) {
			return (T) plus(Instant.now(), amount, timeUnit);
		} else if (tClass.getName().equals(LocalDate.class.getName())) {
			return (T) plus(Instant.now(), amount, timeUnit);
		} else if (tClass.getName().equals(LocalTime.class.getName())) {
			return (T) plus(Instant.now(), amount, timeUnit);
		} else {
			throw new RuntimeException("can not plus now with class:" + tClass.getName());
		}
	}

	/**
	 * 在基础时间上增加
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @param <T>
	 * @return
	 */
	private static <T extends TemporalAccessor> Temporal doPlus(Temporal source, long amount, TimeUnit timeUnit) {
		switch (timeUnit) {
			case DAYS:
				return source.plus(amount, ChronoUnit.DAYS);
			case HOURS:
				return source.plus(amount, ChronoUnit.HOURS);
			case MINUTES:
				return source.plus(amount, ChronoUnit.MINUTES);
			case MILLISECONDS:
				return source.plus(amount, ChronoUnit.MILLIS);
			case SECONDS:
				return source.plus(amount * 1000, ChronoUnit.MILLIS);
			case NANOSECONDS:
				return source.plus(amount, ChronoUnit.NANOS);
			case MICROSECONDS:
				return source.plus(amount / 1000, ChronoUnit.MILLIS);
			default:
				throw new RuntimeException("unknown timeUnit:" + timeUnit.name());
		}
	}

	/**
	 * 在基础时间上减少
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static Date minus(Date source, long amount, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		LocalDateTime localDateTime = minus(dateToLocalDateTimeConverter(source), amount, timeUnit);
		return localDateTimeToDateConverter(localDateTime);
	}

	/**
	 * 在基础时间上减少
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static Instant minus(Instant source, long amount, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return (Instant) doMinus(source, amount, timeUnit);
	}

	/**
	 * 在基础时间上减少
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static LocalDateTime minus(LocalDateTime source, long amount, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		return (LocalDateTime) doMinus(source, amount, timeUnit);
	}

	/**
	 * 在基础时间上减少
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static LocalDate minus(LocalDate source, long amount, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		if (TimeUnit.DAYS != timeUnit) {
			throw new RuntimeException("LocalDate can not minus with :" + timeUnit.name());
		}
		return (LocalDate) doMinus(source, amount, timeUnit);
	}

	/**
	 * 在基础时间上减少
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static LocalTime minus(LocalTime source, long amount, TimeUnit timeUnit) {
		Optional.ofNullable(source).orElseThrow(() -> new RuntimeException("source can not be null"));
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		if (TimeUnit.DAYS == timeUnit) {
			throw new RuntimeException("LocalDate can not minus with :" + timeUnit.name());
		}
		return (LocalTime) doMinus(source, amount, timeUnit);
	}

	/**
	 * 在当前时间上增加
	 *
	 * @param amount
	 * @param timeUnit
	 * @return
	 */
	public static <T> T minusNow(long amount, TimeUnit timeUnit, Class<T> tClass) {
		Optional.ofNullable(amount).orElseThrow(() -> new RuntimeException("amount can not be null"));
		Optional.ofNullable(timeUnit).orElseThrow(() -> new RuntimeException("timeUnit can not be null"));
		Optional.ofNullable(tClass).orElseThrow(() -> new RuntimeException("tClass can not be null"));
		if (tClass.getName().equals(Date.class.getName())) {
			return (T) minus(new Date(), amount, timeUnit);
		} else if (tClass.getName().equals(Instant.class.getName())) {
			return (T) minus(Instant.now(), amount, timeUnit);
		} else if (tClass.getName().equals(LocalDateTime.class.getName())) {
			return (T) minus(Instant.now(), amount, timeUnit);
		} else if (tClass.getName().equals(LocalDate.class.getName())) {
			return (T) minus(Instant.now(), amount, timeUnit);
		} else if (tClass.getName().equals(LocalTime.class.getName())) {
			return (T) minus(Instant.now(), amount, timeUnit);
		} else {
			throw new RuntimeException("can not minus now with class:" + tClass.getName());
		}
	}

	/**
	 * 在基础时间上减少
	 *
	 * @param source
	 * @param amount
	 * @param timeUnit
	 * @param <T>
	 * @return
	 */
	private static <T extends TemporalAccessor> Temporal doMinus(Temporal source, long amount, TimeUnit timeUnit) {
		switch (timeUnit) {
			case DAYS:
				return source.minus(amount, ChronoUnit.DAYS);
			case HOURS:
				return source.minus(amount, ChronoUnit.HOURS);
			case MINUTES:
				return source.minus(amount, ChronoUnit.MINUTES);
			case MILLISECONDS:
				return source.minus(amount, ChronoUnit.MILLIS);
			case SECONDS:
				return source.minus(amount * 1000, ChronoUnit.MILLIS);
			case NANOSECONDS:
				return source.minus(amount, ChronoUnit.NANOS);
			case MICROSECONDS:
				return source.minus(amount / 1000, ChronoUnit.MILLIS);
			default:
				throw new RuntimeException("unknown timeUnit:" + timeUnit.name());
		}
	}

	/**
	 * 对时间进行类型转换
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <D extends Date, R extends Date> R castTo(D value, Class<R> clazz) {
		if (value == null) {
			return null;
		}
		if (clazz.equals(Date.class)) {
			return (R) value;
		} else if (clazz.equals(java.sql.Date.class)) {
			return (R) new java.sql.Date(value.getTime());
		} else if (clazz.equals(Timestamp.class)) {
			return (R) new Timestamp(value.getTime());
		}
		throw new RuntimeException(String.format("Value type(%s) error for cast to type(%s)", value.getClass(), clazz.getClass()));
	}

	/**
	 * @param object
	 * @return {@link Date}
	 * @description 将其他类型的Date对象转换为java.util.Date对象
	 * @author yida
	 * @date 2023-09-01 09:06:13
	 */
	public static <T> Date convertToUtilDate(T object) {
		if (null == object) {
			return null;
		}
		Class<T> objectClass = (Class<T>) object.getClass();
		if (Date.class.equals(objectClass)) {
			return (Date) object;
		}
		if (java.sql.Date.class.equals(objectClass)) {
			return new Date(((java.sql.Date) object).getTime());
		}
		if (Timestamp.class.equals(objectClass)) {
			return new Date(((Timestamp) object).getTime());
		}
		if (java.sql.Time.class.equals(objectClass)) {
			return new Date(((java.sql.Time) object).getTime());
		}
		if (LocalDate.class.equals(objectClass)) {
			return localDateToDateConverter(((LocalDate) object));
		}
		if (LocalDateTime.class.equals(objectClass)) {
			return localDateTimeToDateConverter(((LocalDateTime) object));
		}
		if (LocalTime.class.equals(objectClass)) {
			return localTimeToDateConverter(((LocalTime) object));
		}
		return null;
	}

	/**
	 * 将毫秒数转换成月
	 *
	 * @param millsecond
	 * @return
	 */

	public static float toMonth(long millsecond) {
		return (millsecond) / ((float) 3600 * 1000 * 24 * 30);
	}


	/**
	 * 将Date加上日期 	年	月	日
	 *
	 * @param date
	 * @param y
	 * @param m
	 * @param d
	 * @return
	 */

	public static Date addDate(Date date, int y, int m, int d) {
		Calendar c = getCalendar(date);
		c.add(Calendar.YEAR, y);
		c.add(Calendar.MONTH, m);
		c.add(Calendar.DAY_OF_MONTH, d);
		return c.getTime();
	}

	/**
	 * 将Date加上时间	时	分	秒
	 *
	 * @param date
	 * @param h
	 * @param m
	 * @param s
	 * @return
	 */
	public static Date addTime(Date date, int h, int m, int s) {
		Calendar c = getCalendar(date);
		c.add(Calendar.HOUR_OF_DAY, h);
		c.add(Calendar.MINUTE, m);
		c.add(Calendar.SECOND, s);
		return c.getTime();
	}

	/**
	 * 将Date加上毫秒
	 */
	public static Date addMilliSecond(Date date, int ms) {
		Calendar c = getCalendar(date);
		c.add(Calendar.MILLISECOND, ms);
		return c.getTime();
	}

	/**
	 * 设置Date的 日期,	为-1则表示不设置
	 *
	 * @param date
	 * @param y    从1开始
	 * @param m    注意: 从0开始. 1表示1月
	 * @param d    从1开始
	 * @return
	 */
	public static Date setDate(Date date, int y, int m, int d) {
		Calendar c = getCalendar(date);
		if (y > -1) c.set(Calendar.YEAR, y);
		if (m > -1) c.set(Calendar.MONTH, m);
		if (d > -1) c.set(Calendar.DAY_OF_MONTH, d);
		return c.getTime();
	}

	/**
	 * 设置Date的 时间	为-1则表示不设置
	 *
	 * @param date
	 * @param h
	 * @param m
	 * @param s
	 * @return
	 */
	public static Date setTime(Date date, int h, int m, int s) {
		Calendar c = getCalendar(date);
		if (h > -1) c.set(Calendar.HOUR_OF_DAY, h);
		if (m > -1) c.set(Calendar.MINUTE, m);
		if (s > -1) c.set(Calendar.SECOND, s);
		return c.getTime();
	}

	/**
	 * 返回符合中国时间的Calendar
	 */
	public static Calendar getCalendar(Date date) {
		//		if(date==null)date=new Date();
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		if (date != null) c.setTime(date);
		return c;
	}

	/**
	 * 清除时间部分,设置成 00:00:00.000
	 */
	public static Calendar setTimeFirst(Calendar c) {
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		return c;
	}

	public static Timestamp setTimeFirst(Timestamp d) {
		Calendar c = getCalendar(d);
		setTimeFirst(c);
		return new Timestamp(c.getTimeInMillis());
	}

	public static java.sql.Date setTimeFirst(java.sql.Date d) {
		Calendar c = getCalendar(d);
		setTimeFirst(c);
		return new java.sql.Date(c.getTimeInMillis());
	}

	/**
	 * 清除时间部分,设置成 00:00:00.000
	 */
	public static Date setTimeFirst(Date d) {
		Calendar c = getCalendar(d);
		setTimeFirst(c);
		return c.getTime();
	}

	/**
	 * 将时间部分设置成一天的最后, 23:59:59.999
	 */
	public static Calendar setTimeLast(Calendar c) {
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.HOUR_OF_DAY, 23);
		return c;
	}

	public static Timestamp setTimeLast(Timestamp d) {
		Calendar c = getCalendar(d);
		setTimeLast(c);
		return new Timestamp(c.getTimeInMillis());
	}

	public static java.sql.Date setTimeLast(java.sql.Date d) {
		Calendar c = getCalendar(d);
		setTimeLast(c);
		return new java.sql.Date(c.getTimeInMillis());
	}

	/**
	 * 将时间部分设置成一天的最后, 23:59:59.999
	 */
	public static Date setTimeLast(Date d) {
		Calendar c = getCalendar(d);
		setTimeLast(c);
		return c.getTime();
	}

	/**
	 * 获取昨天
	 *
	 * @param date
	 * @return
	 */
	public static Date getPrevDay(Date date) {
		Calendar c = getCalendar(date);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	/**
	 * 获取明天
	 *
	 * @param date
	 * @return
	 */
	public static Date getNextDay(Date date) {
		Calendar c = getCalendar(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	/**
	 * 本月开始		格式:2000-MM-1 00:00:00
	 */
	public static Date getMonthFirst(Date date) {
		Calendar c = getCalendar(date);
		setTimeFirst(c);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	/**
	 * 上月开始			格式:2000-MM-1 00:00:00
	 */
	public static Date getPrevMonthFirst(Date date) {
		Calendar c = getCalendar(date);
		setTimeFirst(c);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.MONTH, -1);
		return c.getTime();
	}

	/**
	 * 下月开始			格式:2000-MM-1 00:00:00
	 */
	public static Date getNextMonthFirst(Date date) {
		Calendar c = getCalendar(date);
		setTimeFirst(c);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.MONTH, 1);
		return c.getTime();
	}

	/**
	 * 本月结束		格式:2000-MM-31 23:59:59.999
	 */
	public static Date getMonthLast(Date date) {
		Calendar c = getCalendar(date);
		setTimeLast(c);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return c.getTime();
	}

	/**
	 * 下月结束		格式:2000-MM-31 23:59:59.999
	 */
	public static Date getNextMonthLast(Date date) {
		Calendar c = getCalendar(date);
		setTimeLast(c);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		c.add(Calendar.MONTH, 1);
		return c.getTime();
	}

	/***
	 * 上月结束		格式:2000-MM-31 23:59:59.999
	 */
	public static Date getPrevMonthLast(Date date) {
		Calendar c = getCalendar(date);
		setTimeLast(c);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		c.add(Calendar.MONTH, -1);
		return c.getTime();
	}

	/**
	 * 本周开始		格式:2000-MM-1 00:00:00
	 */
	public static Date getWeekFirst(Date date) {
		Calendar c = getCalendar(date);
		setTimeFirst(c);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return c.getTime();
	}

	/**
	 * 上月开始		格式:2000-MM-1 00:00:00
	 */
	public static Date getPrevWeekFirst(Date date) {
		Calendar c = getCalendar(date);
		setTimeFirst(c);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		c.add(Calendar.WEEK_OF_YEAR, -1);
		return c.getTime();
	}

	/**
	 * 下周开始		格式:2000-MM-1 00:00:00
	 */
	public static Date getNextWeekFirst(Date date) {
		Calendar c = getCalendar(date);
		setTimeFirst(c);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		c.add(Calendar.WEEK_OF_YEAR, 1);
		return c.getTime();
	}

	/**
	 * 本周结束		格式:2000-MM-31 23:59:59.999
	 */
	public static Date getWeekLast(Date date) {
		Calendar c = getCalendar(date);
		setTimeLast(c);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return c.getTime();
	}

	/**
	 * 下周结束		格式:2000-MM-31 23:59:59.999
	 */
	public static Date getNextWeekLast(Date date) {
		Calendar c = getCalendar(date);
		setTimeLast(c);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		c.add(Calendar.WEEK_OF_YEAR, 1);
		return c.getTime();
	}

	/**
	 * 上周结束		格式:2000-MM-31 23:59:59.999
	 */
	public static Date getPrevWeekLast(Date date) {
		Calendar c = getCalendar(date);
		setTimeLast(c);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		c.add(Calendar.WEEK_OF_YEAR, -1);
		return c.getTime();
	}

	/**
	 * 本年开始		格式:2000-MM-1 00:00:00
	 */
	public static Date getYearFirst(Date date) {
		Calendar c = getCalendar(date);
		setTimeFirst(c);
		c.set(Calendar.DAY_OF_YEAR, 1);
		return c.getTime();
	}

	/**
	 * 上年开始		格式:2000-MM-1 00:00:00
	 */
	public static Date getPrevYearFirst(Date date) {
		Calendar c = getCalendar(date);
		setTimeFirst(c);
		c.set(Calendar.DAY_OF_YEAR, 1);
		c.add(Calendar.YEAR, -1);
		return c.getTime();
	}

	/**
	 * 下年开始		格式:2000-MM-1 00:00:00
	 */
	public static Date getNextYearFirst(Date date) {
		Calendar c = getCalendar(date);
		setTimeFirst(c);
		c.set(Calendar.DAY_OF_YEAR, 1);
		c.add(Calendar.YEAR, 1);
		return c.getTime();
	}

	/**
	 * 本年结束		格式:2000-MM-31 23:59:59.999
	 */
	public static Date getYearLast(Date date) {
		Calendar c = getCalendar(date);
		setTimeLast(c);
		c.set(Calendar.DAY_OF_YEAR, c.getActualMaximum(Calendar.DAY_OF_YEAR));
		return c.getTime();
	}

	/**
	 * 下年结束		格式:2000-MM-31 23:59:59.999
	 */
	public static Date getNextYearLast(Date date) {
		Calendar c = getCalendar(date);
		setTimeLast(c);
		c.set(Calendar.DAY_OF_YEAR, c.getActualMaximum(Calendar.DAY_OF_YEAR));
		c.add(Calendar.YEAR, 1);
		return c.getTime();
	}

	/**
	 * 上年结束		格式:2000-MM-31 23:59:59.999
	 */
	public static Date getPrevYearLast(Date date) {
		Calendar c = getCalendar(date);
		setTimeLast(c);
		c.set(Calendar.DAY_OF_YEAR, c.getActualMaximum(Calendar.DAY_OF_YEAR));
		c.add(Calendar.YEAR, -1);
		return c.getTime();
	}

	/**
	 * 验证字符串是否是时间格式. 如果为空则返回false
	 *
	 * @param dateString
	 * @return
	 */
	public static boolean checkDate(String dateString) {
		try {
			if (StringUtils.isNotEmpty(dateString)) {
				return parseDate(dateString) != null;
			}
		} catch (Exception e) {
			logger.error("As checkDate for string:[{}] occur exception:\n{}.", dateString, e.getMessage());
		}
		return false;
	}

	/**
	 * 将对象转换成Date,格式  2012-12-12 或者 2012-12-12 12:12:12.转换错误则返回null
	 *
	 * @param s
	 * @return
	 */
	public static Date parseDate(String s) {
		return parseDate(s, null);
	}

	/**
	 * 转换日期并且将日期设置成当天  23:59:59 999
	 *
	 * @param s
	 * @return
	 */
	public static Date parseDateEnd(String s) {
		return parseDateEnd(s, null);
	}

	/**
	 * 转换日期并且将日期设置成当天  23:59:59 999
	 *
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static Date parseDateEnd(String s, String pattern) {
		Date d = parseDate(s, pattern);
		if (d == null) return null;
		return setTimeLast(d);
	}

	/**
	 * 解析时间,pattern为空时 格式为 2011-01-11 13:12:12	转换错误时返回null
	 *
	 * @param s       字符串默认将 / 替换成 -
	 * @param pattern 指定解析格式,为空时使用默认格式
	 * @return
	 */
	public static Date parseDate(String s, String pattern) {
		if (StringUtils.checkEmpty(s)) {
			s = s.replace('/', '-');
			DateFormat df = null;
			if (pattern == null || pattern.length() < 1) {
				//如果不存在,格式则可能是纯数字
				if (s.indexOf('-') < 0 && s.lastIndexOf(':') < 0) {
					return new Date(StringUtils.toLong(s, 0));
				} else {
					if (StringUtils.getLength(s) > 11) {
						df = createDataFormat(datetimeFormatText);
					} else {
						df = createDataFormat(dateFormatText);
					}
				}

			} else {
				df = new SimpleDateFormat(pattern);
			}
			try {
				return df.parse(s.toString());
			} catch (ParseException e) {

			}
		}
		return null;
	}

	/**
	 * 将字符串按照无符号日期时间转换,格式:20110101120000000	日期+时间+毫秒
	 *
	 * @param s
	 * @return
	 */
	public static Date parseDateNoSeparator(String s) {
		try {
			s = StringUtils.alignRight(s, 17, '0');
			return createDataFormat(datetimeFormatNosepText).parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param d
	 * @param format
	 * @return
	 */
	public static String toDate(Date d, String format) {
		if (d == null) return "";
		if (!StringUtils.checkEmpty(format)) {
			format = dateFormatText;
		}
		return createDataFormat(format).format(d);
	}

	/**
	 * 获取日期时间, 格式 2011-01-10 12:12:12 .如果为null则返回空字符串
	 *
	 * @return
	 */

	public static String toDateTime(Date d) {
		if (d == null) return "";
		return createDataFormat(datetimeFormatText).format(d);
	}

	/**
	 * 获取指定格式的日期时间 .如果为null则返回空字符串
	 *
	 * @param d
	 * @param format
	 * @return
	 */
	public static String toDateTime(Date d, String format) {
		if (d == null) return "";
		DateFormat datetimeFormat = new SimpleDateFormat(format);
		return datetimeFormat.format(d);
	}

	/**
	 * 获取无分隔符的日期时间+毫秒,格式 20110101120000000
	 *
	 * @param d
	 * @return
	 */
	public static String toDateTimeNoSeparator(Date d) {
		if (d == null) return "";
		return createDataFormat(datetimeFormatNosepText).format(d);
	}

	/**
	 * 获取日期,格式 2011-01-01
	 *
	 * @return
	 */
	public static String toDate(Date d) {
		if (d == null) return "";
		return createDataFormat(dateFormatText).format(d);
	}

	/**
	 * 获取日期无分隔符,格式 20110101
	 *
	 * @return
	 */
	public static String toDateNoSeparator(Date d) {
		if (d == null) return "";
		return createDataFormat(datetimeFormatNosepText).format(d).substring(0, 8);
	}

	/**
	 * 获取时间,格式 13:02:12
	 *
	 * @return
	 */
	public static String toTime(Date d) {
		if (d == null) return "";
		return createDataFormat(timeFormatText).format(d);
	}

	/**
	 * 获取时间无分隔符,格式 130212
	 *
	 * @return
	 */
	public static String toTimeNoSeparator(Date d) {
		if (d == null) return "";
		return createDataFormat(datetimeFormatNosepText).format(d).substring(8, 14);
	}

	/**
	 * 获取Date的年,为null时表示当前
	 */
	public static int getDateYear(Date d) {
		Calendar c = getCalendar(d);
		return c.get(Calendar.YEAR);
	}

	/**
	 * 获取Date的月,为null时表示当前.注意:从0开始, 0表示1月份
	 */
	public static int getDateMonth(Date d) {
		Calendar c = getCalendar(d);
		return c.get(Calendar.MONTH);
	}

	/**
	 * 获取Date的日,为null时表示当前
	 */
	public static int getDay(Date d) {
		Calendar c = getCalendar(d);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取Date的小时,为null时表示当前
	 */
	public static int getDateTimeHour(Date d) {
		Calendar c = getCalendar(d);
		return c.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取Date的分,为null时表示当前
	 */
	public static int getDateTimeMinute(Date d) {
		Calendar c = getCalendar(d);
		return c.get(Calendar.MINUTE);
	}

	/**
	 * 获取Date的秒,为null时表示当前
	 */
	public static int getDateTimeSecond(Date d) {
		Calendar c = getCalendar(d);
		return c.get(Calendar.SECOND);
	}

	/**
	 * 获取Date的毫秒,为null时表示当前
	 */
	public static int getMillisecond(Date d) {
		Calendar c = getCalendar(d);
		return c.get(Calendar.MILLISECOND);
	}

	/**
	 * 获取Date的当前年的第几周
	 *
	 * @param d
	 * @return
	 */
	public static int getYearWeek(Date d) {
		Calendar c = getCalendar(d);
		return c.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * 获取时间的指定属性
	 *
	 * @param d
	 * @param field
	 * @return
	 */
	public static int getField(Date d, int field) {
		Calendar c = getCalendar(d);
		return c.get(field);
	}

	/**
	 * 时间相减. 返回差距毫秒
	 *
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static long subDateTime(Date d1, Date d2) {
		return (d1 == null ? 0 : d1.getTime()) - (d2 == null ? 0 : d2.getTime());
	}

	/**
	 * 时间相减. 返回相差天数
	 *
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double subDateTimeDay(Date d1, Date d2) {
		long d = subDateTime(d1, d2);
		return ((double) d) / (3600 * 1000 * 24);
	}

	/**
	 * 时间相减. 返回相差小时数
	 *
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double subDateTimeHour(Date d1, Date d2) {
		long d = subDateTime(d1, d2);
		return ((double) d) / (3600 * 1000);
	}

	/**
	 * 获取日期的数字形式,当前时间清除掉 时分秒的数值
	 *
	 * @param d
	 * @return
	 */

	public static long getDateNum(Date d) {
		return setTimeFirst(d).getTime();
	}

	/**
	 * 构建月时间序列
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<Date> getMonthSequence(Date start, Date end) {
		List<Date> list = new ArrayList<Date>();
		start = getMonthFirst(start);
		end = getMonthLast(end);
		long sl = start.getTime();
		long el = end.getTime();
		if (sl >= el) return list;

		Date d = null;
		while (sl < el) {
			d = new Date(sl);
			list.add(d);
			d = addDate(d, 0, 1, 0);
			sl = d.getTime();
		}

		return list;
	}

	/**
	 * 构建月时间序列
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<String> getMonthSequences(Date start, Date end) {
		List<Date> list = getMonthSequence(start, end);
		List<String> result = new ArrayList<String>();
		for (Date d : list) {
			result.add(toDateTime(d, "yyyy-MM"));
		}
		return result;
	}

	/**
	 * 构建天时间序列
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<Date> getDaySequence(Date start, Date end) {
		List<Date> list = new ArrayList<Date>();
		start = setTimeFirst(start);
		end = setTimeLast(end);
		long sl = start.getTime();
		long el = end.getTime();
		if (sl >= el) return list;

		Date d = null;
		while (sl < el) {
			d = new Date(sl);
			list.add(d);
			d = addDate(d, 0, 0, 1);
			sl = d.getTime();
		}

		return list;
	}

	/**
	 * 构建天时间序列
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<String> getDaySequences(Date start, Date end) {
		List<Date> list = getDaySequence(start, end);
		List<String> result = new ArrayList<String>();
		for (Date d : list) {
			result.add(toDateTime(d, "yyyy-MM-dd"));
		}
		return result;
	}

	/**
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date addDay(Date date, int day) {
		Calendar c = getCalendar(date);
		c.add(Calendar.DAY_OF_MONTH, day);
		return c.getTime();
	}

	private static DateFormat createDataFormat(String text) {
		return new SimpleDateFormat(text);
	}

	/**
	 * @param beginDate
	 * @param endDate
	 * @return String
	 * @description 返回两个日期差的友好显示文本
	 * @author yida
	 * @date 2023-11-23 12:29:35
	 */
	public static String showDateDiffText(Date beginDate, Date endDate) {
		long start = beginDate.getTime();
		long end = endDate.getTime();
		if (end <= start) {
			return "0ms";
		}
		long diff = end - start;
		if (diff > 0L && diff < 1000L) {
			return showMsLevelText(diff);
		}
		if (diff >= 1000L && diff < 60000L) {
			return showSecondLevelText(diff);
		}
		if (diff >= 60000L && diff < 3600000L) {
			return showMinLevelText(diff);
		}
		if (diff >= 3600000L && diff < 86400000L) {
			return showHourLevelText(diff);
		}
		if (diff >= 86400000L) {
			return showDayLevelText(diff);
		}
		return "0ms";
	}

	private static String showMsLevelText(long diff) {
		if (diff <= 0) {
			return "";
		}
		return String.valueOf(diff) + "ms";
	}

	private static String showSecondLevelText(long diff) {
		long val = diff / 1000L;
		long ms = diff % 1000L;
		if (ms > 0) {
			return String.valueOf(val) + "s " + String.valueOf(ms) + "ms";
		}
		return String.valueOf(val) + "s";
	}

	private static String showMinLevelText(long diff) {
		long min = diff / 60000L;
		long remainMs = diff % 60000L;
		if (remainMs > 0L && remainMs < 1000L) {
			String msLevelText = showMsLevelText(remainMs);
			return String.valueOf(min) + "min " + msLevelText;
		}
		if (remainMs >= 1000L && remainMs < 60000L) {
			String secondLevelText = showSecondLevelText(remainMs);
			return String.valueOf(min) + "min " + secondLevelText;
		}
		return String.valueOf(min) + "min";
	}

	private static String showHourLevelText(long diff) {
		long hour = diff / 3600000L;
		long remainMs = diff % 3600000L;
		if (remainMs > 0L && remainMs < 1000L) {
			String msLevelText = showMsLevelText(remainMs);
			return String.valueOf(hour) + "h " + msLevelText;
		}
		if (remainMs >= 1000L && remainMs < 60000L) {
			String secondLevelText = showSecondLevelText(remainMs);
			return String.valueOf(hour) + "h " + secondLevelText;
		}
		if (remainMs >= 60000L && remainMs < 3600000L) {
			String minLevelText = showMinLevelText(remainMs);
			return String.valueOf(hour) + "h " + minLevelText;
		}
		return String.valueOf(hour) + "h";
	}

	private static String showDayLevelText(long diff) {
		long day = diff / 3600000L;
		long remainMs = diff % 3600000L;
		if (remainMs > 0L && remainMs < 1000L) {
			String msLevelText = showMsLevelText(remainMs);
			return String.valueOf(day) + "day " + msLevelText;
		}
		if (remainMs >= 1000L && remainMs < 60000L) {
			String secondLevelText = showSecondLevelText(remainMs);
			return String.valueOf(day) + "day " + secondLevelText;
		}
		if (remainMs >= 60000L && remainMs < 3600000L) {
			String minLevelText = showMinLevelText(remainMs);
			return String.valueOf(day) + "day " + minLevelText;
		}
		if (remainMs >= 3600000L && remainMs < 86400000L) {
			String hourLevelText = showHourLevelText(remainMs);
			return String.valueOf(day) + "day " + hourLevelText;
		}
		return String.valueOf(day) + "day";
	}
}
