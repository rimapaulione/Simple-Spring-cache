package com.example.caching.service.product;


import com.example.caching.product.dto.ProductHistoryResponse;
import com.example.caching.product.model.ProductHistory;
import com.example.caching.product.model.PurchaseStatus;
import com.example.caching.product.repository.ProductHistoryRepository;
import com.example.caching.product.service.ProductHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductHistoryServiceTest {

    @Mock
    private ProductHistoryRepository productHistoryRepository;

    @InjectMocks
    private ProductHistoryService productHistoryService;


    @Test
    void test_shouldGetAllProductHistory() {
        ProductHistory history1 = new ProductHistory(1L, 1L, "Bread", LocalDateTime.now(), PurchaseStatus.ADDED);
        ProductHistory history2 = new ProductHistory(2L, 2L, "Milk", LocalDateTime.now(), PurchaseStatus.DELETED);

        when(productHistoryRepository.findAll()).thenReturn(List.of(history1, history2));

        List<ProductHistoryResponse> result = productHistoryService.getAll(null);

        assertEquals(2, result.size());
        verify(productHistoryRepository).findAll();

    }

    @Test
    void test_shouldGetAllProductHistoryByStatus() {
        ProductHistory history1 = new ProductHistory(1L, 1L, "Bread", LocalDateTime.now(), PurchaseStatus.ADDED);
        ProductHistory history2 = new ProductHistory(2L, 2L, "Milk", LocalDateTime.now(), PurchaseStatus.DELETED);

        when(productHistoryRepository.findByStatus(PurchaseStatus.ADDED)).thenReturn(List.of(history1));

        List<ProductHistoryResponse> result = productHistoryService.getAll(PurchaseStatus.ADDED);

        assertEquals(1, result.size());
        assertEquals(PurchaseStatus.ADDED, result.get(0).status());
        verify(productHistoryRepository).findByStatus(PurchaseStatus.ADDED);

    }

    @Test
    void test_shouldGetHistoryByDateRange() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 5, 23, 59);
        ProductHistory history = new ProductHistory(1L, 1L, "Bread", LocalDateTime.of(2026, 3, 3, 12, 0), PurchaseStatus.PURCHASED);

        when(productHistoryRepository.findByTimestampBetween(start, end)).thenReturn(List.of(history));

        List<ProductHistoryResponse> result = productHistoryService.getByDateRange(start, end);

        assertEquals(1, result.size());
        assertEquals("Bread", result.get(0).productName());
        verify(productHistoryRepository).findByTimestampBetween(start, end);
    }

    @Test
    void test_shouldGetHistoryByProductId() {
        ProductHistory history1 = new ProductHistory(1L, 1L, "Bread", LocalDateTime.now(), PurchaseStatus.ADDED);
        ProductHistory history2 = new ProductHistory(2L, 1L, "Bread", LocalDateTime.now(), PurchaseStatus.PURCHASED);

        when(productHistoryRepository.findByProductId(1L)).thenReturn(List.of(history1, history2));

        List<ProductHistoryResponse> result = productHistoryService.getHistory(1L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).productId());
        assertEquals(1L, result.get(1).productId());
        verify(productHistoryRepository).findByProductId(1L);
    }

    @Test
    void test_shouldReturnEmptyListWhenNoHistoryForProduct() {
        when(productHistoryRepository.findByProductId(999L)).thenReturn(List.of());

        List<ProductHistoryResponse> result = productHistoryService.getHistory(999L);

        assertTrue(result.isEmpty());
        verify(productHistoryRepository).findByProductId(999L);
    }
}