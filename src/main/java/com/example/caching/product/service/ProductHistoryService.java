package com.example.caching.product.service;


import com.example.caching.product.model.ProductHistory;
import com.example.caching.product.model.PurchaseStatus;
import com.example.caching.product.repository.ProductHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductHistoryService {

    private final ProductHistoryRepository productHistoryRepository;


    public List<ProductHistory> getAll(final PurchaseStatus status) {
        if (status == null) {
            return productHistoryRepository.findAll();
        }
        return productHistoryRepository.findByStatus(status);
    }


    public List<ProductHistory> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return productHistoryRepository.findByTimestampBetween(start, end);
    }

}
