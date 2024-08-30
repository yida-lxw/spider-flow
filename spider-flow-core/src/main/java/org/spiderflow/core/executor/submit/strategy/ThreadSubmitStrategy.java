package org.spiderflow.core.executor.submit.strategy;

import org.spiderflow.core.model.SpiderNode;
import org.spiderflow.core.executor.thread.SpiderFutureTask;

import java.util.Comparator;

public interface ThreadSubmitStrategy {
	Comparator<SpiderNode> comparator();

	void add(SpiderFutureTask<?> task);

	boolean isEmpty();

	SpiderFutureTask<?> get();
}
