package org.spiderflow.core.job.id;

import org.apache.commons.lang3.StringUtils;
import org.spiderflow.core.job.id.impl.SnowflakeDistributeIdGenerator;
import org.spiderflow.core.job.id.impl.UUIDIdGenerator;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yida
 * @package org.spiderflow.core.job.id
 * @date 2024-08-25 18:26
 * @description Type your description over here.
 */
public enum IdGeneratorStrategy {
	UUID_STRATEGY(0, IdGeneratorStrategy.UUID_STRATEGY_NAME, UUIDIdGenerator.class),
	SNOW_FLAKE_STRATEGY(1, IdGeneratorStrategy.SNOW_FLAKE_STRATEGY_NAME, SnowflakeDistributeIdGenerator.class);

	private int code;
	private String strategyName;
	private Class<?> idGeneratorClass;

	public static final IdGeneratorStrategy DEFAULT_ID_GENERATOR_STRATEGY = IdGeneratorStrategy.UUID_STRATEGY;

	IdGeneratorStrategy(int code, String strategyName, Class<?> idGeneratorClass) {
		this.code = code;
		this.strategyName = strategyName;
		this.idGeneratorClass = idGeneratorClass;
	}

	public static final String UUID_STRATEGY_NAME = "uuid";
	public static final String SNOW_FLAKE_STRATEGY_NAME = "snow-flake";

	public static final Class<?> UUID_ID_GENERATOR_CLASS = UUIDIdGenerator.class;
	public static final Class<?> SNOW_FLAKE_GENERATOR_CLASS = SnowflakeDistributeIdGenerator.class;

	private static final Set<String> STRATEGY_NAME_SET = new HashSet<>();
	private static final Set<Class> ID_GENERATOR_CLASS_SET = new HashSet<>();

	static {
		STRATEGY_NAME_SET.add(UUID_STRATEGY_NAME);
		STRATEGY_NAME_SET.add(SNOW_FLAKE_STRATEGY_NAME);

		ID_GENERATOR_CLASS_SET.add(UUID_ID_GENERATOR_CLASS);
		ID_GENERATOR_CLASS_SET.add(SNOW_FLAKE_GENERATOR_CLASS);
	}

	public static IdGeneratorStrategy of(String strategyName) {
		if (StringUtils.isEmpty(strategyName)) {
			throw new IllegalArgumentException("The parameter strategyName MUST not be NULL or empty.");
		}
		if (!STRATEGY_NAME_SET.contains(strategyName)) {
			throw new IllegalArgumentException("The parameter strategyName MUST be in the STRATEGY_NAME_SET.");
		}
		if (IdGeneratorStrategy.UUID_STRATEGY_NAME.equals(strategyName)) {
			return UUID_STRATEGY;
		}
		if (IdGeneratorStrategy.SNOW_FLAKE_STRATEGY_NAME.equals(strategyName)) {
			return SNOW_FLAKE_STRATEGY;
		}
		return UUID_STRATEGY;
	}

	public static IdGeneratorStrategy of(Class<?> idGeneratorClass) {
		if (null == idGeneratorClass) {
			throw new IllegalArgumentException("The parameter idGeneratorClass MUST not be NULL.");
		}
		if (!ID_GENERATOR_CLASS_SET.contains(idGeneratorClass)) {
			throw new IllegalArgumentException("The parameter idGeneratorClass MUST be in the ID_GENERATOR_CLASS_SET.");
		}
		if (IdGeneratorStrategy.UUID_ID_GENERATOR_CLASS.equals(idGeneratorClass)) {
			return UUID_STRATEGY;
		}
		if (IdGeneratorStrategy.SNOW_FLAKE_GENERATOR_CLASS.equals(idGeneratorClass)) {
			return SNOW_FLAKE_STRATEGY;
		}
		return UUID_STRATEGY;
	}

	public static boolean containsThis(String strategyName) {
		return STRATEGY_NAME_SET.contains(strategyName);
	}

	public static boolean containsThis(Class<?> idGeneratorClass) {
		return ID_GENERATOR_CLASS_SET.contains(idGeneratorClass);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public Class<?> getIdGeneratorClass() {
		return idGeneratorClass;
	}

	public void setIdGeneratorClass(Class<?> idGeneratorClass) {
		this.idGeneratorClass = idGeneratorClass;
	}
}
