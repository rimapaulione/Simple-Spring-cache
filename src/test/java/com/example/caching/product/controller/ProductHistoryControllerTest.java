package com.example.caching.product.controller;

import com.example.caching.product.dto.ProductHistoryResponse;
import com.example.caching.product.model.PurchaseStatus;
import com.example.caching.product.service.ProductHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ProductHistoryController.class)
class ProductHistoryControllerTest {
    private final String URL = "/products/history";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductHistoryService productHistoryService;

    @Test
    void test_shouldReturnListOfProductHistoriesWithoutStatus() throws Exception {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 10, 0);
        List<ProductHistoryResponse> productHistories = List.of(new ProductHistoryResponse(1L, 1L, "Bread", time, PurchaseStatus.ADDED));

        given(productHistoryService.getAll(null)).willReturn(productHistories);

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1L));

        verify(productHistoryService).getAll(null);
    }

    @Test
    void test_shouldReturnListOfProductHistoriesWithStatus() throws Exception {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 10, 0);
        List<ProductHistoryResponse> productHistories = List.of(new ProductHistoryResponse(1L, 1L, "Bread", time, PurchaseStatus.ADDED));

        given(productHistoryService.getAll(PurchaseStatus.ADDED)).willReturn(productHistories);

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("status", "ADDED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1L));

        verify(productHistoryService).getAll(PurchaseStatus.ADDED);
    }

    @Test
    void test_shouldReturnEmptyListOfProductHistoriesWithStatus() throws Exception {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 10, 0);
        List<ProductHistoryResponse> productHistories = List.of(new ProductHistoryResponse(1L, 1L, "Bread", time, PurchaseStatus.ADDED));

        given(productHistoryService.getAll(PurchaseStatus.DELETED)).willReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("status", "DELETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(productHistoryService).getAll(PurchaseStatus.DELETED);
    }

    @Test
    void test_shouldReturnListProductHistoriesByProductId() throws Exception {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 10, 0);
        List<ProductHistoryResponse> productHistories = List.of(new ProductHistoryResponse(1L, 1L, "Bread", time, PurchaseStatus.ADDED));

        given(productHistoryService.get(1L)).willReturn(productHistories);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/history/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ADDED"));

        verify(productHistoryService).get(1L);
    }

    @Test
    void test_shouldReturnEmptyListProductHistoriesByProductId() throws Exception {

        List<ProductHistoryResponse> productHistories = List.of();

        given(productHistoryService.get(2L)).willReturn(productHistories);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/history/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(productHistoryService).get(2L);
    }

    @Test
    void test_shouldReturnListByDateRange() throws Exception {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 1);
        LocalDateTime expectedStart = startDate.atStartOfDay();
        LocalDateTime expectedEnd = endDate.atTime(LocalTime.MAX);
        List<ProductHistoryResponse> productHistories = List.of(new ProductHistoryResponse(1L, 1L, "Bread", time, PurchaseStatus.ADDED));

        given(productHistoryService.getByDateRange(expectedStart, expectedEnd)).willReturn(productHistories);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/history/range")
                        .param("start", startDate.toString())
                        .param("end", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1L));

        verify(productHistoryService).getByDateRange(expectedStart, expectedEnd);
    }
}