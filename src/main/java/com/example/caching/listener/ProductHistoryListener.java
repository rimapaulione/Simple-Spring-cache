package com.example.caching.listener;

import com.example.caching.event.ProductEvent;
import com.example.caching.model.ProductHistory;
import com.example.caching.repository.ProductHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class ProductHistoryListener {

    private final ProductHistoryRepository productHistoryRepository;

    @EventListener
    public void productHistoryEvent(ProductEvent event) {
        productHistoryRepository.save(ProductHistory.builder()
                .productName(event.getProductName())
                .timeStamp(new Date())
                .status(event.getStatus())
                .build());
    }
}
