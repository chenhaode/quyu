package com.chd.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//启动Swagger2
@EnableSwagger2
// 扫描mybatis mapper包路径
@MapperScan(basePackages="com.chd.chat.mapper")
// 扫描 所有需要的包, 包含一些自用的工具类包 所在的路径
@ComponentScan(basePackages= {"com.chd.chat","org.n3r.idworker"})

public class SpringBootStart extends SpringBootServletInitializer {
	
	@Bean
	public SpringUtil getSpingUtil() {
		return new SpringUtil();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootStart.class, args);
	}

	/**
     * 重写configure
	 * @param builder
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		setRegisterErrorPageFilter(false);
		return builder.sources(SpringBootStart.class);
	}

}
