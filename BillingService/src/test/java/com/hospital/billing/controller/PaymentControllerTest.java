package com.hospital.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.billing.dto.PaymentDTO;
import com.hospital.billing.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private ObjectMapper objectMapper;
    private PaymentDTO mockPayment;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockPayment = new PaymentDTO();
        mockPayment.setPaymentId("PAY0000000001");
        mockPayment.setInvoiceNumber("INV0000000001");
        mockPayment.setAmount(new BigDecimal("500.00"));
        mockPayment.setPaymentMode("CASH");
        mockPayment.setPaymentStatus("SUCCESS");
        mockPayment.setPaymentDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /billing/payments/process/{invoiceNumber} - Process Payment Success")
    void testProcessPayment() throws Exception {
        PaymentDTO processDTO = new PaymentDTO();
        processDTO.setAmount(new BigDecimal("500.00"));
        processDTO.setPaymentMode("CASH");

        Mockito.when(paymentService.processPayment(eq("INV0000000001"), any(PaymentDTO.class))).thenReturn(mockPayment);

        mockMvc.perform(post("/billing/payments/process/INV0000000001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(processDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentId").value("PAY0000000001"))
                .andExpect(jsonPath("$.data.paymentMode").value("CASH"));
    }

    @Test
    @DisplayName("GET /billing/payments/invoice/{invoiceNumber} - Get Payments for Invoice")
    void testGetPaymentsForInvoice() throws Exception {
        Mockito.when(paymentService.getInvoicePayments("INV0000000001")).thenReturn(Collections.singletonList(mockPayment));

        mockMvc.perform(get("/billing/payments/invoice/INV0000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].paymentId").value("PAY0000000001"));
    }
}
