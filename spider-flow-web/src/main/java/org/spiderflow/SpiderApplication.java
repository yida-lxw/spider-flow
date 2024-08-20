package org.spiderflow;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@MapperScan("org.spiderflow.*.mapper")
public class SpiderApplication implements ServletContextInitializer {

	public static void main(String[] args) throws IOException {

		SpringApplication.run(SpiderApplication.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		//设置文本缓存1M
		servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize", Integer.toString((1024 * 1024)));
	}

	@Bean
	public PaginationInterceptor paginationInterceptor() {
		return new PaginationInterceptor();
	}

}
