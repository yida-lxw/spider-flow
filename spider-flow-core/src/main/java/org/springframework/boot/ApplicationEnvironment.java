package org.springframework.boot;

import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

/**
 * @author yida
 * @package org.springframework.boot
 * @date 2024-09-02 11:44
 * @description Type your description over here.
 */
public class ApplicationEnvironment extends StandardEnvironment {

	@Override
	protected String doGetActiveProfilesProperty() {
		return null;
	}

	@Override
	protected String doGetDefaultProfilesProperty() {
		return null;
	}

	@Override
	protected ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
		return ConfigurationPropertySources.createPropertyResolver(propertySources);
	}
}
