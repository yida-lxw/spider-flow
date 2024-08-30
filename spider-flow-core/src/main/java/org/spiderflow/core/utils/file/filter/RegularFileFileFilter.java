package org.spiderflow.core.utils.file.filter;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;

/**
 * @author yida
 * @date 2023-09-02 16:26
 * @description Regular File文件过滤器
 */
public class RegularFileFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return Files.isRegularFile(file.toPath());
	}
}
