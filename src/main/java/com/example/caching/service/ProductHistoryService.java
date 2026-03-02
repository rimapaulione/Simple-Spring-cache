package com.example.caching.service;


import com.example.caching.model.ProductHistory;
import com.example.caching.repository.ProductHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductHistoryService {

    private final ProductHistoryRepository productHistoryRepository;

    public List<ProductHistory> getAll() {
        return productHistoryRepository.findAll();

    }
}

