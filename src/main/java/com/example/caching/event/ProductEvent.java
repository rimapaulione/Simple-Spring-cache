package com.example.caching.event;


import com.example.caching.model.PurchaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder

public class ProductEvent {

    private final String productName;
    private final PurchaseStatus status;

}
