package org.spiderflow.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.spiderflow.core.model.Variable;

import java.io.Serializable;

/**
 * @author yida
 * @package org.spiderflow.core.service
 * @date 2024-08-21 17:07
 * @description Type your description over here.
 */
public interface VariableService extends IService<Variable> {
	boolean removeById(Serializable id);

	boolean saveOrUpdate(Variable entity);
}
