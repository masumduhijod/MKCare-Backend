package com.hospital.report.controller;

import com.hospital.report.dto.*;
import com.hospital.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reports/billing")
@RequiredArgsConstructor
@Tag(name = "Billing Reports", description = "Invoice, Payment Collection, Outstanding Dues and Revenue Analysis Reports")
public class BillingReportController {

    private final ReportService reportService;

    /**
     * REPORT 12: Invoice Summary Report
     */
    @GetMapping("/invoices")
    @Operation(summary = "Invoice Summary Report",
            description = "Get invoice details by: invoiceNumber (single), pinNumber (patient all invoices), or date range. Pass any one.")
    public ResponseEntity<ReportResponse<Map<String, Object>>> getInvoiceSummaryReport(
            @Parameter(description = "Patient PIN Number") @RequestParam(required = false) String pinNumber,
            @Parameter(description = "Invoice Number for single lookup") @RequestParam(required = false) String invoiceNumber,
            @Parameter(description = "From date (yyyy-MM-dd)") @RequestParam(required = false) String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)") @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(reportService.getInvoiceSummaryReport(pinNumber, invoiceNumber, fromDate, toDate));
    }

    /**
     * REPORT 13: Payment Collection Report
     */
    @GetMapping("/payment-collection")
    @Operation(summary = "Payment Collection Report",
            description = "Get payment collection summary for a date range. Includes payment-mode wise breakdown (CASH/CARD/UPI etc.).")
    public ResponseEntity<ReportResponse<Map<String, Object>>> getPaymentCollectionReport(
            @Parameter(description = "From date (yyyy-MM-dd)", required = true) @RequestParam String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)", required = true) @RequestParam String toDate,
            @Parameter(description = "Doctor ID to filter by specific doctor") @RequestParam(required = false) String doctorId) {
        return ResponseEntity.ok(reportService.getPaymentCollectionReport(fromDate, toDate, doctorId));
    }

    /**
     * REPORT 14: Outstanding Dues Report
     */
    @GetMapping("/outstanding-dues")
    @Operation(summary = "Outstanding Dues Report",
            description = "Get all pending/partially paid invoices with outstanding amounts. No filters required.")
    public ResponseEntity<ReportResponse<Map<String, Object>>> getOutstandingDuesReport() {
        return ResponseEntity.ok(reportService.getOutstandingDuesReport());
    }

    /**
     * REPORT 15: Revenue Analysis Report
     */
    @GetMapping("/revenue-analysis")
    @Operation(summary = "Revenue Analysis Report",
            description = "Complete revenue analysis for date range: total billed, collected, outstanding, invoice type wise and payment mode wise breakdown.")
    public ResponseEntity<ReportResponse<RevenueReportDTO>> getRevenueAnalysisReport(
            @Parameter(description = "From date (yyyy-MM-dd)", required = true) @RequestParam String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)", required = true) @RequestParam String toDate) {
        return ResponseEntity.ok(reportService.getRevenueAnalysisReport(fromDate, toDate));
    }
}
