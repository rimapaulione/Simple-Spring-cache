package com.example.caching.product.service;

import com.example.caching.product.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductCacheTest {

    private final ProductCache productCache = new ProductCache();

    @AfterEach
    void cleanup() {
        productCache.clear();
    }

    @Test
    void test_shouldAddAndReturnProduct() {

        productCache.put(1L, new Product(1L, "Bread", 1.99, 5));

        Product result = productCache.get(1L);

        assertEquals("Bread", result.getName());
        assertNotNull(result);
    }

    @Test
    void test_shouldOverwriteExistingProduct() {
        productCache.put(1L, new Product(1L, "Bread", 1.99, 5));
        productCache.put(1L, new Product(1L, "Bread", 2.99, 10));

        Product result = productCache.get(1L);
        assertEquals(2.99, result.getPrice());
        assertEquals(10, result.getQuantity());
    }

    @Test
    void test_shouldReturnNullForMissingKey() {
        Product product = new Product(1L, "Bread", 1.99, 5);
        productCache.put(product.getId(), product);

        Product result = productCache.get(999L);
        assertNull(result);
    }

    @Test
    void test_shouldEvictProductById() {
        Product product1 = new Product(1L, "Bread", 1.99, 5);
        productCache.put(product1.getId(), product1);
        Product product2 = new Product(2L, "Milk", 3.99, 5);
        productCache.put(product2.getId(), product2);

        productCache.evict(2L);
        List<Product> result = productCache.getAll();

        assertEquals(1, result.size());
        assertNull(productCache.get(2L));
    }

    @Test
    void test_shouldReturnListOfProducts() {

        productCache.put(1L, new Product(1L, "Bread", 1.99, 5));
        productCache.put(2L, new Product(2L, "Milk", 1.99, 5));

        List<Product> result = productCache.getAll();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Bread")));
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Milk")));
    }

    @Test
    void test_shouldClearAllProducts() {
        productCache.put(1L, new Product(1L, "Bread", 1.99, 5));
        productCache.put(2L, new Product(2L, "Milk", 3.99, 5));

        productCache.clear();

        assertEquals(0, productCache.getAll().size());
        assertNull(productCache.get(1L));
    }

    @Test
    void test_getAllShouldReturnUnmodifiableCopy() {
        productCache.put(1L, new Product(1L, "Bread", 1.99, 5));

        List<Product> result = productCache.getAll();

        assertThrows(UnsupportedOperationException.class, () -> result.add(new Product()));
    }

}