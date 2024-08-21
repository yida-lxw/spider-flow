package org.spiderflow.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.model.Function;
import org.spiderflow.core.script.ScriptManager;
import org.spiderflow.core.service.FunctionService;
import org.spiderflow.mapper.FunctionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import java.io.Serializable;

@Service
public class FunctionServiceImpl extends ServiceImpl<FunctionMapper, Function> implements FunctionService {
	private static final Logger logger = LoggerFactory.getLogger(FunctionServiceImpl.class);

	/**
	 * 初始化/重置自定义函数
	 */
	@PostConstruct
	private void init() {
		try {
			ScriptManager.lock();
			ScriptManager.clearFunctions();
			ScriptEngine engine = ScriptManager.createEngine();
			super.list().forEach(function -> {
				ScriptManager.registerFunction(engine, function.getName(), function.getParameter(), function.getScript());
			});
			ScriptManager.setScriptEngine(engine);
		} finally {
			ScriptManager.unlock();
		}
	}

	@Override
	public String saveFunction(Function entity) {
		try {
			ScriptManager.validScript(entity.getName(), entity.getParameter(), entity.getScript());
			super.saveOrUpdate(entity);
			init();
			return null;
		} catch (Exception e) {
			logger.error("保存自定义函数出错", e);
			return ExceptionUtils.getStackTrace(e);
		}
	}

	@Override
	public boolean removeById(Serializable id) {
		boolean ret = super.removeById(id);
		init();
		return ret;
	}
}
