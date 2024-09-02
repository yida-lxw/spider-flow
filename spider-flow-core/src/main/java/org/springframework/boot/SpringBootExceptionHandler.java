package org.springframework.boot;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yida
 * @package org.springframework.boot
 * @date 2024-09-02 11:40
 * @description Type your description over here.
 */
public class SpringBootExceptionHandler implements Thread.UncaughtExceptionHandler {
	private static final Set<String> LOG_CONFIGURATION_MESSAGES;

	static {
		Set<String> messages = new HashSet<>();
		messages.add("Logback configuration error detected");
		LOG_CONFIGURATION_MESSAGES = Collections.unmodifiableSet(messages);
	}

	private static LoggedExceptionHandlerThreadLocal handler = new LoggedExceptionHandlerThreadLocal();

	private final Thread.UncaughtExceptionHandler parent;

	private final List<Throwable> loggedExceptions = new ArrayList<>();

	private int exitCode = 0;

	SpringBootExceptionHandler(Thread.UncaughtExceptionHandler parent) {
		this.parent = parent;
	}

	public void registerLoggedException(Throwable exception) {
		this.loggedExceptions.add(exception);
	}

	public void registerExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			if (isPassedToParent(ex) && this.parent != null) {
				this.parent.uncaughtException(thread, ex);
			}
		}
		finally {
			this.loggedExceptions.clear();
			if (this.exitCode != 0) {
				System.exit(this.exitCode);
			}
		}
	}

	private boolean isPassedToParent(Throwable ex) {
		return isLogConfigurationMessage(ex) || !isRegistered(ex);
	}

	/**
	 * Check if the exception is a log configuration message, i.e. the log call might not
	 * have actually output anything.
	 * @param ex the source exception
	 * @return {@code true} if the exception contains a log configuration message
	 */
	private boolean isLogConfigurationMessage(Throwable ex) {
		if (ex instanceof InvocationTargetException) {
			return isLogConfigurationMessage(ex.getCause());
		}
		String message = ex.getMessage();
		if (message != null) {
			for (String candidate : LOG_CONFIGURATION_MESSAGES) {
				if (message.contains(candidate)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isRegistered(Throwable ex) {
		if (this.loggedExceptions.contains(ex)) {
			return true;
		}
		if (ex instanceof InvocationTargetException) {
			return isRegistered(ex.getCause());
		}
		return false;
	}

	public static SpringBootExceptionHandler forCurrentThread() {
		return handler.get();
	}

	/**
	 * Thread local used to attach and track handlers.
	 */
	public static class LoggedExceptionHandlerThreadLocal extends ThreadLocal<SpringBootExceptionHandler> {

		@Override
		protected SpringBootExceptionHandler initialValue() {
			SpringBootExceptionHandler handler = new SpringBootExceptionHandler(
					Thread.currentThread().getUncaughtExceptionHandler());
			Thread.currentThread().setUncaughtExceptionHandler(handler);
			return handler;
		}
	}
}
