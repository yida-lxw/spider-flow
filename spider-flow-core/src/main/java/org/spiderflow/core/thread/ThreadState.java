package org.spiderflow.core.thread;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yida
 * @date 2023-08-28 09:45
 * @description 线程状态枚举
 */
public enum ThreadState {
	NEW(0, ThreadState.NEW_KEY),
	RUNNING(1, ThreadState.RUNNING_KEY),
	BLOCKING(2, ThreadState.BLOCKING_KEY),
	SUCCESS(3, ThreadState.SUCCESS_KEY),
	FAILED(4, ThreadState.FAILED_KEY);

	private int code;
	private String val;

	ThreadState(int code, String val) {
		this.code = code;
		this.val = val;
	}

	public static final String NEW_KEY = "new";
	public static final String RUNNING_KEY = "running";
	public static final String BLOCKING_KEY = "blocking";
	public static final String SUCCESS_KEY = "success";
	public static final String FAILED_KEY = "failed";

	public static final Set<String> threadStateSet = new HashSet<>();

	/**
	 * 默认的线程状态为NEW
	 */
	public static final ThreadState DEFAULT_THREAD_STATE = NEW;

	static {
		threadStateSet.add(NEW_KEY);
		threadStateSet.add(RUNNING_KEY);
		threadStateSet.add(BLOCKING_KEY);
		threadStateSet.add(SUCCESS_KEY);
		threadStateSet.add(FAILED_KEY);
	}

	public static ThreadState of(String threadStateValue) {
		if (null == threadStateValue || "".equals(threadStateValue)) {
			throw new IllegalArgumentException("threadStateValue MUST NOT be null or empty.");
		}
		if (!threadStateSet.contains(threadStateValue)) {
			throw new UnsupportedOperationException("threadStateValue MUST BE in the threadStateSet.");
		}
		if (NEW_KEY.equals(threadStateValue)) {
			return NEW;
		}
		if (RUNNING_KEY.equals(threadStateValue)) {
			return RUNNING;
		}
		if (BLOCKING_KEY.equals(threadStateValue)) {
			return BLOCKING;
		}
		if (SUCCESS_KEY.equals(threadStateValue)) {
			return SUCCESS;
		}
		return FAILED;
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
