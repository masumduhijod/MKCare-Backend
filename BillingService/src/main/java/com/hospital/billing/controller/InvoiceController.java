package com.hospital.billing.controller;

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
public class InvoiceController {
    
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
    
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByDoctorAndDate(
            @PathVariable String doctorId,
            @PathVariable String date) {
        log.info("API: Get invoices for doctor {} on date {}", doctorId, date);
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByDoctorAndDate(doctorId, date);
        return ResponseEntity.ok(ApiResponse.success("Invoices loaded", invoices));
    }

    /**
     * Search invoices by CVR numbers (Batch lookup)
     * Using POST to handle large lists of CVRs
     */
    @PostMapping("/search/by-cvrs")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByCvrs(@RequestBody List<String> cvrNumbers) {
        log.info("API: Batch lookup invoices for {} CVRs", (cvrNumbers != null ? cvrNumbers.size() : 0));
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByCvrs(cvrNumbers);
        return ResponseEntity.ok(ApiResponse.success("Invoices fetched successfully", invoices));
    }

    @GetMapping("/by-pin/{pin}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getByPin(@PathVariable String pin) {
        List<InvoiceDTO> invoices = invoiceService.getPatientInvoices(pin);
        return ResponseEntity.ok(ApiResponse.success("Invoices for patient", invoices));
    }
}
