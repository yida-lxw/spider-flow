package org.spiderflow.core.utils;

/**
 * @author yida
 * @package com.yida.java.bootlayuicurd.utils
 * @date 2023-08-07 16:32
 * @description 获取操作系统相关信息的工具类
 */
public class OSUtils {
	/**
	 * @return String
	 * @description 获取操作系统类型
	 * @author yida
	 * @date 2023-08-07 16:35:54
	 */
	public static String getOSType() {
		String osType = System.getProperty("os.name").toLowerCase();
		if (osType.contains("windows")) {
			return "windows";
		}
		if (osType.contains("linux")) {
			return "linux";
		}
		if (osType.contains("mac")) {
			return "mac";
		}
		return "other";
	}

	/**
	 * @return int
	 * @description 获取操作系统的位数
	 * @author yida
	 * @date 2023-08-07 16:39:16
	 */
	public static int getOSArch() {
		String arch = System.getProperty("sun.arch.data.model");
		if ("32".equals(arch)) {
			return 32;
		}
		if ("64".equals(arch)) {
			return 64;
		}
		throw new IllegalArgumentException("Unknown OS Arch:[" + arch + "].");
	}
}
