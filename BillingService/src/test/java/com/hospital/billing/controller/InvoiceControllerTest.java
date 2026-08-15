package com.hospital.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.billing.dto.CreateInvoiceDTO;
import com.hospital.billing.dto.InvoiceDTO;
import com.hospital.billing.service.InvoiceService;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    private ObjectMapper objectMapper;
    private InvoiceDTO mockInvoice;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockInvoice = new InvoiceDTO();
        mockInvoice.setInvoiceNumber("INV0000000001");
        mockInvoice.setPinNumber("PIN100001");
        mockInvoice.setSubTotal(new BigDecimal("500.00"));
        mockInvoice.setTotalAmount(new BigDecimal("500.00"));
        mockInvoice.setPaidAmount(new BigDecimal("500.00"));
        mockInvoice.setPaymentStatus("PAID");
    }

    @Test
    @DisplayName("POST /billing/invoices/create - Create Invoice Success")
    void testCreateInvoice() throws Exception {
        CreateInvoiceDTO createDTO = new CreateInvoiceDTO();
        createDTO.setPinNumber("PIN100001");
        createDTO.setDoctorId("DOC1001");
        createDTO.setInvoiceType("OPD");

        Mockito.when(invoiceService.createInvoice(any(CreateInvoiceDTO.class))).thenReturn(mockInvoice);

        mockMvc.perform(post("/billing/invoices/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.invoiceNumber").value("INV0000000001"))
                .andExpect(jsonPath("$.data.paymentStatus").value("PAID"));
    }

    @Test
    @DisplayName("GET /billing/invoices/{invoiceNumber} - Get Invoice by Number")
    void testGetInvoiceByNumber() throws Exception {
        Mockito.when(invoiceService.getInvoiceByNumber("INV0000000001")).thenReturn(mockInvoice);

        mockMvc.perform(get("/billing/invoices/INV0000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.invoiceNumber").value("INV0000000001"));
    }

    @Test
    @DisplayName("GET /billing/invoices/patient/{pinNumber} - Get Patient Invoices")
    void testGetPatientInvoices() throws Exception {
        Mockito.when(invoiceService.getPatientInvoices("PIN100001")).thenReturn(Collections.singletonList(mockInvoice));

        mockMvc.perform(get("/billing/invoices/patient/PIN100001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].pinNumber").value("PIN100001"));
    }
}
