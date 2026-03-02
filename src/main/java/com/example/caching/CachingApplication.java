package com.example.caching;

import com.example.caching.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching

public class CachingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CachingApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(ProductService productService) {
        return args -> {
            productService.create("Duona", 2.99, 50);
            productService.create("Suris", 3.99, 10);

            System.out.println("Demo run complete");
        };
    }
}

