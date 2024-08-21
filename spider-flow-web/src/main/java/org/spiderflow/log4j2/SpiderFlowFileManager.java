package org.spiderflow.log4j2;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileManager;
import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.context.SpiderContextHolder;
import org.spiderflow.core.job.SpiderJobContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * @author yida
 * @package org.spiderflow.log4j2
 * @date 2024-08-20 21:15
 * @description Type your description over here.
 */
public class SpiderFlowFileManager extends FileManager {
	protected volatile OutputStream outputStream;

	protected SpiderFlowFileManager(String fileName, OutputStream os, boolean append, boolean locking, String advertiseURI, Layout<? extends Serializable> layout, int bufferSize, boolean writeHeader) {
		super(fileName, os, append, locking, advertiseURI, layout, bufferSize, writeHeader);
	}

	protected SpiderFlowFileManager(String fileName, OutputStream os, boolean append, boolean locking, String advertiseURI, Layout<? extends Serializable> layout, boolean writeHeader, ByteBuffer buffer) {
		super(fileName, os, append, locking, advertiseURI, layout, writeHeader, buffer);
	}

	protected SpiderFlowFileManager(LoggerContext loggerContext, String fileName, OutputStream os, boolean append, boolean locking, boolean createOnDemand, String advertiseURI, Layout<? extends Serializable> layout, boolean writeHeader, ByteBuffer buffer) {
		super(loggerContext, fileName, os, append, locking, createOnDemand, advertiseURI, layout, writeHeader, buffer);
	}

	protected SpiderFlowFileManager(LoggerContext loggerContext, String fileName, OutputStream os, boolean append, boolean locking, boolean createOnDemand, String advertiseURI, Layout<? extends Serializable> layout, String filePermissions, String fileOwner, String fileGroup, boolean writeHeader, ByteBuffer buffer) {
		super(loggerContext, fileName, os, append, locking, createOnDemand, advertiseURI, layout, filePermissions, fileOwner, fileGroup, writeHeader, buffer);
	}

	@Override
	protected OutputStream getOutputStream() throws IOException {
		SpiderContext context = SpiderContextHolder.get();
		if (context instanceof SpiderJobContext) {
			SpiderJobContext jobContext = (SpiderJobContext) context;
			this.outputStream = jobContext.getOutputstream();
		} else {
			this.outputStream = super.getOutputStream();
		}
		return this.outputStream;
	}
}
