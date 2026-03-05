package com.example.caching;

import com.example.caching.product.dto.CreateProductRequest;
import com.example.caching.product.service.ProductService;
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
            productService.create(new CreateProductRequest("Duona", 2.99, 50));
            productService.create(new CreateProductRequest("Suris", 2.99, 50));
            productService.create(new CreateProductRequest("Pienas", 2.99, 50));
            productService.create(new CreateProductRequest("Balta duona", 2.99, 2));
            productService.create(new CreateProductRequest("Surio gaminys ", 2.99, 0));

            System.out.println("Demo run complete");
        };
    }
}

