package org.spiderflow.core.thread;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yida
 * @date 2023-08-24 16:04
 * @description BlockingQueue枚举
 */
public enum BlockingQueueType {
	DELAY_QUEUE(0, BlockingQueueType.DELAY_QUEUE_KEY),
	ARRAY_BLOCKING_QUEUE(1, BlockingQueueType.ARRAY_BLOCKING_QUEUE_KEY),
	LINKED_BLOCKING_QUEUE(2, BlockingQueueType.LINKED_BLOCKING_QUEUE_KEY),
	LINKED_BLOCKING_DEQUE(3, BlockingQueueType.LINKED_BLOCKING_DEQUE_KEY),
	LINKED_TRANSFER_QUEUE(4, BlockingQueueType.LINKED_TRANSFER_QUEUE_KEY),
	PRIORITY_BLOCKING_QUEUE(5, BlockingQueueType.PRIORITY_BLOCKING_QUEUE_KEY),
	SYNCHRONOUS_QUEUE(6, BlockingQueueType.SYNCHRONOUS_QUEUE_KEY),
	CONCURRENT_LINKED_BLOCKING_QUEUE(7, BlockingQueueType.CONCURRENT_LINKED_BLOCKING_QUEUE_KEY),
	BOUNDED_BLOCKING_TRANSFER_QUEUE(8, BlockingQueueType.BOUNDED_BLOCKING_TRANSFER_QUEUE_KEY),
	SUPPORT_RETRY_TASK_ARRAY_BLOCKING_QUEUE(9, BlockingQueueType.SUPPORT_RETRY_TASK_ARRAY_BLOCKING_QUEUE_KEY);

	private int code;
	private String val;

	BlockingQueueType(int code, String val) {
		this.code = code;
		this.val = val;
	}

	public static final String DELAY_QUEUE_KEY = "DelayQueue";
	public static final String ARRAY_BLOCKING_QUEUE_KEY = "ArrayBlockingQueue";
	public static final String LINKED_BLOCKING_QUEUE_KEY = "LinkedBlockingQueue";
	public static final String LINKED_BLOCKING_DEQUE_KEY = "LinkedBlockingDeque";
	public static final String LINKED_TRANSFER_QUEUE_KEY = "LinkedTransferQueue";
	public static final String PRIORITY_BLOCKING_QUEUE_KEY = "PriorityBlockingQueue";
	public static final String SYNCHRONOUS_QUEUE_KEY = "SynchronousQueue";
	public static final String CONCURRENT_LINKED_BLOCKING_QUEUE_KEY = "ConcurrentLinkedBlockingQueue";
	public static final String BOUNDED_BLOCKING_TRANSFER_QUEUE_KEY = "BoundedBlockingTransferQueue";
	public static final String SUPPORT_RETRY_TASK_ARRAY_BLOCKING_QUEUE_KEY = "SupportRetryTaskArrayBlockingQueue";

	public static final Set<String> blockingQueueTypeValueSet = new HashSet<>();

	/**
	 * 默认的任务队列类型为ArrayBlockingQueue
	 */
	public static final BlockingQueueType DEFAULT_BLOCKING_QUEUE_TYPE = ARRAY_BLOCKING_QUEUE;

	static {
		blockingQueueTypeValueSet.add(DELAY_QUEUE_KEY);
		blockingQueueTypeValueSet.add(ARRAY_BLOCKING_QUEUE_KEY);
		blockingQueueTypeValueSet.add(LINKED_BLOCKING_QUEUE_KEY);
		blockingQueueTypeValueSet.add(LINKED_BLOCKING_DEQUE_KEY);
		blockingQueueTypeValueSet.add(LINKED_TRANSFER_QUEUE_KEY);
		blockingQueueTypeValueSet.add(PRIORITY_BLOCKING_QUEUE_KEY);
		blockingQueueTypeValueSet.add(SYNCHRONOUS_QUEUE_KEY);
		blockingQueueTypeValueSet.add(CONCURRENT_LINKED_BLOCKING_QUEUE_KEY);
		blockingQueueTypeValueSet.add(BOUNDED_BLOCKING_TRANSFER_QUEUE_KEY);
		blockingQueueTypeValueSet.add(SUPPORT_RETRY_TASK_ARRAY_BLOCKING_QUEUE_KEY);
	}

	public static BlockingQueueType of(String blockingQueueTypeValue) {
		if (null == blockingQueueTypeValue || "".equals(blockingQueueTypeValue)) {
			throw new IllegalArgumentException("blockingQueueTypeValue MUST NOT be null or empty.");
		}
		if (!blockingQueueTypeValueSet.contains(blockingQueueTypeValue)) {
			throw new UnsupportedOperationException("blockingQueueTypeValue MUST BE in the blockingQueueTypeValueSet.");
		}
		if (DELAY_QUEUE_KEY.equals(blockingQueueTypeValue)) {
			return DELAY_QUEUE;
		}
		if (ARRAY_BLOCKING_QUEUE_KEY.equals(blockingQueueTypeValue)) {
			return ARRAY_BLOCKING_QUEUE;
		}
		if (LINKED_BLOCKING_QUEUE_KEY.equals(blockingQueueTypeValue)) {
			return LINKED_BLOCKING_QUEUE;
		}
		if (LINKED_BLOCKING_DEQUE_KEY.equals(blockingQueueTypeValue)) {
			return LINKED_BLOCKING_DEQUE;
		}
		if (LINKED_TRANSFER_QUEUE_KEY.equals(blockingQueueTypeValue)) {
			return LINKED_TRANSFER_QUEUE;
		}
		if (PRIORITY_BLOCKING_QUEUE_KEY.equals(blockingQueueTypeValue)) {
			return PRIORITY_BLOCKING_QUEUE;
		}
		if (SYNCHRONOUS_QUEUE_KEY.equals(blockingQueueTypeValue)) {
			return SYNCHRONOUS_QUEUE;
		}
		if (CONCURRENT_LINKED_BLOCKING_QUEUE_KEY.equals(blockingQueueTypeValue)) {
			return CONCURRENT_LINKED_BLOCKING_QUEUE;
		}
		if (BOUNDED_BLOCKING_TRANSFER_QUEUE_KEY.equals(blockingQueueTypeValue)) {
			return BOUNDED_BLOCKING_TRANSFER_QUEUE;
		}
		return SUPPORT_RETRY_TASK_ARRAY_BLOCKING_QUEUE;
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
