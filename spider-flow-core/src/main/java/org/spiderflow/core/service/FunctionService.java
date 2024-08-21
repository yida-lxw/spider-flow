package org.spiderflow.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.spiderflow.core.model.Function;

import java.io.Serializable;

/**
 * @author yida
 * @package org.spiderflow.core.service
 * @date 2024-08-21 17:01
 * @description Type your description over here.
 */
public interface FunctionService extends IService<Function> {
	String saveFunction(Function entity);

	boolean removeById(Serializable id);
}
