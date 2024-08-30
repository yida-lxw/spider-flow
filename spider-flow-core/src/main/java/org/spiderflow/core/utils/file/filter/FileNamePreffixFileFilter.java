package org.spiderflow.core.utils.file.filter;

import org.spiderflow.core.utils.StringUtils;

import java.io.File;
import java.io.FileFilter;

/**
 * @author yida
 * @package com.witarchive.common.file.filter
 * @date 2024-07-15 11:30
 * @description Type your description over here.
 */
public class FileNamePreffixFileFilter implements FileFilter {
	private String fileNamePreffix;

	public FileNamePreffixFileFilter(String fileNamePreffix) {
		this.fileNamePreffix = fileNamePreffix;
	}

	@Override
	public boolean accept(File file) {
		if (StringUtils.isEmpty(fileNamePreffix)) {
			return true;
		}
		String fileName = file.getName();
		return fileName.startsWith(fileNamePreffix);
	}

	public String getFileNamePreffix() {
		return fileNamePreffix;
	}

	public void setFileNamePreffix(String fileNamePreffix) {
		this.fileNamePreffix = fileNamePreffix;
	}
}
