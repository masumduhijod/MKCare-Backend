package com.hospital.report.controller;

import com.hospital.report.dto.*;
import com.hospital.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Reports", description = "Patient Registration, Demographics and Visit History Reports")
public class PatientReportController {

    private final ReportService reportService;

    /**
     * REPORT 1: Patient Registration Report
     * Filter by date range and/or status
     */
    @GetMapping("/registration")
    @Operation(summary = "Patient Registration Report",
            description = "Get all registered patients with optional filters: fromDate, toDate (yyyy-MM-dd), status (ACTIVE/INACTIVE)")
    public ResponseEntity<ReportResponse<List<PatientDTO>>> getPatientRegistrationReport(
            @Parameter(description = "From date (yyyy-MM-dd)") @RequestParam(required = false) String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)") @RequestParam(required = false) String toDate,
            @Parameter(description = "Patient status: ACTIVE, INACTIVE") @RequestParam(required = false) String status) {
        return ResponseEntity.ok(reportService.getPatientRegistrationReport(fromDate, toDate, status));
    }

    /**
     * REPORT 2: Patient Demographics Report
     * Age group, gender, blood group, city, insurance distribution
     */
    @GetMapping("/demographics")
    @Operation(summary = "Patient Demographics Report",
            description = "Get demographics analysis: gender distribution, age groups, blood groups, city/state distribution, insurance coverage")
    public ResponseEntity<ReportResponse<java.util.Map<String, Object>>> getPatientDemographicsReport() {
        return ResponseEntity.ok(reportService.getPatientDemographicsReport());
    }

    /**
     * REPORT 3: Patient Visit History Report (PIN required)
     */
    @GetMapping("/visit-history/{pinNumber}")
    @Operation(summary = "Patient Visit History Report",
            description = "Get complete visit history for a patient by PIN number. Returns all CVR records with timestamps.")
    public ResponseEntity<ReportResponse<PatientVisitHistoryDTO>> getPatientVisitHistoryReport(
            @Parameter(description = "Patient PIN number", required = true) @PathVariable String pinNumber) {
        return ResponseEntity.ok(reportService.getPatientVisitHistoryReport(pinNumber));
    }

    /**
     * EXTRA: Patient Search Report
     */
    @GetMapping("/search")
    @Operation(summary = "Patient Search Report",
            description = "Search patients by NAME, CONTACT, PIN, EMAIL. Returns matching patients with summary.")
    public ResponseEntity<ReportResponse<java.util.Map<String, Object>>> getPatientSearchReport(
            @Parameter(description = "Search query", required = true) @RequestParam String query,
            @Parameter(description = "Search type: NAME, CONTACT, PIN, EMAIL") @RequestParam(defaultValue = "NAME") String searchType) {
        return ResponseEntity.ok(reportService.getPatientSearchReport(query, searchType));
    }
}
