package org.spiderflow.model;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpiderLog {
	private String level;
	private String loggerName;
	private long threadId;
	private String threadName;
	private long eventTime;
	private String message;
	private Map<String, String> contextDataMap;

	public SpiderLog(String level, String loggerName, long threadId, String threadName, long eventTime, String message, Map<String, String> contextDataMap) {
		this.level = level;
		this.loggerName = loggerName;
		this.threadId = threadId;
		this.threadName = threadName;
		this.eventTime = eventTime;
		this.message = message;
		this.contextDataMap = contextDataMap;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, String> getContextDataMap() {
		return contextDataMap;
	}

	public void setContextDataMap(Map<String, String> contextDataMap) {
		this.contextDataMap = contextDataMap;
	}
}
