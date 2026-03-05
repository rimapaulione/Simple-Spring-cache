package com.example.caching.product.dto;

public record StatisticsResponse(
        long totalCount,
        double inventoryValue,
        long lowStockCount,
        long outOfStockCount

) {

}
