package com.example.caching.product.service;


import com.example.caching.product.dto.StockAlertResponse;
import com.example.caching.product.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository stockRepository;

    public List<StockAlertResponse> getAll() {
        return stockRepository.findAll().stream()
                .map(StockAlertResponse::from).toList();
    }
}
