package org.spiderflow.core.utils;

import java.io.Serializable;

/**
 * @author yida
 * @package org.spiderflow.core.utils
 * @date 2024-08-30 16:23
 * @description Type your description over here.
 */
public interface Builder<T> extends Serializable {
	T build();
}
