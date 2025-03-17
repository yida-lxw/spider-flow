package org.spiderflow.core.utils;


import com.github.jazzhow.command4j.CommandManager;
import com.github.jazzhow.command4j.CommandProcess;
import org.spiderflow.core.constants.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * @author yida
 * @package com.yida.java.bootlayuicurd.utils
 * @date 2023-08-07 18:50
 * @description Type your description over here.
 */
public class CommandExecuteUtils {
	public static boolean execute(String command) {
		return execute(command, null);
	}

	public static boolean execute(String command, long waitForSecond) {
		return execute(command, null, waitForSecond);
	}

	public static boolean execute(String command, String processName) {
		return execute(command, processName, 0L);
	}

	public static boolean execute(String command, String processName, long waitForSecond) {
		if("linux".equals(OSUtils.getOSType())) {
			String[] cmdArray = new String[] {"sh", "-c", command};
			String result = executeCommand(cmdArray);
			return StringUtils.isNotEmpty(result);
		}
		CommandManager commandManager = new CommandManager();
		CommandProcess commandProcess = null;
		Process process = null;
		if (StringUtils.isEmpty(processName)) {
			processName = "default-process";
		}
		try {
			commandProcess = commandManager.exec(processName, command);
			process = commandProcess.getProcess();
			if (null != process) {
				while (process.isAlive()) {
					if (waitForSecond > 0L) {
						process.waitFor(waitForSecond, TimeUnit.SECONDS);
					} else {
						process.waitFor();
					}
					break;
				}
			}
			return null != process && 0 == process.exitValue();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String executeCommand(String command) {
		try {
			Process ps = Runtime.getRuntime().exec(command);
			ps.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ps.getInputStream(), Constants.DEFAULT_CHARSET));
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line).append("\n");
			}
			String result = stringBuffer.toString();
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public static String executeCommand(String[] commandArray) {
		try {
			Process ps = Runtime.getRuntime().exec(commandArray);
			ps.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ps.getInputStream(), Constants.DEFAULT_CHARSET));
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line).append("\n");
			}
			String result = stringBuffer.toString();
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
