package com.example.caching.listener;

import com.example.caching.event.ProductEvent;
import com.example.caching.model.ProductHistory;
import com.example.caching.repository.ProductHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ProductHistoryListener {

    private final ProductHistoryRepository productHistoryRepository;

    @TransactionalEventListener
    public void productHistoryEvent(ProductEvent event) {
        productHistoryRepository.save(ProductHistory.builder()
                .productName(event.getProductName())
                .timestamp(LocalDateTime.now())
                .status(event.getStatus())
                .build());
    }
}
