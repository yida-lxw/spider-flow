package org.spiderflow.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author yida
 * @package org.spiderflow.configuration
 * @date 2024-09-01 21:45
 * @description Redis单机版配置项
 */
@Component
@Configuration
@DependsOn("springContextUtils")
@PropertySource(value = "classpath:application.yml", factory = YmlPropertySourceFactory.class)
public class RedisStandAloneProperties {
	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private int port;

	@Value("${spring.redis.password}")
	private String password;

	@Value("${spring.redis.database}")
	private int database;

	/**命令执行超时时间(单位:毫秒)*/
	@Value("${spring.redis.timeout}")
	private long commandTimeout;

	/**连接超时时间(单位:毫秒)*/
	@Value("${spring.redis.connect-timeout}")
	private long connectTimeout;

	/**是否验证连接*/
	@Value("${spring.redis.valid-connection}")
	private boolean validConnection;

	/**是否共享连接*/
	@Value("${spring.redis.share-native-connection}")
	private boolean shareNativeConnection;

	@Value("${spring.redis.lettuce.pool.max-total}")
	private int maxTotal;

	@Value("${spring.redis.lettuce.pool.max-idle}")
	private int maxIdle;

	@Value("${spring.redis.lettuce.pool.min-idle}")
	private int minIdle;

	@Value("${spring.redis.lettuce.pool.max-wait}")
	private long maxWait;

	@Value("${spring.redis.lettuce.pool.time-between-eviction-runs}")
	private long timeBetweenEvictionRuns;

	/**Redis客户端关闭超时时间(单位:毫秒)*/
	@Value("${spring.redis.lettuce.shutdown-timeout}")
	private long shutDownTimeout;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	public long getTimeBetweenEvictionRuns() {
		return timeBetweenEvictionRuns;
	}

	public void setTimeBetweenEvictionRuns(long timeBetweenEvictionRuns) {
		this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
	}

	public long getCommandTimeout() {
		return commandTimeout;
	}

	public void setCommandTimeout(long commandTimeout) {
		this.commandTimeout = commandTimeout;
	}

	public long getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public boolean isValidConnection() {
		return validConnection;
	}

	public void setValidConnection(boolean validConnection) {
		this.validConnection = validConnection;
	}

	public boolean isShareNativeConnection() {
		return shareNativeConnection;
	}

	public void setShareNativeConnection(boolean shareNativeConnection) {
		this.shareNativeConnection = shareNativeConnection;
	}

	public long getShutDownTimeout() {
		return shutDownTimeout;
	}

	public void setShutDownTimeout(long shutDownTimeout) {
		this.shutDownTimeout = shutDownTimeout;
	}
}
