package com.example.caching.product.service;

import com.example.caching.product.model.Product;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class ProductCache {
    private final Map<Long, Product> cache = new ConcurrentHashMap<>();


    public void put(final Long id, final Product product) {
        cache.put(id, product);
    }

    public Product get(final Long id) {
        return cache.get(id);
    }

    public void evict(final Long id) {
        cache.remove(id);
    }

    public void clear() {
        cache.clear();
    }

    public List<Product> getAll() {

        return List.copyOf(cache.values());
    }
}
