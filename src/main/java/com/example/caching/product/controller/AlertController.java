package com.example.caching.product.controller;


import com.example.caching.product.dto.StockAlertResponse;
import com.example.caching.product.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/alert")
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<StockAlertResponse>> getAll() {
        return ResponseEntity.ok().body(alertService.getAll());
    }
}
