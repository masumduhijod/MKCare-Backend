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
@RequestMapping("/reports/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Reports", description = "Doctor Consultation and Schedule Reports")
public class DoctorReportController {

    private final ReportService reportService;

    /**
     * REPORT 16: Doctor Consultation Report
     */
    @GetMapping("/{doctorId}/consultations")
    @Operation(summary = "Doctor Consultation Report",
            description = "Get all consultations for a specific doctor for a date/range. Includes appointments, revenue, follow-ups.")
    public ResponseEntity<ReportResponse<DoctorConsultationReportDTO>> getDoctorConsultationReport(
            @Parameter(description = "Doctor ID", required = true) @PathVariable String doctorId,
            @Parameter(description = "From date (yyyy-MM-dd)", required = true) @RequestParam String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)") @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(reportService.getDoctorConsultationReport(doctorId, fromDate, toDate));
    }

    /**
     * REPORT 17: Doctor Schedule Report
     */
    @GetMapping("/schedule")
    @Operation(summary = "Doctor Schedule Report",
            description = "Get doctor schedule. Pass doctorId for specific doctor, or leave blank for all doctors. Optional date range.")
    public ResponseEntity<ReportResponse<Map<String, Object>>> getDoctorScheduleReport(
            @Parameter(description = "Doctor ID (optional - blank returns all doctors)") @RequestParam(required = false) String doctorId,
            @Parameter(description = "From date (yyyy-MM-dd)") @RequestParam(required = false) String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)") @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(reportService.getDoctorScheduleReport(doctorId, fromDate, toDate));
    }
}
