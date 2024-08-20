package org.spiderflow.configuration;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yida
 * @package org.spiderflow.configuration
 * @date 2024-08-20 23:04
 * @description Type your description over here.
 */
@Configuration
public class UndertowConfig {
	@Value("${server.undertow.record-request-start-time:false}")
	private boolean recordRequestStartTime;

	@Bean
	public UndertowServletWebServerFactory undertowServletWebServerFactory() {
		UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
		factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
			@Override
			public void customize(Undertow.Builder builder) {
				builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, recordRequestStartTime);
			}
		});
		return factory;
	}
}
