package org.spiderflow.configuration;

import org.spiderflow.core.constants.Constants;
import org.spiderflow.core.utils.SpringContextUtils;
import org.spiderflow.core.utils.StringUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yida
 * @package org.spiderflow.configuration
 * @date 2024-09-02 09:29
 * @description Type your description over here.
 */

@Component
@DependsOn("springContextUtils")
public class YmlPropertySourceFactory implements PropertySourceFactory {
	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource)
			throws IOException {
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
		factory.setResources(encodedResource.getResource());
		Properties properties = factory.getObject();
		String fileName = encodedResource.getResource().getFilename();
		String activeProfile = SpringContextUtils.getActiveProfile();
		if(StringUtils.isNotEmpty(activeProfile)) {
			String fileNameWithoutStufix = fileName.substring(0, fileName.lastIndexOf(Constants.DOT));
			String stufix = fileName.substring(fileName.lastIndexOf(Constants.DOT) + 1);
			fileName = fileNameWithoutStufix + Constants.HYPHEN + activeProfile + Constants.DOT + stufix;
		}
		return new PropertiesPropertySource(fileName, properties);
	}
}
