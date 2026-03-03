package com.example.caching.listener;

import com.example.caching.event.ProductEvent;
import com.example.caching.event.StockEvent;
import com.example.caching.model.ProductHistory;
import com.example.caching.model.StockAlert;
import com.example.caching.repository.ProductHistoryRepository;
import com.example.caching.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductListener {

    private final ProductHistoryRepository productHistoryRepository;
    private final StockRepository stockRepository;

    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener
    public void productHistoryEvent(ProductEvent event) {

        log.info("EVENT RECEIVED");
        productHistoryRepository.save(ProductHistory.builder()
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
