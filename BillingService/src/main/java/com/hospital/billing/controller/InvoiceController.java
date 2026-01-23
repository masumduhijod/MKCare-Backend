/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.billing.controller;

/**
 *
 * @author mduhijod
 */
// ========== Controllers ==========

import com.hospital.billing.dto.*;
import com.hospital.billing.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/billing/invoices")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
class InvoiceController {
    
    private final InvoiceService invoiceService;
    
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<InvoiceDTO>> createInvoice(@Valid @RequestBody CreateInvoiceDTO dto) {
        log.info("API: Create invoice for PIN {}", dto.getPinNumber());
        InvoiceDTO invoice = invoiceService.createInvoice(dto);
        return new ResponseEntity<>(ApiResponse.success("Invoice created: " + invoice.getInvoiceNumber(), invoice), HttpStatus.CREATED);
    }
    
    @GetMapping("/{invoiceNumber}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoice(@PathVariable String invoiceNumber) {
        log.info("API: Get invoice {}", invoiceNumber);
        InvoiceDTO invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(ApiResponse.success("Invoice found", invoice));
    }
    
    @GetMapping("/patient/{pinNumber}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getPatientInvoices(@PathVariable String pinNumber) {
        log.info("API: Get invoices for patient {}", pinNumber);
        List<InvoiceDTO> invoices = invoiceService.getPatientInvoices(pinNumber);
        return ResponseEntity.ok(ApiResponse.success(invoices.size() + " invoice(s) found", invoices));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getPendingInvoices() {
        log.info("API: Get pending invoices");
        List<InvoiceDTO> invoices = invoiceService.getPendingInvoices();
        return ResponseEntity.ok(ApiResponse.success(invoices.size() + " pending invoice(s)", invoices));
    }
}

