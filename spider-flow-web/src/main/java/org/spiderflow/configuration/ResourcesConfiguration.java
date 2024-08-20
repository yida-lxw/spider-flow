package org.spiderflow.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置放行静态资源文件
 *
 * @author Administrator
 */
@Configuration
public class ResourcesConfiguration implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				//.allowedOrigins("*")
				.allowedOriginPatterns("")
				.allowedMethods(new String[] {"GET", "POST", "PUT", "OPTIONS"})
				.allowedHeaders("*")
				.allowCredentials(true).maxAge(3600);
	}
}
