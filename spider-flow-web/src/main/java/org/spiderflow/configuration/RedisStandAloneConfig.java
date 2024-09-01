package org.spiderflow.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.spiderflow.core.utils.StringUtils;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author yida
 * @package org.spiderflow.configuration
 * @date 2024-09-01 21:38
 * @description Redis单机版配置
 */
@EnableCaching
@Configuration
public class RedisStandAloneConfig extends CachingConfigurerSupport {
	@Bean
	public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisProperties.getHost());
		redisStandaloneConfiguration.setPort(redisProperties.getPort());
		redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
		String password = redisProperties.getPassword();
		if(StringUtils.isNotEmpty(password)) {
			redisStandaloneConfiguration.setPassword(password);
		}
		// 配置Redis连接池
		GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
		poolConfig.setMaxTotal(10);
		poolConfig.setMaxIdle(5);
		poolConfig.setMinIdle(1);
		poolConfig.setMaxWaitMillis(2000);

		LettucePoolingClientConfiguration lettucePoolingClientConfiguration =
				LettucePoolingClientConfiguration.builder()
						.poolConfig(poolConfig)
						.build();
		return new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolingClientConfiguration);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}
}
