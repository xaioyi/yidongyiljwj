package com.sectong;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * Swagger 配置文件
 *
 * @author jiekechoo
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("v1").select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/v2/mongo/**")).build().apiInfo(apiInfo());
    }

    @Bean
    public Docket api_two() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("v2").select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/ljwjpay/**")).build().apiInfo(apiInfo());
    }


    @SuppressWarnings("deprecation")
    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("LJWJ API 手册", "API手册", "0.1.0", "", "",
                "Apache 2.0", "");
        return apiInfo;
    }

}


//	@Bean
//	public Docket createRestApi() {
//		return new Docket(DocumentationType.SWAGGER_2)
//				.apiInfo(apiInfo())
//				.select()
//				.apis(RequestHandlerSelectors.basePackage("com.sectong.controller"))
//				.paths(PathSelectors.any())
//				.build();
//	}
//
//	private ApiInfo apiInfo() {
//		return new ApiInfoBuilder()
//				.title("Spring 中使用Swagger2构建RESTful APIs")
//				.termsOfServiceUrl("http://blog.csdn.net/he90227")
//				.contact("逍遥飞鹤")
//				.version("1.1")
//				.build();
//	}
