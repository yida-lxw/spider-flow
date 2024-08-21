package org.spiderflow.core.utils;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库连接工具类
 *
 * @author jmxd
 */
@Component
public class DataSourceUtils {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceUtils.class);

	private static final Map<String, DataSource> datasources = new HashMap<>();

	private static DataSourceService dataSourceService;

	public static DataSource createDataSource(String driverClassName, String jdbcUrl, String username, String password,
											  Map<String, Object> connectionPoolConfigMap) {
		HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setDriverClassName(driverClassName);
		hikariDataSource.setJdbcUrl(jdbcUrl);
		hikariDataSource.setUsername(username);
		hikariDataSource.setPassword(password);
		hikariDataSource.setAutoCommit(true);
		if (null != connectionPoolConfigMap && connectionPoolConfigMap.size() > 0) {
			Object poolNameObj = connectionPoolConfigMap.get("pool-name");
			if (null != poolNameObj && !"".equals(poolNameObj.toString())) {
				hikariDataSource.setPoolName(poolNameObj.toString());
			}
			Object maxPoolSizeObj = connectionPoolConfigMap.get("maximum-pool-size");
			if (null != maxPoolSizeObj) {
				hikariDataSource.setMaximumPoolSize(Integer.valueOf(maxPoolSizeObj.toString()));
			}
			Object minIdleObj = connectionPoolConfigMap.get("minimum-idle");
			if (null != minIdleObj) {
				hikariDataSource.setMinimumIdle(Integer.valueOf(minIdleObj.toString()));
			}
			Object connectionTimeoutObj = connectionPoolConfigMap.get("connection-timeout");
			if (null != connectionTimeoutObj) {
				hikariDataSource.setConnectionTimeout(Long.valueOf(connectionTimeoutObj.toString()));
			}
			Object idleTimeoutObj = connectionPoolConfigMap.get("idle-timeout");
			if (null != idleTimeoutObj) {
				hikariDataSource.setIdleTimeout(Long.valueOf(idleTimeoutObj.toString()));
			}
			Object maxLifeTimeObj = connectionPoolConfigMap.get("max-lifetime");
			if (null != maxLifeTimeObj) {
				hikariDataSource.setMaxLifetime(Long.valueOf(maxLifeTimeObj.toString()));
			}
			Object connectionTestQueryObj = connectionPoolConfigMap.get("connection-test-query");
			if (null != connectionTestQueryObj && !"".equals(connectionTestQueryObj.toString())) {
				hikariDataSource.setConnectionTestQuery(connectionTestQueryObj.toString());
			}
			Object keepAliveTimeObj = connectionPoolConfigMap.get("keepalive-time");
			if (null != keepAliveTimeObj) {
				hikariDataSource.setKeepaliveTime(Long.valueOf(keepAliveTimeObj.toString()));
			}
			Object autoCommitObj = connectionPoolConfigMap.get("auto-commit");
			if (null != autoCommitObj && !"".equals(autoCommitObj.toString())) {
				hikariDataSource.setAutoCommit(Boolean.valueOf(autoCommitObj.toString()));
			}
		}
		return hikariDataSource;
	}

	public static void remove(String dataSourceId) {
		DataSource dataSource = datasources.get(dataSourceId);
		if (null != dataSource) {
			HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
			try {
				hikariDataSource.close();
			} catch (Exception e) {
				logger.error("Closing the HikariDataSource occur exception:\n{}.", e.getMessage());
			}
			datasources.remove(dataSourceId);
		}
	}

	public synchronized static DataSource getDataSource(String dataSourceId) {
		DataSource dataSource = datasources.get(dataSourceId);
		if (dataSource == null) {
			org.spiderflow.core.model.DataSource ds = dataSourceService.getById(dataSourceId);
			if (ds != null) {
				String connectionPoolConfigJSON = ds.getConnectionPoolConfig();
				Map<String, Object> connectionPoolConfigMap = null;
				if (null != connectionPoolConfigJSON && !"".equals(connectionPoolConfigJSON)) {
					connectionPoolConfigMap = JacksonUtils.json2Map(connectionPoolConfigJSON);
				}
				dataSource = createDataSource(ds.getDriverClassName(), ds.getJdbcUrl(), ds.getUsername(), ds.getPassword(), connectionPoolConfigMap);
				datasources.put(dataSourceId, dataSource);
			}
		}
		return dataSource;
	}

	@Autowired
	public void setDataSourceService(DataSourceService dataSourceService) {
		DataSourceUtils.dataSourceService = dataSourceService;
	}
}
