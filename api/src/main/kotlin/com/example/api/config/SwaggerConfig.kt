package com.example.api.com.example.api.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SwaggerConfig {
    @Bean
    fun api(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("0.0.1")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI().info(Info().title("Swagger").description("Reservation API").version("0.0.1"))
    }
}