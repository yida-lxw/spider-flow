package org.spiderflow.core.job.id;

import org.spiderflow.core.job.id.impl.SnowflakeDistributeIdGenerator;
import org.spiderflow.core.job.id.impl.UUIDIdGenerator;

/**
 * @author yida
 * @package org.spiderflow.core.job.id
 * @date 2024-08-25 18:43
 * @description Id生成器工厂类.
 */
public class IdGeneratorFactory {
	public static IdGenerator build(IdGeneratorStrategy idGeneratorStrategy, long workerId, long dataCenterId) {
		if(null == idGeneratorStrategy) {
			idGeneratorStrategy = IdGeneratorStrategy.DEFAULT_ID_GENERATOR_STRATEGY;
		}
		IdGenerator idGenerator = null;
		switch (idGeneratorStrategy) {
			case UUID_STRATEGY:
				idGenerator = new UUIDIdGenerator();
				break;
			case SNOW_FLAKE_STRATEGY:
				idGenerator = new SnowflakeDistributeIdGenerator(workerId, dataCenterId);
				break;
			default:
				idGenerator = new UUIDIdGenerator();
				break;
		}
		return idGenerator;
	}
}
