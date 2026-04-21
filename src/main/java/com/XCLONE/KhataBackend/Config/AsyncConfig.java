package com.XCLONE.KhataBackend.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    /**
     * Dedicated thread pool for receipt notification tasks.
     * Keeps notification work isolated from the main request threads.
     */
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("notification-");
        executor.setRejectedExecutionHandler((r, e) ->
                log.error("Notification task rejected — queue is full!")
        );
        executor.initialize();
        return executor;
    }
}
