package com.example.caching.product.event;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StockEvent {

    private Long productId;
    private String productName;
    private Integer remainingQuantity;
}
