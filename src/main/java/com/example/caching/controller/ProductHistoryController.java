package com.example.caching.controller;


import com.example.caching.model.ProductHistory;
import com.example.caching.model.PurchaseStatus;
import com.example.caching.service.ProductHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
    public List<ProductHistory> getAll(@RequestParam(required = false) PurchaseStatus status) {
        return productHistoryService.getAll(status);
    }

    @GetMapping("/range")
    public List<ProductHistory> getByDateRange(
            @RequestParam("start") LocalDate start, @RequestParam("end") LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        return productHistoryService.getByDateRange(startDateTime, endDateTime);

    }
}

