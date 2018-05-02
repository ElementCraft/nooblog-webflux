package com.noobug.nooblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class NooblogApplication {

    public static void main(String[] args) {
        SpringApplication.run(NooblogApplication.class, args);
    }

    @Bean
    RouterFunction<?> resourceRouter() {
        return RouterFunctions.resources("/upload/**", new FileSystemResource("upload/"));
    }
}
