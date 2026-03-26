package com.hospital.report.client;

import com.hospital.report.dto.ApiResponse;
import com.hospital.report.dto.InvoiceDTO;
import com.hospital.report.dto.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "BILLING-SERVICE", path = "/billing")
public interface BillingServiceClient {

    @GetMapping("/invoices/patient/{pinNumber}")
    ApiResponse<List<InvoiceDTO>> getPatientInvoices(@PathVariable("pinNumber") String pinNumber);

    @GetMapping("/invoices/{invoiceNumber}")
    ApiResponse<InvoiceDTO> getInvoice(@PathVariable("invoiceNumber") String invoiceNumber);

    @GetMapping("/invoices/pending")
    ApiResponse<List<InvoiceDTO>> getPendingInvoices();

    @GetMapping("/invoices/doctor/{doctorId}/date/{date}")
    ApiResponse<List<InvoiceDTO>> getInvoicesByDoctorAndDate(
            @PathVariable("doctorId") String doctorId,
            @PathVariable("date") String date);

    @GetMapping("/payments/invoice/{invoiceNumber}")
    ApiResponse<List<PaymentDTO>> getInvoicePayments(@PathVariable("invoiceNumber") String invoiceNumber);

    /**
     * Direct payment collection by date range — avoids going through the
     * appointments chain.
     * Maps to GET
     * /billing/payments/collection?fromDate=...&toDate=...[&doctorId=...]
     */
    @GetMapping("/payments/collection")
    ApiResponse<List<PaymentDTO>> getPaymentsByDateRange(
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate,
            @RequestParam(value = "doctorId", required = false) String doctorId);
}
