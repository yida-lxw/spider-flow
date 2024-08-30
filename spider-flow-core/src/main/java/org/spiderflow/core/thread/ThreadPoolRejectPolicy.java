package org.spiderflow.core.thread;

import org.spiderflow.core.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池拒绝策略枚举
 */
public enum ThreadPoolRejectPolicy {
	/**
	 * 在调用者线程上执行当前任务
	 */
	CALLER_RUNS(1, "caller-runs", new ThreadPoolExecutor.CallerRunsPolicy()),
	/**
	 * 直接中断处理，抛出RejectedExecutionException异常给调用者
	 */
	ABORT(2, "abort", new ThreadPoolExecutor.AbortPolicy()),
	/**
	 * 直接丢弃当前任务
	 */
	DISCARD(3, "discard", new ThreadPoolExecutor.DiscardPolicy()),
	/**
	 * 丢弃最旧的任务(即队列的头节点)
	 */
	DISCARD_OLDEST(4, "discard-oldest", new ThreadPoolExecutor.DiscardOldestPolicy()),
	/**
	 * 对任务入队操作进行阻塞，直到队列容量未满为止
	 */
	BLOCKING(5, "blocking", new BlockingPolicy());

	public static final ThreadPoolRejectPolicy DEFAULT_THREAD_POOL_REJECT_POLICY = ThreadPoolRejectPolicy.ABORT;

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

	private String policyKey;

	private RejectedExecutionHandler rejectPolicy;

	private static final Set<String> REJECT_POLICY_KEY_SET = new HashSet<>();
	static {
		REJECT_POLICY_KEY_SET.add(POLICY_KEY_CALLER_RUNS);
		REJECT_POLICY_KEY_SET.add(POLICY_KEY_ABORT);
		REJECT_POLICY_KEY_SET.add(POLICY_KEY_DISCARD);
		REJECT_POLICY_KEY_SET.add(POLICY_KEY_DISCARD_OLDEST);
		REJECT_POLICY_KEY_SET.add(POLICY_KEY_BLOCKING);
	}

	ThreadPoolRejectPolicy(int code, String policyKey, RejectedExecutionHandler rejectPolicy) {
		this.code = code;
		this.policyKey = policyKey;
		this.rejectPolicy = rejectPolicy;
	}

	public static ThreadPoolRejectPolicy of(String policyKey) {
		if(StringUtils.isEmpty(policyKey)) {
			throw new IllegalArgumentException("the parameter policyKey MUST not be NULL or empty.");
		}
		if(!REJECT_POLICY_KEY_SET.contains(policyKey)) {
			throw new IllegalArgumentException("the parameter policyKey:["+policyKey+"] MUST be contains in REJECT_POLICY_KEY_SET.");
		}
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
			threadPoolRejectPolicy = POLICY_ABORT;
		}
		return threadPoolRejectPolicy;
	}

	public static boolean constainsThis(String policyKey) {
		if(StringUtils.isEmpty(policyKey)) {
			return false;
		}
		return REJECT_POLICY_KEY_SET.contains(policyKey);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getPolicyKey() {
		return policyKey;
	}

	public void setPolicyKey(String policyKey) {
		this.policyKey = policyKey;
	}

	public RejectedExecutionHandler getRejectPolicy() {
		return rejectPolicy;
	}

	public void setRejectPolicy(RejectedExecutionHandler rejectPolicy) {
		this.rejectPolicy = rejectPolicy;
	}
}
