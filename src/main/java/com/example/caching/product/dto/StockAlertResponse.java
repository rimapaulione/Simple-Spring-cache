package com.example.caching.product.dto;

import com.example.caching.product.model.StockAlert;

import java.time.LocalDateTime;

public record StockAlertResponse(
        Long id,
        Long productId,
        String productName,
        Integer remainingQuantity,
        LocalDateTime timestamp
) {
    public static StockAlertResponse from(StockAlert alert) {
        return new StockAlertResponse(
                alert.getId(),
                alert.getProductId(),
                alert.getProductName(),
                alert.getRemainingQuantity(),
                alert.getTimestamp()
        );
    }
}
