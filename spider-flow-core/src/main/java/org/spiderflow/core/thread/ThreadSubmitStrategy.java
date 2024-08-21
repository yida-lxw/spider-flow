package org.spiderflow.core.thread;

import org.spiderflow.core.model.SpiderNode;

import java.util.Comparator;

public interface ThreadSubmitStrategy {
	Comparator<SpiderNode> comparator();

	void add(SpiderFutureTask<?> task);

	boolean isEmpty();

	SpiderFutureTask<?> get();
}
