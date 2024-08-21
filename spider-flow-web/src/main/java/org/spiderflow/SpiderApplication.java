package org.spiderflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = {"org.spiderflow.mapper", "org.spiderflow.**.mapper"})
public class SpiderApplication implements ServletContextInitializer {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(SpiderApplication.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

	}
}
