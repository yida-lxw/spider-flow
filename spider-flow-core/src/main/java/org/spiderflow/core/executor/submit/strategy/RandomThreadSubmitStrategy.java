package org.spiderflow.core.executor.submit.strategy;

import org.apache.commons.lang3.RandomUtils;
import org.spiderflow.core.model.SpiderNode;
import org.spiderflow.core.executor.thread.SpiderFutureTask;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RandomThreadSubmitStrategy implements ThreadSubmitStrategy {

	private List<SpiderFutureTask<?>> taskList = new CopyOnWriteArrayList<>();

	@Override
	public Comparator<SpiderNode> comparator() {
		return (o1, o2) -> RandomUtils.nextInt(0, 3) - 1;
	}

	@Override
	public void add(SpiderFutureTask<?> task) {
		taskList.add(task);
	}

	@Override
	public boolean isEmpty() {
		return taskList.isEmpty();
	}

	@Override
	public SpiderFutureTask<?> get() {
		return taskList.remove(RandomUtils.nextInt(0, taskList.size()));
	}
}
