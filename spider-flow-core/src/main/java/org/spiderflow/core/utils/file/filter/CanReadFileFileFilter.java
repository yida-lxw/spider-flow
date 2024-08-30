package org.spiderflow.core.utils.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * @author yida
 * @date 2023-09-02 16:25
 * @description 拥有可读权限的文件过滤器
 */
public class CanReadFileFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isFile() && file.canRead();
	}
}
