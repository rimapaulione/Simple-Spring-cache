package com.example.caching.product.event;

import com.example.caching.product.model.ProductHistory;
import com.example.caching.product.model.StockAlert;
import com.example.caching.product.repository.ProductHistoryRepository;
import com.example.caching.product.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductListener {

    private final ProductHistoryRepository productHistoryRepository;
    private final StockRepository stockRepository;

    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener
    public void productHistoryEvent(ProductEvent event) {
        productHistoryRepository.save(ProductHistory.builder()
                .productId(event.getProductId())
                .productName(event.getProductName())
                .timestamp(LocalDateTime.now())
                .status(event.getStatus())
                .build());
    }

    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener
    public void productStockEvent(StockEvent event) {
        stockRepository.save(new StockAlert(null, event.getProductId(), event.getProductName(), event.getRemainingQuantity(), LocalDateTime.now()));
        log.info("You need to order product {}, because stock is {}", event.getProductName(), event.getRemainingQuantity());
    }
}
