package com.example.caching.product.controller;

import com.example.caching.product.dto.ProductResponse;
import com.example.caching.product.dto.StatisticsResponse;
import com.example.caching.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private final String URL = "/products";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void test_shouldCreateProduct() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Bread", 2.50, 10, "IN_STOCK");

        given(productService.create(any())).willReturn(response);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Bread", "price": 2.50, "quantity": 10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Bread"))
                .andExpect(jsonPath("$.price").value(2.50))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.stockStatus").value("IN_STOCK"));

        verify(productService).create(any());
    }

    @Test
    void test_shouldReturnBadRequestWhenCreatingWithBlankName() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "price": 2.50, "quantity": 10}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_shouldReturnBadRequestWhenCreatingWithNegativePrice() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Bread", "price": -1, "quantity": 10}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_shouldReturnBadRequestWhenCreatingWithNegativeQuantity() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Bread", "price": 2.50, "quantity": -1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_shouldGetAllProducts() throws Exception {
        List<ProductResponse> products = List.of(
                new ProductResponse(1L, "Bread", 2.50, 10, "IN_STOCK"),
                new ProductResponse(2L, "Milk", 1.80, 3, "LOW_STOCK")
        );

        given(productService.getAll(null, null)).willReturn(products);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Bread"))
                .andExpect(jsonPath("$[1].name").value("Milk"));

        verify(productService).getAll(null, null);
    }

    @Test
    void test_shouldGetAllProductsFilteredByName() throws Exception {
        List<ProductResponse> products = List.of(
                new ProductResponse(1L, "Bread", 2.50, 10, "IN_STOCK")
        );

        given(productService.getAll("Bread", null)).willReturn(products);

        mockMvc.perform(get(URL).param("name", "Bread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Bread"));

        verify(productService).getAll("Bread", null);
    }

    @Test
    void test_shouldGetAllProductsFilteredByStockStatus() throws Exception {
        List<ProductResponse> products = List.of(
                new ProductResponse(2L, "Milk", 1.80, 3, "LOW_STOCK")
        );

        given(productService.getAll(null, "LOW_STOCK")).willReturn(products);

        mockMvc.perform(get(URL).param("stockStatus", "LOW_STOCK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].stockStatus").value("LOW_STOCK"));

        verify(productService).getAll(null, "LOW_STOCK");
    }

    @Test
    void test_shouldGetProductById() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Bread", 2.50, 10, "IN_STOCK");

        given(productService.get(1L)).willReturn(response);

        mockMvc.perform(get(URL + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Bread"));

        verify(productService).get(1L);
    }

    @Test
    void test_shouldGetStatistics() throws Exception {
        StatisticsResponse stats = new StatisticsResponse(5, 150.0, 2, 1);

        given(productService.getStatistics()).willReturn(stats);

        mockMvc.perform(get(URL + "/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(5))
                .andExpect(jsonPath("$.inventoryValue").value(150.0))
                .andExpect(jsonPath("$.lowStockCount").value(2))
                .andExpect(jsonPath("$.outOfStockCount").value(1));

        verify(productService).getStatistics();
    }

    @Test
    void test_shouldUpdateName() throws Exception {
        ProductResponse response = new ProductResponse(1L, "White Bread", 2.50, 10, "IN_STOCK");

        given(productService.updateName(1L, "White Bread")).willReturn(response);

        mockMvc.perform(put(URL + "/{id}/name", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "White Bread"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("White Bread"));

        verify(productService).updateName(1L, "White Bread");
    }

    @Test
    void test_shouldReturnBadRequestWhenUpdatingWithBlankName() throws Exception {
        mockMvc.perform(put(URL + "/{id}/name", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": ""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_shouldUpdatePrice() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Bread", 3.00, 10, "IN_STOCK");

        given(productService.updatePrice(1L, 3.00)).willReturn(response);

        mockMvc.perform(put(URL + "/{id}/price", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"price": 3.00}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(3.00));

        verify(productService).updatePrice(1L, 3.00);
    }

    @Test
    void test_shouldReturnBadRequestWhenUpdatingWithNegativePrice() throws Exception {
        mockMvc.perform(put(URL + "/{id}/price", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"price": -5.00}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_shouldPurchaseProduct() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Bread", 2.50, 7, "IN_STOCK");

        given(productService.reduceQuantity(1L, 3)).willReturn(response);

        mockMvc.perform(post(URL + "/{id}/purchase", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 3}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(7));

        verify(productService).reduceQuantity(1L, 3);
    }

    @Test
    void test_shouldReturnBadRequestWhenPurchasingWithZeroAmount() throws Exception {
        mockMvc.perform(post(URL + "/{id}/purchase", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 0}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_shouldRestockProduct() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Bread", 2.50, 15, "IN_STOCK");

        given(productService.extendQuantity(1L, 5)).willReturn(response);

        mockMvc.perform(post(URL + "/{id}/restock", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(15));

        verify(productService).extendQuantity(1L, 5);
    }

    @Test
    void test_shouldDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete(URL + "/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }
}
