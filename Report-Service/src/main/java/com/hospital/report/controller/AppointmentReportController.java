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
import java.util.Map;

@RestController
@RequestMapping("/reports/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Reports", description = "Appointment Schedule, Status and Availability Reports")
public class AppointmentReportController {

    private final ReportService reportService;

    /**
     * REPORT 7: Appointment Schedule Report
     */
    @GetMapping("/schedule")
    @Operation(summary = "Appointment Schedule Report",
            description = "Get appointment schedule for date range. Optional filters: doctorId, status (SCHEDULED/COMPLETED/CANCELLED/NO_SHOW).")
    public ResponseEntity<ReportResponse<List<AppointmentSummaryDTO>>> getAppointmentScheduleReport(
            @Parameter(description = "From date (yyyy-MM-dd)", required = true) @RequestParam String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)", required = true) @RequestParam String toDate,
            @Parameter(description = "Doctor ID (optional)") @RequestParam(required = false) String doctorId,
            @Parameter(description = "Appointment status filter") @RequestParam(required = false) String status) {
        return ResponseEntity.ok(reportService.getAppointmentScheduleReport(fromDate, toDate, doctorId, status));
    }

    /**
     * REPORT 8: Appointment Status Report
     */
    @GetMapping("/status-summary")
    @Operation(summary = "Appointment Status Report",
            description = "Get appointment counts grouped by status (SCHEDULED, COMPLETED, CANCELLED, NO_SHOW, IN_CONSULTATION) for a date range.")
    public ResponseEntity<ReportResponse<Map<String, Object>>> getAppointmentStatusReport(
            @Parameter(description = "From date (yyyy-MM-dd)", required = true) @RequestParam String fromDate,
            @Parameter(description = "To date (yyyy-MM-dd)", required = true) @RequestParam String toDate) {
        return ResponseEntity.ok(reportService.getAppointmentStatusReport(fromDate, toDate));
    }

    /**
     * REPORT 9: Doctor Availability Report
     */
    @GetMapping("/doctor-availability")
    @Operation(summary = "Doctor Availability Report",
            description = "Get availability status of all active doctors. Pass date to see scheduled slots for that day.")
    public ResponseEntity<ReportResponse<Map<String, Object>>> getDoctorAvailabilityReport(
            @Parameter(description = "Date (yyyy-MM-dd) to check schedule. Optional.") @RequestParam(required = false) String date) {
        return ResponseEntity.ok(reportService.getDoctorAvailabilityReport(date));
    }
}
