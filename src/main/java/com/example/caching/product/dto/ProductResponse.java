package com.example.caching.product.dto;

import com.example.caching.product.model.Product;

import static com.example.caching.product.util.ProductConstants.MIN_STOCK;

public record ProductResponse(
        Long id,
        String name,
        Double price,
        Integer quantity,
        String stockStatus
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                resolveStockStatus(product.getQuantity())
        );
    }

    private static String resolveStockStatus(Integer quantity) {
        if (quantity == null || quantity == 0) return "OUT_OF_STOCK";
        if (quantity < MIN_STOCK) return "LOW_STOCK";
        return "IN_STOCK";
    }
}
