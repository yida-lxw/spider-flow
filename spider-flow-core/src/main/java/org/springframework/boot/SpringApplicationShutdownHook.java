package org.springframework.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.Assert;

import java.security.AccessControlException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yida
 * @package org.springframework.boot
 * @date 2024-09-02 11:20
 * @description Type your description over here.
 */
public class SpringApplicationShutdownHook implements Runnable {
	private static final int SLEEP = 50;

	private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(10);

	private static final Log logger = LogFactory.getLog(SpringApplicationShutdownHook.class);

	private final Handlers handlers = new Handlers();

	private final Set<ConfigurableApplicationContext> contexts = new LinkedHashSet<>();

	private final Set<ConfigurableApplicationContext> closedContexts = Collections.newSetFromMap(new WeakHashMap<>());

	private final ApplicationContextClosedListener contextCloseListener = new ApplicationContextClosedListener();

	private final AtomicBoolean shutdownHookAdded = new AtomicBoolean();

	private boolean inProgress;

	public SpringApplicationShutdownHandlers getHandlers() {
		return this.handlers;
	}

	public void registerApplicationContext(ConfigurableApplicationContext context) {
		addRuntimeShutdownHookIfNecessary();
		synchronized (SpringApplicationShutdownHook.class) {
			assertNotInProgress();
			context.addApplicationListener(this.contextCloseListener);
			this.contexts.add(context);
		}
	}

	private void addRuntimeShutdownHookIfNecessary() {
		if (this.shutdownHookAdded.compareAndSet(false, true)) {
			addRuntimeShutdownHook();
		}
	}

	void addRuntimeShutdownHook() {
		try {
			Runtime.getRuntime().addShutdownHook(new Thread(this, "SpringApplicationShutdownHook"));
		}
		catch (AccessControlException ex) {
			// Not allowed in some environments
		}
	}

	public void deregisterFailedApplicationContext(ConfigurableApplicationContext applicationContext) {
		synchronized (SpringApplicationShutdownHook.class) {
			Assert.state(!applicationContext.isActive(), "Cannot unregister active application context");
			SpringApplicationShutdownHook.this.contexts.remove(applicationContext);
		}
	}

	@Override
	public void run() {
		Set<ConfigurableApplicationContext> contexts;
		Set<ConfigurableApplicationContext> closedContexts;
		Set<Runnable> actions;
		synchronized (SpringApplicationShutdownHook.class) {
			this.inProgress = true;
			contexts = new LinkedHashSet<>(this.contexts);
			closedContexts = new LinkedHashSet<>(this.closedContexts);
			actions = new LinkedHashSet<>(this.handlers.getActions());
		}
		contexts.forEach(this::closeAndWait);
		closedContexts.forEach(this::closeAndWait);
		actions.forEach(Runnable::run);
	}

	boolean isApplicationContextRegistered(ConfigurableApplicationContext context) {
		synchronized (SpringApplicationShutdownHook.class) {
			return this.contexts.contains(context);
		}
	}

	void reset() {
		synchronized (SpringApplicationShutdownHook.class) {
			this.contexts.clear();
			this.closedContexts.clear();
			this.handlers.getActions().clear();
			this.inProgress = false;
		}
	}

	/**
	 * Call {@link ConfigurableApplicationContext#close()} and wait until the context
	 * becomes inactive. We can't assume that just because the close method returns that
	 * the context is actually inactive. It could be that another thread is still in the
	 * process of disposing beans.
	 * @param context the context to clean
	 */
	private void closeAndWait(ConfigurableApplicationContext context) {
		if (!context.isActive()) {
			return;
		}
		context.close();
		try {
			int waited = 0;
			while (context.isActive()) {
				if (waited > TIMEOUT) {
					throw new TimeoutException();
				}
				Thread.sleep(SLEEP);
				waited += SLEEP;
			}
		}
		catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			logger.warn("Interrupted waiting for application context " + context + " to become inactive");
		}
		catch (TimeoutException ex) {
			logger.warn("Timed out waiting for application context " + context + " to become inactive", ex);
		}
	}

	private void assertNotInProgress() {
		Assert.state(!SpringApplicationShutdownHook.this.inProgress, "Shutdown in progress");
	}

	/**
	 * The handler actions for this shutdown hook.
	 */
	private class Handlers implements SpringApplicationShutdownHandlers {

		private final Set<Runnable> actions = Collections.newSetFromMap(new IdentityHashMap<>());

		@Override
		public void add(Runnable action) {
			Assert.notNull(action, "Action must not be null");
			synchronized (SpringApplicationShutdownHook.class) {
				assertNotInProgress();
				this.actions.add(action);
			}
		}

		@Override
		public void remove(Runnable action) {
			Assert.notNull(action, "Action must not be null");
			synchronized (SpringApplicationShutdownHook.class) {
				assertNotInProgress();
				this.actions.remove(action);
			}
		}

		Set<Runnable> getActions() {
			return this.actions;
		}

	}

	/**
	 * {@link ApplicationListener} to track closed contexts.
	 */
	private class ApplicationContextClosedListener implements ApplicationListener<ContextClosedEvent> {

		@Override
		public void onApplicationEvent(ContextClosedEvent event) {
			// The ContextClosedEvent is fired at the start of a call to {@code close()}
			// and if that happens in a different thread then the context may still be
			// active. Rather than just removing the context, we add it to a {@code
			// closedContexts} set. This is weak set so that the context can be GC'd once
			// the {@code close()} method returns.
			synchronized (SpringApplicationShutdownHook.class) {
				ApplicationContext applicationContext = event.getApplicationContext();
				SpringApplicationShutdownHook.this.contexts.remove(applicationContext);
				SpringApplicationShutdownHook.this.closedContexts
						.add((ConfigurableApplicationContext) applicationContext);
			}
		}
	}
}
