package com.example.caching.service;

import com.example.caching.model.Product;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProductCache {
    private static final Map<Long, Product> CACHE = new ConcurrentHashMap<>();


    public static void put(final Long id, final Product product) {
        CACHE.put(id, product);
    }

    public static Product get(final Long id) {
        return CACHE.get(id);
    }

    public static void evict(final Long id) {
        CACHE.remove(id);
    }

    public static void clear() {
        CACHE.clear();
    }
}
