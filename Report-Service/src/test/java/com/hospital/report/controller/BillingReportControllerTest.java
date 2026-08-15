package com.hospital.report.controller;

import com.hospital.report.dto.ReportResponse;
import com.hospital.report.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BillingReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    @DisplayName("GET /reports/billing/payment-collection - Get Payment Collection Report")
    void testGetPaymentCollectionReport() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("totalAmount", 500.00);

        ReportResponse<Map<String, Object>> response = ReportResponse.success("Payment Collection Report", data);

        Mockito.when(reportService.getPaymentCollectionReport("2026-08-15", "2026-08-15", "DOC1001")).thenReturn(response);

        mockMvc.perform(get("/reports/billing/payment-collection")
                        .param("fromDate", "2026-08-15")
                        .param("toDate", "2026-08-15")
                        .param("doctorId", "DOC1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalAmount").value(500.00));
    }

    @Test
    @DisplayName("GET /reports/billing/outstanding-dues - Get Outstanding Dues Report")
    void testGetOutstandingDuesReport() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("totalDues", 0.00);

        ReportResponse<Map<String, Object>> response = ReportResponse.success("Outstanding Dues Report", data);

        Mockito.when(reportService.getOutstandingDuesReport()).thenReturn(response);

        mockMvc.perform(get("/reports/billing/outstanding-dues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.reportName").value("Outstanding Dues Report"));
    }
}
