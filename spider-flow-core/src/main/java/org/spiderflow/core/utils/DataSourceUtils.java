package org.spiderflow.core.utils;

import com.alibaba.druid.pool.DruidDataSource;
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

	private static final Map<String, DataSource> datasources = new HashMap<>();

	private static DataSourceService dataSourceService;

	public static DataSource createDataSource(String className, String url, String username, String password) {
		DruidDataSource datasource = new DruidDataSource();
		datasource.setDriverClassName(className);
		datasource.setUrl(url);
		datasource.setUsername(username);
		datasource.setPassword(password);
		datasource.setDefaultAutoCommit(true);
		datasource.setMinIdle(1);
		datasource.setInitialSize(2);
		return datasource;
	}

	public static void remove(String dataSourceId) {
		DataSource dataSource = datasources.get(dataSourceId);
		if (dataSource != null) {
			DruidDataSource ds = (DruidDataSource) dataSource;
			ds.close();
			datasources.remove(dataSourceId);
		}
	}

	public synchronized static DataSource getDataSource(String dataSourceId) {
		DataSource dataSource = datasources.get(dataSourceId);
		if (dataSource == null) {
			org.spiderflow.core.model.DataSource ds = dataSourceService.getById(dataSourceId);
			if (ds != null) {
				dataSource = createDataSource(ds.getDriverClassName(), ds.getJdbcUrl(), ds.getUsername(), ds.getPassword());
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
