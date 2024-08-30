package org.spiderflow.core.executor.submit.strategy;

import org.spiderflow.core.model.SpiderNode;
import org.spiderflow.core.executor.thread.SpiderFutureTask;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ChildPriorThreadSubmitStrategy implements ThreadSubmitStrategy {

	private Object mutex = this;

	private Comparator<SpiderNode> comparator = (o1, o2) -> {
		if (o1.equals(o2) || o1.getNodeId().equals(o2.getNodeId())) {
			return 0;
		}
		if (o1.hasLeftNode(o2.getNodeId())) {
			return -1;
		}
		return 1;
	};

	private PriorityQueue<SpiderFutureTask<?>> priorityQueue = new PriorityQueue<>((o1, o2) -> comparator.compare(o1.getNode(), o2.getNode()));

	@Override
	public Comparator<SpiderNode> comparator() {
		return comparator;
	}

	@Override
	public void add(SpiderFutureTask<?> task) {
		synchronized (mutex) {
			priorityQueue.add(task);
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized (mutex) {
			return priorityQueue.isEmpty();
		}
	}

	@Override
	public SpiderFutureTask<?> get() {
		synchronized (mutex) {
			return priorityQueue.poll();
		}
	}
}
