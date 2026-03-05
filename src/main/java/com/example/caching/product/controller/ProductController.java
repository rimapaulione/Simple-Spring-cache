package com.example.caching.product.controller;

import com.example.caching.product.dto.CreateProductRequest;
import com.example.caching.product.dto.ProductResponse;
import com.example.caching.product.dto.QuantityRequest;
import com.example.caching.product.dto.StatisticsResponse;
import com.example.caching.product.dto.UpdateNameRequest;
import com.example.caching.product.dto.UpdatePriceRequest;
import com.example.caching.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody final CreateProductRequest createProductRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(createProductRequest));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll(
            @RequestParam(required = false) final String name,
            @RequestParam(required = false) final String stockStatus) {
        return ResponseEntity.ok().body(productService.getAll(name, stockStatus));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable final Long id) {
        return ResponseEntity.ok().body(productService.get(id));
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics(){
        return ResponseEntity.ok().body(productService.getStatistics());
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<ProductResponse> updateName(@PathVariable final Long id, @Valid @RequestBody final UpdateNameRequest request) {
        return ResponseEntity.ok().body(productService.updateName(id, request.name()));
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<ProductResponse> updatePrice(@PathVariable final Long id, @Valid @RequestBody final UpdatePriceRequest request) {
        return ResponseEntity.ok().body(productService.updatePrice(id, request.price()));
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<ProductResponse> reduceQuantity(@PathVariable final Long id, @Valid @RequestBody final QuantityRequest request) {
        return ResponseEntity.ok(productService.reduceQuantity(id, request.amount()));
    }

    @PostMapping("/{id}/restock")
    public ResponseEntity<ProductResponse> extendQuantity(@PathVariable final Long id, @Valid @RequestBody final QuantityRequest request) {
        return ResponseEntity.ok().body(productService.extendQuantity(id, request.amount()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable final Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
