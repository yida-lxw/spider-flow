package org.spiderflow.core.utils.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * @author yida
 * @package com.witarchive.common.file.filter
 * @date 2024-07-15 11:32
 * @description 过滤空文件.
 */
public class NotEmptyFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		long fileSize = file.length();
		return fileSize > 0L;
	}
}
