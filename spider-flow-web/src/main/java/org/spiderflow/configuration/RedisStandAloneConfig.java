package org.spiderflow.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.spiderflow.core.utils.StringUtils;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.DefaultLettucePool;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePool;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yida
 * @package org.spiderflow.configuration
 * @date 2024-09-01 21:38
 * @description Redis单机版配置
 */
@EnableCaching
@Configuration
public class RedisStandAloneConfig extends CachingConfigurerSupport {
	@Resource
	private RedisStandAloneProperties redisStandAloneProperties;

	@Bean
	public RedisStandaloneConfiguration redisStandaloneConfiguration() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisStandAloneProperties.getHost());
		redisStandaloneConfiguration.setPort(redisStandAloneProperties.getPort());
		redisStandaloneConfiguration.setDatabase(redisStandAloneProperties.getDatabase());
		String password = redisStandAloneProperties.getPassword();
		if(StringUtils.isNotEmpty(password)) {
			redisStandaloneConfiguration.setPassword(password);
		}
		return redisStandaloneConfiguration;
	}

	@Bean
	public LettuceClientConfiguration clientConfig() {
		LettuceConnectionFactory.MutableLettuceClientConfiguration mutableLettuceClientConfiguration = new LettuceConnectionFactory.MutableLettuceClientConfiguration();
		mutableLettuceClientConfiguration.setTimeout(redisStandAloneProperties.getCommandTimeout());
		mutableLettuceClientConfiguration.setShutdownTimeout(redisStandAloneProperties.getShutDownTimeout());
		return mutableLettuceClientConfiguration;
	}

	@Bean
	public LettucePool lettucePool(GenericObjectPoolConfig poolConfig) {
		DefaultLettucePool lettucePool = new DefaultLettucePool(redisStandAloneProperties.getHost(),
				redisStandAloneProperties.getPort(), poolConfig);
		return lettucePool;
	}

	@Bean
	public GenericObjectPoolConfig<Object> poolConfig() {
		GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
		poolConfig.setMaxTotal(redisStandAloneProperties.getMaxTotal());
		poolConfig.setMaxIdle(redisStandAloneProperties.getMaxIdle());
		poolConfig.setMinIdle(redisStandAloneProperties.getMinIdle());
		poolConfig.setMaxWaitMillis(redisStandAloneProperties.getMaxWait());
		poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(redisStandAloneProperties.getTimeBetweenEvictionRuns()));
		return poolConfig;
	}

	@Bean
	public LettuceConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,
														   LettuceClientConfiguration clientConfig, LettucePool lettucePool) {
		LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig, lettucePool);
		connectionFactory.setValidateConnection(redisStandAloneProperties.isValidConnection());
		connectionFactory.setTimeout(redisStandAloneProperties.getConnectTimeout());
		connectionFactory.setShareNativeConnection(redisStandAloneProperties.isShareNativeConnection());
		return connectionFactory;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
				ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
		// 自定义ObjectMapper的时间处理模块
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		objectMapper.registerModule(javaTimeModule);
		// 禁用将日期序列化为时间戳的行为
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		//key采用String的序列化方式
		template.setKeySerializer(stringRedisSerializer);
		// hash的key也采用String的序列化方式
		template.setHashKeySerializer(stringRedisSerializer);
		// value序列化方式采用jackson
		template.setValueSerializer(jackson2JsonRedisSerializer);
		// hash的value序列化方式采用jackson
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}
}
