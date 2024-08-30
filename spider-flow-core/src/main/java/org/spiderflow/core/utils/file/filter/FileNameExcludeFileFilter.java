package org.spiderflow.core.utils.file.filter;

import org.spiderflow.core.utils.StringUtils;

import java.io.File;
import java.io.FileFilter;

/**
 * @author yida
 * @date 2024-07-16 09:06
 * @description 排除特定文件名的文件/目录
 */
public class FileNameExcludeFileFilter implements FileFilter {
	private String excludeDirectoryName;

	public FileNameExcludeFileFilter(String excludeDirectoryName) {
		this.excludeDirectoryName = excludeDirectoryName;
	}

	@Override
	public boolean accept(File file) {
		if (StringUtils.isEmpty(excludeDirectoryName)) {
			return true;
		}
		String fileName = file.getName();
		return !excludeDirectoryName.equals(fileName);
	}
}
