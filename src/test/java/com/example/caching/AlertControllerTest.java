package com.example.caching;

import com.example.caching.product.controller.AlertController;
import com.example.caching.product.dto.StockAlertResponse;
import com.example.caching.product.service.AlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(AlertController.class)
class AlertControllerTest {
    private final String URL = "/products/alert";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlertService alertService;

    @Test
    void test_shouldReturnStockAlerts() throws  Exception{
        LocalDateTime time = LocalDateTime.of(2024,1,1,10,0);
        List<StockAlertResponse> alerts = List.of(
                new StockAlertResponse(1L, 1L, "Bread", 2, time));

        given(alertService.getAll()).willReturn(alerts);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Bread"));
        verify(alertService).getAll();

    }
}


