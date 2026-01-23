/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.billing.controller;

import com.hospital.billing.dto.ApiResponse;
import com.hospital.billing.dto.PaymentDTO;
import com.hospital.billing.service.PaymentService;
import static java.lang.StrictMath.log;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mduhijod
 */
@RestController
@RequestMapping("/billing/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/process/{invoiceNumber}")
    public ResponseEntity<ApiResponse<PaymentDTO>> processPayment(
            @PathVariable String invoiceNumber,
            @Valid @RequestBody PaymentDTO dto) {
        log.info("API: Process payment for invoice {}", invoiceNumber);
        PaymentDTO payment = paymentService.processPayment(invoiceNumber, dto);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", payment));
    }
    
    @GetMapping("/invoice/{invoiceNumber}")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getInvoicePayments(@PathVariable String invoiceNumber) {
        log.info("API: Get payments for invoice {}", invoiceNumber);
        List<PaymentDTO> payments = paymentService.getInvoicePayments(invoiceNumber);
        return ResponseEntity.ok(ApiResponse.success(payments.size() + " payment(s) found", payments));
    }
}
