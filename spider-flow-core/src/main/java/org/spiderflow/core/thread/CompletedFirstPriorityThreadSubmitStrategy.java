package org.spiderflow.core.thread;

import org.spiderflow.core.model.SpiderNode;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yida
 * @package org.spiderflow.core.thread
 * @date 2024-08-24 18:05
 * @description 先完成的任务优先处理
 */
public class CompletedFirstPriorityThreadSubmitStrategy implements ThreadSubmitStrategy {
	private List<SpiderFutureTask<?>> taskList = new CopyOnWriteArrayList<>();

	@Override
	public Comparator<SpiderNode> comparator() {
		return (o1, o2) -> o1.getExecutionCompletedTimeMills().compareTo(o2.getExecutionCompletedTimeMills());
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
		int taskSize = taskList.size();
		Long minExecutionCompletedTimeMills = 0L;
		int targetIndex = 0;
		for (int i = 0; i < taskSize; i++) {
			SpiderFutureTask<?> futureTask = taskList.get(i);
			SpiderNode spiderNode = futureTask.getNode();
			Long executionCompletedTimeMills = spiderNode.getExecutionCompletedTimeMills();
			if (executionCompletedTimeMills < minExecutionCompletedTimeMills) {
				minExecutionCompletedTimeMills = executionCompletedTimeMills;
				targetIndex = i;
			}
		}
		return taskList.remove(targetIndex);
	}
}
