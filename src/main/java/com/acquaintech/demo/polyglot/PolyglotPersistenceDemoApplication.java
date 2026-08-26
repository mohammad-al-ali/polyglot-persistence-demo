package com.acquaintech.demo.polyglot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class PolyglotPersistenceDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolyglotPersistenceDemoApplication.class, args);
    }
}
