package org.spiderflow.log4j2;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.context.SpiderContextHolder;
import org.spiderflow.core.model.SpiderLog;
import org.spiderflow.model.SpiderWebSocketContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yida
 * @package org.spiderflow.log4j2
 * @date 2024-08-20 21:43
 * @description Type your description over here.
 */
public class SpiderFlowWebSocketAppender extends AbstractOutputStreamAppender<OutputStreamManager> {
	protected SpiderFlowWebSocketAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush, OutputStreamManager manager) {
		super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
	}

	protected SpiderFlowWebSocketAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush, Property[] properties, OutputStreamManager manager) {
		super(name, layout, filter, ignoreExceptions, immediateFlush, properties, manager);
	}

	@Override
	public void append(LogEvent event) {
		String level = event.getLevel().toString();
		String loggerName = event.getLoggerName();
		String message = event.getMessage().getFormattedMessage();
		long threadId = event.getThreadId();
		String threadName = event.getThreadName();
		long eventTime = event.getTimeMillis();
		Throwable throwable = event.getThrownProxy() != null ? ((ThrowableProxy) event.getThrownProxy()).getThrowable() : null;
		SpiderContext context = SpiderContextHolder.get();
		if (context instanceof SpiderWebSocketContext) {
			SpiderWebSocketContext socketContext = (SpiderWebSocketContext) context;
			ReadOnlyStringMap readOnlyStringMap = event.getContextData();
			Map<String, String> contextDataMap = (null == readOnlyStringMap)?null : readOnlyStringMap.toMap();
			if(null == contextDataMap) {
				contextDataMap = new HashMap<>();
			}
			if (throwable != null) {
				contextDataMap.put("exception", ExceptionUtils.getStackTrace(throwable));
			}
			socketContext.log(new SpiderLog(level, loggerName, threadId, threadName, eventTime, message, contextDataMap));
		}
	}
}
