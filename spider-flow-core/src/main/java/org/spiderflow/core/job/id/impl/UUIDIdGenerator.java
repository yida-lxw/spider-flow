package org.spiderflow.core.job.id.impl;

import org.spiderflow.core.job.id.IdGenerator;

import java.util.UUID;

/**
 * @author yida
 * @package org.spiderflow.core.job.id.impl
 * @date 2024-08-25 17:43
 * @description Type your description over here.
 */
public class UUIDIdGenerator implements IdGenerator<String> {
	//是否移除连接字符
	private boolean removeHyphen;
	public UUIDIdGenerator() {}

	public UUIDIdGenerator(boolean removeHyphen) {
		this.removeHyphen = removeHyphen;
	}

	@Override
	public String nextId() {
		UUID uuid = UUID.randomUUID();
		String id = uuid.toString();
		if(removeHyphen) {
			id = id.replace("-", "");
		}
		return id;
	}
}
