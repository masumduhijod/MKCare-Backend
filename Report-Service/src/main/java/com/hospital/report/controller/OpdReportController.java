package com.hospital.report.controller;

import com.hospital.report.dto.*;
import com.hospital.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/reports/opd")
@RequiredArgsConstructor
@Tag(name = "OPD Reports", description = "OPD Daily, Department-wise and Revenue Reports")
public class OpdReportController {

    private final ReportService reportService;

    /**
     * REPORT 4: OPD Daily Report
     */
    @GetMapping("/daily")
    @Operation(summary = "OPD Daily Report",
            description = "Get OPD summary for a specific date. Includes total visits, department-wise count, CVR status breakdown. Default: today.")
    public ResponseEntity<ReportResponse<OpdDailyReportDTO>> getOpdDailyReport(
            @Parameter(description = "Date (yyyy-MM-dd). Default: today") @RequestParam(required = false) String date) {
        if (date == null) date = LocalDate.now().toString();
        return ResponseEntity.ok(reportService.getOpdDailyReport(date));
    }

    /**
     * REPORT 5: OPD Department-wise Report
     */
    @GetMapping("/department-wise")
    @Operation(summary = "OPD Department-wise Report",
            description = "Get department-wise patient count and appointment stats for date range.")
    public ResponseEntity<ReportResponse<Map<String, Object>>> getOpdDepartmentWiseReport(
            @Parameter(description = "From date (yyyy-MM-dd)", required = true) @RequestParam String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)", required = true) @RequestParam String toDate) {
        return ResponseEntity.ok(reportService.getOpdDepartmentWiseReport(fromDate, toDate));
    }

    /**
     * REPORT 6: OPD Revenue Report
     */
    @GetMapping("/revenue")
    @Operation(summary = "OPD Revenue Report",
            description = "Get revenue summary for OPD for a date range. Includes total billed, collected, outstanding, payment-mode wise collection.")
    public ResponseEntity<ReportResponse<RevenueReportDTO>> getOpdRevenueReport(
            @Parameter(description = "From date (yyyy-MM-dd)", required = true) @RequestParam String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)", required = true) @RequestParam String toDate) {
        return ResponseEntity.ok(reportService.getOpdRevenueReport(fromDate, toDate));
    }

    /**
     * REPORT 10: CVR Summary Report
     */
    @GetMapping("/cvr-summary")
    @Operation(summary = "CVR Summary Report",
            description = "CVR report by: cvrNumber (single), pinNumber (patient history), doctorId+date, or date. Pass any one filter.")
    public ResponseEntity<ReportResponse<Map<String, Object>>> getCvrSummaryReport(
            @Parameter(description = "Date (yyyy-MM-dd)") @RequestParam(required = false) String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)") @RequestParam(required = false) String toDate,
            @Parameter(description = "CVR Number for single CVR lookup") @RequestParam(required = false) String cvrNumber,
            @Parameter(description = "Patient PIN Number") @RequestParam(required = false) String pinNumber,
            @Parameter(description = "Doctor ID") @RequestParam(required = false) String doctorId) {
        return ResponseEntity.ok(reportService.getCvrSummaryReport(fromDate, toDate, cvrNumber, pinNumber, doctorId));
    }

    /**
     * REPORT 11: Prescription Report
     */
    @GetMapping("/prescriptions")
    @Operation(summary = "Prescription Report",
            description = "Get prescriptions by: prescriptionId (single), pinNumber (patient), or doctorId+date.")
    public ResponseEntity<ReportResponse<Map<String, Object>>> getPrescriptionReport(
            @Parameter(description = "Patient PIN Number") @RequestParam(required = false) String pinNumber,
            @Parameter(description = "Prescription ID for single lookup") @RequestParam(required = false) String prescriptionId,
            @Parameter(description = "Doctor ID") @RequestParam(required = false) String doctorId,
            @Parameter(description = "Date (yyyy-MM-dd)") @RequestParam(required = false) String date) {
        return ResponseEntity.ok(reportService.getPrescriptionReport(pinNumber, prescriptionId, doctorId, date));
    }
}
