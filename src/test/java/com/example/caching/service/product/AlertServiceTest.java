package com.example.caching.service.product;


import com.example.caching.product.dto.StockAlertResponse;
import com.example.caching.product.model.StockAlert;
import com.example.caching.product.repository.AlertRepository;
import com.example.caching.product.service.AlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void test_shouldGetAllAlerts() {
        StockAlert alert1 = new StockAlert(1L, 1L, "Bread", 2, LocalDateTime.now());
        StockAlert alert2 = new StockAlert(2L, 2L, "Milk", 4, LocalDateTime.now());

        when(alertRepository.findAll()).thenReturn(List.of(alert1, alert2));

        List<StockAlertResponse> result = alertService.getAll();

        assertEquals(2, result.size());
        verify(alertRepository).findAll();
    }
}