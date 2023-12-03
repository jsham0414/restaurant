package com.example.api.com.example.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar

@Configuration
class SchedulerConfig : SchedulingConfigurer {
    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val threadPool = ThreadPoolTaskScheduler()
        val n = Runtime.getRuntime().availableProcessors()
        threadPool.setPoolSize(n)
        threadPool.initialize()
        taskRegistrar.setTaskScheduler(threadPool)
    }
}

