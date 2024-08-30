package org.spiderflow.core.utils.file.filter;

import org.spiderflow.core.utils.StringUtils;

import java.io.File;
import java.io.FileFilter;

/**
 * @author yida
 * @date 2024-07-15 11:30
 * @description Type your description over here.
 */
public class FileNameSuffixFileFilter implements FileFilter {
	private String fileNameSuffix;

	public FileNameSuffixFileFilter(String fileNameSuffix) {
		this.fileNameSuffix = fileNameSuffix;
	}

	@Override
	public boolean accept(File file) {
		String fileName = file.getName();
		String curFileNameSuffix = "";
		if (fileName.indexOf(".") != -1) {
			curFileNameSuffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		}
		if (StringUtils.isEmpty(curFileNameSuffix) && StringUtils.isEmpty(fileNameSuffix)) {
			return true;
		}
		if (StringUtils.isEmpty(curFileNameSuffix) && StringUtils.isNotEmpty(fileNameSuffix)) {
			return false;
		}
		if (StringUtils.isNotEmpty(curFileNameSuffix) && StringUtils.isEmpty(fileNameSuffix)) {
			return false;
		}
		String actualFileNameSuffix = fileNameSuffix;
		if (fileNameSuffix.indexOf(".") != -1) {
			actualFileNameSuffix = fileNameSuffix.substring(fileNameSuffix.lastIndexOf(".") + 1, fileNameSuffix.length());
		}
		return curFileNameSuffix.equalsIgnoreCase(actualFileNameSuffix);
	}

	public String getFileNameSuffix() {
		return fileNameSuffix;
	}

	public void setFileNameSuffix(String fileNameSuffix) {
		this.fileNameSuffix = fileNameSuffix;
	}
}
