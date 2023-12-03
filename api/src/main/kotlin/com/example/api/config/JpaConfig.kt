package com.example.api.com.example.api.config

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaAuditing
@ComponentScan(basePackages = ["com.example"])
@EntityScan(basePackages = ["com.example.database"])
@EnableJpaRepositories(basePackages = ["com.example.database"])
class JpaConfig {
    @PostConstruct
    fun started() {
    }
}