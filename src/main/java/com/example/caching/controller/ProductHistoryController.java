package com.example.caching.controller;


import com.example.caching.model.ProductHistory;
import com.example.caching.service.ProductHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/history")
public class ProductHistoryController {

    private final ProductHistoryService productHistoryService;

    @GetMapping
    public List<ProductHistory> getAll(){
       return productHistoryService.getAll();
    }
}
