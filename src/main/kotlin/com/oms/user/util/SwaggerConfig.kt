package com.oms.user.util

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.oms.user"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .apiInfo(this.apiInfo())
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("OMS User")
                .description("OMS User API")
                .version("1.0")
                .contact(
                        Contact(
                                "Jongkil Park",
                                "https://oms.park108.net",
                                "park108@gmail.com"
                        )
                )
                .build()
    }

}