package com.example.caching.controller;


import com.example.caching.model.Product;
import com.example.caching.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Product create(@RequestBody final Product product) {
        return productService.create(product.getName(), product.getPrice(), product.getQuantity());
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable final Long id) {
        return productService.get(id);
    }

    @PutMapping("/{id}/price")
    public Product updatePrice(@PathVariable final Long id, @RequestBody final Double price) {
        return productService.updatePrice(id, price);
    }

    @PostMapping("/{id}/purchase")
    public Product reduceQuantity(@PathVariable Long id, @RequestParam int amount) {
        return productService.reduceQuantity(id, amount);
    }

      @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


}
