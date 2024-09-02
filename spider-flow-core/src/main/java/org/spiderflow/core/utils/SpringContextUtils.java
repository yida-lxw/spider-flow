package org.spiderflow.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yida
 * @package org.spiderflow.core.utils
 * @date 2024-09-02 09:32
 * @description Spring上下文工具类
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
	private static final Logger logger = LoggerFactory.getLogger(SpringContextUtils.class);

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (SpringContextUtils.applicationContext == null) {
			SpringContextUtils.applicationContext = applicationContext;
		}
	}

	public static void setAppContext(ApplicationContext applicationContext) {
		SpringContextUtils.applicationContext = applicationContext;
	}

	/**
	 * 获取applicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 通过beanName获取 Bean.
	 */
	public static <T>T getBean(String beanName) {
		T beanObject = null;
		try {
			beanObject = (T)applicationContext.getBean(beanName);
		} catch (NoSuchBeanDefinitionException e) {
			logger.error("Get bean with name:[{}] from Spring IOC occur exception:\n{}.", beanName, e.getMessage());
		}
		return beanObject;
	}

	/**
	 * 通过class获取Bean.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 通过name,以及Class类型返回指定的Bean
	 */
	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

	/**
	 * 通过Class类型获取 Bean.
	 */
	public static <T> Map<String, T> getBeans(Class<T> requiredType) {
		return applicationContext.getBeansOfType(requiredType);
	}

	public static boolean containsBean(String name) {
		return applicationContext.containsBean(name);
	}

	public static boolean isSingleton(String name) {
		return applicationContext.isSingleton(name);
	}

	/**
	 * 获取配置文件配置项的值
	 *
	 * @param key 配置项key
	 */
	public static String getEnvironmentProperty(String key) {
		return applicationContext.getEnvironment().getProperty(key);
	}

	/**
	 * 获取spring.profiles.active值
	 */
	public static String getActiveProfile() {
		Environment environment = applicationContext.getEnvironment();
		if(null == environment) {
			return null;
		}
		String[] activeProfiles = environment.getActiveProfiles();
		if(null == activeProfiles || activeProfiles.length <= 0) {
			return null;
		}
		return activeProfiles[0];
	}
}
