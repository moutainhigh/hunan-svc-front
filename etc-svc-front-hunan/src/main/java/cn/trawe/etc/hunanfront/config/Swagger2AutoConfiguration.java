package cn.trawe.etc.hunanfront.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
* @ClassName: Swagger2AutoConfiguration  
* @Description: TODO
* @author jianjun.chai  
* @date 2019年4月3日  
*
 */
@Configuration
@EnableSwagger2
public class Swagger2AutoConfiguration {


	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.useDefaultResponseMessages(false)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("cn.trawe"))
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("Feign HN Service")
				.description("Feign Clients of HN Service")
				.version("2.0")
				.build();
	}
}