package org.spiderflow.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.FileManager;
import org.apache.logging.log4j.core.config.Property;
import org.spiderflow.context.SpiderContext;
import org.spiderflow.context.SpiderContextHolder;
import org.spiderflow.core.job.SpiderJobContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * @author yida
 * @package org.spiderflow.log4j2
 * @date 2024-08-20 20:46
 * @description Type your description over here.
 */
public class SpiderFlowFileAppender extends AbstractOutputStreamAppender<SpiderFlowFileManager> {

	protected SpiderFlowFileAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush, SpiderFlowFileManager manager) {
		super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
	}

	protected SpiderFlowFileAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush, Property[] properties, SpiderFlowFileManager manager) {
		super(name, layout, filter, ignoreExceptions, immediateFlush, properties, manager);
	}

	@Override
	public void append(LogEvent event) {
		super.append(event);
	}
}
