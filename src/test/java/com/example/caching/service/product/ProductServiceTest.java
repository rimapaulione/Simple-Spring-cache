package com.example.caching.service.product;

import com.example.caching.product.dto.CreateProductRequest;
import com.example.caching.product.dto.ProductResponse;
import com.example.caching.product.event.ProductEvent;
import com.example.caching.product.event.StockEvent;
import com.example.caching.product.model.Product;
import com.example.caching.product.repository.ProductRepository;
import com.example.caching.product.service.ProductCache;
import com.example.caching.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCache productCache;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private ProductService productService;

    @Test
    void test_shouldCreateAndReturnProduct() {
        Product product = new Product(1L, "Bread", 1.99, 5);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse result = productService.create(new CreateProductRequest("Bread", 1.99, 5));

        assertNotNull(result);
        assertEquals("Bread", result.name());
        assertEquals("IN_STOCK", result.stockStatus());
        verify(productRepository).save(any(Product.class));
        verify(productCache).put(1L, product);
        verify(publisher).publishEvent(any(ProductEvent.class));
        verify(publisher, never()).publishEvent(any(StockEvent.class));
    }

    @Test
    void test_shouldPublishStockEventWhenQuantityBelowMinStock() {
        Product product = new Product(1L, "Bread", 1.99, 3);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse result = productService.create(new CreateProductRequest("Bread", 1.99, 3));

        assertEquals("LOW_STOCK", result.stockStatus());
        verify(publisher).publishEvent(any(ProductEvent.class));
        verify(publisher).publishEvent(any(StockEvent.class));
    }

    @Test
    void test_shouldReturnAllProductsFromCache() {
        Product product = new Product(1L, "Bread", 1.99, 5);

        when(productCache.getAll()).thenReturn(List.of(product));

        List<ProductResponse> result = productService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bread", result.get(0).name());
        verify(productCache).getAll();
        verify(productRepository, never()).findAll();
    }

    @Test
    void test_shouldReturnAllProductsFromDb() {
        Product product = new Product(1L, "Bread", 1.99, 5);

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> result = productService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bread", result.get(0).name());
        verify(productCache).getAll();
        verify(productRepository).findAll();
    }

    @Test
    void test_shouldReturnFromCacheById() {
        Product product = new Product(1L, "Bread", 1.99, 5);

        when(productCache.get(1L)).thenReturn(product);

        ProductResponse result = productService.get(1L);

        assertNotNull(result);
        assertEquals("Bread", result.name());
        assertEquals("IN_STOCK", result.stockStatus());
        verify(productCache).get(1L);
        verify(productRepository, never()).findById(1L);
    }

    @Test
    void test_shouldReturnFromDbById() {
        Product product = new Product(1L, "Bread", 1.99, 5);
        when(productCache.get(1L)).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse result = productService.get(1L);

        assertNotNull(result);
        assertEquals("Bread", result.name());
        verify(productCache).get(1L);
        verify(productRepository).findById(1L);
        verify(productCache).put(1L, product);
    }

    @Test
    void test_shouldUpdateName() {
        Product product = new Product(1L, "Bread", 1.99, 5);
        String newName = "White Bread";

        when(productCache.get(1L)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse result = productService.updateName(1L, newName);

        assertNotNull(result);
        assertEquals("White Bread", result.name());
        verify(productRepository).save(any(Product.class));
        verify(productCache).put(1L, product);
        verify(publisher).publishEvent(any(ProductEvent.class));
    }

    @Test
    void test_shouldThrowWhenUpdatingNameOfNonExistentProduct() {
        when(productCache.get(1L)).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> productService.updateName(1L, "New Name"));

        verify(productRepository, never()).save(any());
    }

    @Test
    void test_shouldUpdatePriceWhenNewPriceIsPositive() {
        Product product = new Product(1L, "Bread", 1.99, 5);
        Double newPrice = 3.99;

        when(productCache.get(1L)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse result = productService.updatePrice(1L, newPrice);

        assertNotNull(result);
        assertEquals(3.99, result.price());
        verify(productRepository).save(any(Product.class));
        verify(productCache).put(1L, product);
        verify(publisher).publishEvent(any(ProductEvent.class));
    }

    @Test
    void test_shouldThrowWhenPriceIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.updatePrice(1L, -1.0));

        verify(productRepository, never()).save(any());
    }

    @Test
    void test_shouldReduceQuantityWhenAmountIsCorrect() {
        Product product = new Product(1L, "Bread", 1.99, 15);
        int amount = 5;

        when(productCache.get(1L)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse result = productService.reduceQuantity(1L, amount);

        assertNotNull(result);
        assertEquals(10, result.quantity());
        assertEquals("IN_STOCK", result.stockStatus());
        verify(productRepository).save(any(Product.class));
        verify(productCache).put(1L, product);
        verify(publisher).publishEvent(any(ProductEvent.class));
        verify(publisher, never()).publishEvent(any(StockEvent.class));
    }

    @Test
    void test_shouldReduceQuantityWhenAmountIsCorrectAndCallPublisher() {
        Product product = new Product(1L, "Bread", 1.99, 7);
        int amount = 5;

        when(productCache.get(1L)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse result = productService.reduceQuantity(1L, amount);

        assertNotNull(result);
        assertEquals(2, result.quantity());
        assertEquals("LOW_STOCK", result.stockStatus());
        verify(productRepository).save(any(Product.class));
        verify(productCache).put(1L, product);
        verify(publisher).publishEvent(new StockEvent(product.getId(), product.getName(), product.getQuantity()));
    }

    @Test
    void test_shouldThrowWhenAmountNegative() {
        assertThrows(IllegalArgumentException.class, () -> productService.reduceQuantity(1L, 0));
        verify(productRepository, never()).save(any());
    }

    @Test
    void test_shouldThrowWhenAmountExceedAvailableQuantity() {
        Product product = new Product(1L, "Bread", 1.99, 7);
        when(productCache.get(1L)).thenReturn(product);

        assertThrows(IllegalArgumentException.class, () -> productService.reduceQuantity(1L, 10));
        verify(productRepository, never()).save(any());
    }

    @Test
    void test_shouldExtendQuantity() {
        Product product = new Product(1L, "Bread", 1.99, 5);

        when(productCache.get(1L)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse result = productService.extendQuantity(1L, 10);

        assertEquals(15, result.quantity());
        assertEquals("IN_STOCK", result.stockStatus());
        verify(productRepository).save(any(Product.class));
        verify(productCache).put(1L, product);
        verify(publisher).publishEvent(any(ProductEvent.class));
        verify(publisher, never()).publishEvent(any(StockEvent.class));
    }

    @Test
    void test_shouldThrowWhenExtendAmountIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.extendQuantity(1L, 0));

        assertThrows(IllegalArgumentException.class,
                () -> productService.extendQuantity(1L, -5));

        verify(productRepository, never()).save(any());
    }

    @Test
    void test_shouldDeleteProduct() {
        Product product = new Product(1L, "Bread", 1.99, 5);

        when(productCache.get(1L)).thenReturn(product);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
        verify(productCache).evict(1L);
        verify(publisher).publishEvent(any(ProductEvent.class));
    }

    @Test
    void test_shouldThrowWhenDeletingNonExistentProduct() {
        when(productCache.get(1L)).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> productService.deleteProduct(1L));

        verify(productRepository, never()).deleteById(1L);
        verify(productCache, never()).evict(1L);
    }

}
