package com.chd.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * @Description: Swagger配置类
 * @author: chd
 * @date: 2019年11月22日 下午4:10:20
 */
@Configuration
public class SwaggerConfig {

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				// 修正Byte转string的Bug
				.directModelSubstitute(Byte.class, Integer.class).select()
				.apis(RequestHandlerSelectors
				.basePackage("com.chd.chat.controller"))
				.paths(PathSelectors.any()).build();
	}

	/**
	 * @Description: 页面显示的开发者个人信息
	 * @return: ApiInfo   
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("趣语APP接口文档")
				.contact(new Contact("", "", ""))
				.description("不再尬聊，让我们用风趣幽默的用语聊天！")
				.version("1.0").build();
	}

}
