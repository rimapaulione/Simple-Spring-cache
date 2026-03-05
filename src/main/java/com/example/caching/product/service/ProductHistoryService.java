package com.example.caching.product.service;


import com.example.caching.product.dto.ProductHistoryResponse;
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


    public List<ProductHistoryResponse> getAll(final PurchaseStatus status) {
        if (status == null) {
            return productHistoryRepository.findAll().stream().map(ProductHistoryResponse::from).toList();
        }
        return productHistoryRepository.findByStatus(status).stream().map(ProductHistoryResponse::from).toList();
    }


    public List<ProductHistoryResponse> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return productHistoryRepository.findByTimestampBetween(start, end).stream().map(ProductHistoryResponse::from).toList();
    }

}
