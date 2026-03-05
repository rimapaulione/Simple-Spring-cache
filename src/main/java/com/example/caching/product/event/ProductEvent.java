package com.example.caching.product.event;


import com.example.caching.product.model.PurchaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data

public class ProductEvent {

    private final String productName;
    private final PurchaseStatus status;

}
