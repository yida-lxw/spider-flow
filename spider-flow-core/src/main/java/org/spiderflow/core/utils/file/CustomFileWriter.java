package org.spiderflow.core.utils.file;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author yida
 * @package org.example.utils
 * @date 2024-01-30 11:48
 * @description Type your description over here.
 */
public class CustomFileWriter extends OutputStreamWriter {
	public static final boolean DEFAULT_APPEND = false;
	public static final String DEFAULT_CHARSET_NAME = "UTF-8";

	public CustomFileWriter(String fileName) throws IOException {
		this(fileName, DEFAULT_CHARSET_NAME, DEFAULT_APPEND);
	}

	public CustomFileWriter(String fileName, boolean append) throws IOException {
		this(fileName, DEFAULT_CHARSET_NAME, append);
	}

	public CustomFileWriter(String fileName, String charset) throws IOException {
		this(fileName, charset, DEFAULT_APPEND);
	}

	public CustomFileWriter(String fileName, String charset, boolean append) throws IOException {
		super(new FileOutputStream(fileName, append), charset);
	}

	public CustomFileWriter(File file) throws IOException {
		this(file, DEFAULT_CHARSET_NAME, DEFAULT_APPEND);
	}

	public CustomFileWriter(File file, boolean append) throws IOException {
		this(file, DEFAULT_CHARSET_NAME, append);
	}

	public CustomFileWriter(File file, String charset) throws IOException {
		this(file, charset, DEFAULT_APPEND);
	}

	public CustomFileWriter(File file, String charset, boolean append) throws IOException {
		super(new FileOutputStream(file, append), charset);
	}

	public CustomFileWriter(FileDescriptor fileDescriptor) {
		super(new FileOutputStream(fileDescriptor));
	}
}
