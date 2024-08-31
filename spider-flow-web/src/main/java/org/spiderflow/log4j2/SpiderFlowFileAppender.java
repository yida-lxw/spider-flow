package org.spiderflow.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.context.SpiderContextHolder;
import org.spiderflow.core.job.SpiderJobContext;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author yida
 * @package org.spiderflow.log4j2
 * @date 2024-08-20 20:46
 * @description Type your description over here.
 */
@Plugin(name="SpiderFlowFileAppender", category="Core", elementType="appender", printObject=true)
public class SpiderFlowFileAppender extends AbstractAppender {
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock writeLock = rwLock.writeLock();

	protected SpiderFlowFileAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
		super(name, filter, layout);
	}

	protected SpiderFlowFileAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
	}

	protected SpiderFlowFileAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
		super(name, filter, layout, ignoreExceptions, properties);
	}


	@Override
	public void append(LogEvent event) {
		SpiderContext context = SpiderContextHolder.get();
		OutputStream outputStream = System.out;
		if (context instanceof SpiderJobContext) {
			SpiderJobContext jobContext = (SpiderJobContext) context;
			outputStream = jobContext.getOutputstream();
		}
		try {
			writeLock.lock();
			final byte[] bytes = getLayout().toByteArray(event);
			outputStream.write(bytes);
		} catch (Exception ex) {
			if (!ignoreExceptions()) {
				throw new AppenderLoggingException(ex);
			}
		} finally {
			writeLock.unlock();
		}
	}

	@PluginFactory
	public static SpiderFlowFileAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter,
			@PluginAttribute("ignoreExceptions") final boolean ignoreExceptions) {
		if (name == null) {
			LOGGER.error("No name provided for SpiderFlowFileAppender");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		return new SpiderFlowFileAppender(name, filter, layout, ignoreExceptions);
	}
}
