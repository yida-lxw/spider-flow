package org.spiderflow.core.job.id;

/**
 * @author yida
 * @package org.spiderflow.core.job.id
 * @date 2024-08-25 17:41
 * @description Type your description over here.
 */
public interface IdGenerator<T> {
	/**
	 * @description 生成唯一不重复的id
	 * @author yida
	 * @date 2024-08-25 17:42:29
	 * @return {@link T}
	 */
	T nextId();
}
