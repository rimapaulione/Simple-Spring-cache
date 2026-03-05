package com.example.caching.product.dto;

import com.example.caching.product.model.ProductHistory;
import com.example.caching.product.model.PurchaseStatus;

import java.time.LocalDateTime;

public record ProductHistoryResponse(
        Long id,
        String productName,
        LocalDateTime timestamp,
        PurchaseStatus status
) {
    public static ProductHistoryResponse from(ProductHistory history) {
        return new ProductHistoryResponse(
                history.getId(),
                history.getProductName(),
                history.getTimestamp(),
                history.getStatus()
        );
    }
}
