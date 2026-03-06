package com.example.caching.product.controller;


import com.example.caching.product.dto.ProductHistoryResponse;
import com.example.caching.product.model.PurchaseStatus;
import com.example.caching.product.service.ProductHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/history")
public class ProductHistoryController {

    private final ProductHistoryService productHistoryService;

    @GetMapping
    public ResponseEntity<List<ProductHistoryResponse>> getAll(@RequestParam(required = false) PurchaseStatus status) {
        return ResponseEntity.ok().body(productHistoryService.getAll(status));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductHistoryResponse>> get(@PathVariable final Long productId) {
        return ResponseEntity.ok().body(productHistoryService.get(productId));
    }

    @GetMapping("/range")
    public ResponseEntity<List<ProductHistoryResponse>> getByDateRange(
            @RequestParam("start") LocalDate start, @RequestParam("end") LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        return ResponseEntity.ok().body(productHistoryService.getByDateRange(startDateTime, endDateTime));
    }
}
