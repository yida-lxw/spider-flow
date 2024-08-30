package org.spiderflow.core.thread;

/**
 * 线程池拒绝策略枚举
 */
public enum ThreadPoolRejectPolicy {
	/**
	 * 在调用者线程上执行当前任务
	 */
	CALLER_RUNS(1, "caller-runs"),
	/**
	 * 直接中断处理，抛出RejectedExecutionException异常给调用者
	 */
	ABORT(2, "abort"),
	/**
	 * 直接丢弃当前任务
	 */
	DISCARD(3, "discard"),
	/**
	 * 丢弃最旧的任务
	 */
	DISCARD_OLDEST(4, "discard-oldest"),
	/**
	 * 对任务入队操作进行阻塞，直到队列容量未满为止
	 */
	BLOCKING(5, "blocking");


	public static final String POLICY_KEY_CALLER_RUNS = "caller-runs";
	public static final String POLICY_KEY_ABORT = "abort";
	public static final String POLICY_KEY_DISCARD = "discard";
	public static final String POLICY_KEY_DISCARD_OLDEST = "discard-oldest";
	public static final String POLICY_KEY_BLOCKING = "blocking";

	public static final ThreadPoolRejectPolicy POLICY_CALLER_RUNS = ThreadPoolRejectPolicy.CALLER_RUNS;
	public static final ThreadPoolRejectPolicy POLICY_ABORT = ThreadPoolRejectPolicy.ABORT;
	public static final ThreadPoolRejectPolicy POLICY_DISCARD = ThreadPoolRejectPolicy.DISCARD;
	public static final ThreadPoolRejectPolicy POLICY_DISCARD_OLDEST = ThreadPoolRejectPolicy.DISCARD_OLDEST;
	public static final ThreadPoolRejectPolicy POLICY_BLOCKING = ThreadPoolRejectPolicy.BLOCKING;

	private int code;

	private String key;

	ThreadPoolRejectPolicy(int code, String key) {
		this.code = code;
		this.key = key;
	}

	public static ThreadPoolRejectPolicy of(String policyKey) {
		ThreadPoolRejectPolicy threadPoolRejectPolicy = null;
		if (POLICY_KEY_CALLER_RUNS.equals(policyKey)) {
			threadPoolRejectPolicy = POLICY_CALLER_RUNS;
		} else if (POLICY_KEY_ABORT.equals(policyKey)) {
			threadPoolRejectPolicy = POLICY_ABORT;
		} else if (POLICY_KEY_DISCARD.equals(policyKey)) {
			threadPoolRejectPolicy = POLICY_DISCARD;
		} else if (POLICY_KEY_DISCARD_OLDEST.equals(policyKey)) {
			threadPoolRejectPolicy = POLICY_DISCARD_OLDEST;
		} else if (POLICY_KEY_BLOCKING.equals(policyKey)) {
			threadPoolRejectPolicy = POLICY_BLOCKING;
		} else {
			throw new IllegalArgumentException("Unkown threadpool reject policy key:[" + policyKey +
					"] to build ThreadPoolRejectPolicy Enum.");
		}
		return threadPoolRejectPolicy;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
