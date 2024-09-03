package org.spiderflow.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author yida
 * @package org.spiderflow.configuration
 * @date 2024-09-03 16:49
 * @description Type your description over here.
 */
@Primary
@Component
public class CustomPropertySourcesPlaceHolderConfigure extends PropertySourcesPlaceholderConfigurer {
	@Autowired
	protected Environment environment;

	/**
	 * {@code PropertySources} from the given {@link Environment}
	 * <p>
	 * will be searched when replacing ${...} placeholders.
	 *
	 * @see #setPropertySources
	 * @see #postProcessBeanFactory
	 */
	@Override
	public void setEnvironment(Environment environment) {
		super.setEnvironment(environment);
		this.environment = environment;
	}


	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, ConfigurablePropertyResolver propertyResolver) throws BeansException {
		Field field = null;
		MutablePropertySources propertySource = null;
		try {
			field = propertyResolver.getClass().getDeclaredField("propertySources");
			boolean access = field.isAccessible();
			field.setAccessible(true);
			propertySource = (MutablePropertySources) field.get(propertyResolver);
			field.setAccessible(access);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		PropertySource source = new PropertySource<Environment>(ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME, this.environment) {
			@Override
			public String getProperty(String key) {
				// 对数组进行兼容
				String ans = this.source.getProperty(key);
				if (ans != null) {
					return ans;
				}
				StringBuilder builder = new StringBuilder();
				String prefix = key.contains(":") ? key.substring(key.indexOf(":")) : key;
				int i = 0;
				while (true) {
					String subKey = prefix + "[" + i + "]";
					ans = this.source.getProperty(subKey);
					if (ans == null) {
						return i == 0 ? null : builder.toString();
					}
					if (i > 0) {
						builder.append(",");
					}
					builder.append(ans);
					++i;
				}
			}
		};
		propertySource.addLast(source);
		super.processProperties(beanFactoryToProcess, propertyResolver);
	}
}
