/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.controller;

/**
 *
 * @author mduhijod
 */

import com.hospital.cvr.dto.*;
import com.hospital.cvr.service.CvrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cvr")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class CvrController {

    private final CvrService cvrService;

    /**
     * Create new CVR (Case Visit Record)
     * POST /cvr/create
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CvrDTO>> createCVR(
            @Valid @RequestBody CreateCvrDTO createCvrDTO) {
        
        log.info("API: Create CVR for PIN: {}", createCvrDTO.getPinNumber());
        CvrDTO cvr = cvrService.createCVR(createCvrDTO);
        
        ApiResponse<CvrDTO> response = ApiResponse.success(
            "CVR created successfully: " + cvr.getCvrNumber(),
            cvr
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get CVR by CVR number
     * GET /cvr/{cvrNumber}
     */
    @GetMapping("/{cvrNumber}")
    public ResponseEntity<ApiResponse<CvrDTO>> getCVRByNumber(
            @PathVariable String cvrNumber) {
        
        log.info("API: Get CVR - {}", cvrNumber);
        CvrDTO cvr = cvrService.getCVRByNumber(cvrNumber);
        
        ApiResponse<CvrDTO> response = ApiResponse.success(
            "CVR found",
            cvr
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get patient visit history
     * GET /cvr/patient/{pinNumber}/history
     */
    @GetMapping("/patient/{pinNumber}/history")
    public ResponseEntity<ApiResponse<PatientVisitHistoryDTO>> getPatientHistory(
            @PathVariable String pinNumber) {
        
        log.info("API: Get visit history for PIN: {}", pinNumber);
        PatientVisitHistoryDTO history = cvrService.getPatientVisitHistory(pinNumber);
        
        ApiResponse<PatientVisitHistoryDTO> response = ApiResponse.success(
            "Visit history retrieved successfully",
            history
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get today's CVRs
     * GET /cvr/today
     */
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<CvrSummaryDTO>>> getTodaysCVRs() {
        log.info("API: Get today's CVRs");
        List<CvrSummaryDTO> cvrs = cvrService.getTodaysCVRs();
        
        String message = cvrs.isEmpty() 
            ? "No CVRs found for today" 
            : cvrs.size() + " CVR(s) found for today";
        
        ApiResponse<List<CvrSummaryDTO>> response = ApiResponse.success(message, cvrs);
        return ResponseEntity.ok(response);
    }

    /**
     * Get CVRs by date
     * GET /cvr/date/{date}
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<CvrSummaryDTO>>> getCVRsByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        log.info("API: Get CVRs for date: {}", date);
        List<CvrSummaryDTO> cvrs = cvrService.getCVRsByDate(date);
        
        String message = cvrs.size() + " CVR(s) found for " + date;
        ApiResponse<List<CvrSummaryDTO>> response = ApiResponse.success(message, cvrs);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get CVRs by doctor and date
     * GET /cvr/doctor/{doctorId}/date/{date}
     */
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<ApiResponse<List<CvrSummaryDTO>>> getCVRsByDoctorAndDate(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        log.info("API: Get CVRs for doctor: {} on date: {}", doctorId, date);
        List<CvrSummaryDTO> cvrs = cvrService.getCVRsByDoctorAndDate(doctorId, date);
        
        String message = cvrs.size() + " CVR(s) found";
        ApiResponse<List<CvrSummaryDTO>> response = ApiResponse.success(message, cvrs);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check-in patient
     * PUT /cvr/{cvrNumber}/checkin
     */
    @PutMapping("/{cvrNumber}/checkin")
    public ResponseEntity<ApiResponse<CvrDTO>> checkInPatient(
            @PathVariable String cvrNumber) {
        
        log.info("API: Check-in patient - CVR: {}", cvrNumber);
        CvrDTO cvr = cvrService.checkInPatient(cvrNumber);
        
        ApiResponse<CvrDTO> response = ApiResponse.success(
            "Patient checked in successfully",
            cvr
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Start consultation
     * PUT /cvr/{cvrNumber}/start-consultation
     */
    @PutMapping("/{cvrNumber}/start-consultation")
    public ResponseEntity<ApiResponse<CvrDTO>> startConsultation(
            @PathVariable String cvrNumber) {
        
        log.info("API: Start consultation - CVR: {}", cvrNumber);
        CvrDTO cvr = cvrService.startConsultation(cvrNumber);
        
        ApiResponse<CvrDTO> response = ApiResponse.success(
            "Consultation started",
            cvr
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Complete consultation
     * PUT /cvr/{cvrNumber}/complete-consultation
     */
    @PutMapping("/{cvrNumber}/complete-consultation")
    public ResponseEntity<ApiResponse<CvrDTO>> completeConsultation(
            @PathVariable String cvrNumber) {
        
        log.info("API: Complete consultation - CVR: {}", cvrNumber);
        CvrDTO cvr = cvrService.completeConsultation(cvrNumber);
        
        ApiResponse<CvrDTO> response = ApiResponse.success(
            "Consultation completed",
            cvr
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel CVR
     * DELETE /cvr/{cvrNumber}
     */
    @DeleteMapping("/{cvrNumber}")
    public ResponseEntity<ApiResponse<String>> cancelCVR(
            @PathVariable String cvrNumber,
            @RequestParam(required = false) String reason) {
        
        log.info("API: Cancel CVR - {}", cvrNumber);
        String message = cvrService.cancelCVR(cvrNumber, reason);
        
        ApiResponse<String> response = ApiResponse.success(message, null);
        return ResponseEntity.ok(response);
    }

    /**
     * Record vitals
     * POST /cvr/vitals/record
     */
    @PostMapping("/vitals/record")
    public ResponseEntity<ApiResponse<CvrVitalsDTO>> recordVitals(
            @Valid @RequestBody RecordVitalsDTO recordVitalsDTO) {
        
        log.info("API: Record vitals for CVR: {}", recordVitalsDTO.getCvrNumber());
        CvrVitalsDTO vitals = cvrService.recordVitals(recordVitalsDTO);
        
        ApiResponse<CvrVitalsDTO> response = ApiResponse.success(
            "Vitals recorded successfully",
            vitals
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get vitals for CVR
     * GET /cvr/{cvrNumber}/vitals
     */
    @GetMapping("/{cvrNumber}/vitals")
    public ResponseEntity<ApiResponse<List<CvrVitalsDTO>>> getVitals(
            @PathVariable String cvrNumber) {
        
        log.info("API: Get vitals for CVR: {}", cvrNumber);
        List<CvrVitalsDTO> vitals = cvrService.getVitalsByCVR(cvrNumber);
        
        String message = vitals.isEmpty() 
            ? "No vitals recorded" 
            : vitals.size() + " vital record(s) found";
        
        ApiResponse<List<CvrVitalsDTO>> response = ApiResponse.success(message, vitals);
        return ResponseEntity.ok(response);
    }

    /**
     * Search CVRs
     * GET /cvr/search?query=value
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CvrSummaryDTO>>> searchCVRs(
            @RequestParam String query) {
        
        log.info("API: Search CVRs - Query: {}", query);
        List<CvrSummaryDTO> cvrs = cvrService.searchCVRs(query);
        
        String message = cvrs.isEmpty() 
            ? "No CVRs found" 
            : cvrs.size() + " CVR(s) found";
        
        ApiResponse<List<CvrSummaryDTO>> response = ApiResponse.success(message, cvrs);
        return ResponseEntity.ok(response);
    }

    /**
     * Get recent CVRs
     * GET /cvr/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<CvrSummaryDTO>>> getRecentCVRs(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("API: Get recent CVRs - Limit: {}", limit);
        List<CvrSummaryDTO> cvrs = cvrService.getRecentCVRs(limit);
        
        ApiResponse<List<CvrSummaryDTO>> response = ApiResponse.success(
            cvrs.size() + " recent CVR(s) found",
            cvrs
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Assign doctor to CVR
     * PUT /cvr/{cvrNumber}/assign-doctor
     */
    @PutMapping("/{cvrNumber}/assign-doctor")
    public ResponseEntity<ApiResponse<CvrDTO>> assignDoctor(
            @PathVariable String cvrNumber,
            @RequestParam String doctorId) {
        
        log.info("API: Assign doctor {} to CVR: {}", doctorId, cvrNumber);
        CvrDTO cvr = cvrService.assignDoctor(cvrNumber, doctorId);
        
        ApiResponse<CvrDTO> response = ApiResponse.success(
            "Doctor assigned successfully",
            cvr
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update CVR status
     * PUT /cvr/{cvrNumber}/status
     */
    @PutMapping("/{cvrNumber}/status")
    public ResponseEntity<ApiResponse<CvrDTO>> updateStatus(
            @PathVariable String cvrNumber,
            @RequestParam String status) {
        
        log.info("API: Update CVR status - CVR: {}, Status: {}", cvrNumber, status);
        CvrDTO cvr = cvrService.updateCVRStatus(cvrNumber, status);
        
        ApiResponse<CvrDTO> response = ApiResponse.success(
            "CVR status updated",
            cvr
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Count patient visits
     * GET /cvr/patient/{pinNumber}/count
     */
    @GetMapping("/patient/{pinNumber}/count")
    public ResponseEntity<ApiResponse<Long>> countPatientVisits(
            @PathVariable String pinNumber) {
        
        long count = cvrService.countPatientVisits(pinNumber);
        
        ApiResponse<Long> response = ApiResponse.success(
            "Total visits: " + count,
            count
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if CVR exists
     * GET /cvr/exists/{cvrNumber}
     */
    @GetMapping("/exists/{cvrNumber}")
    public ResponseEntity<ApiResponse<Boolean>> checkCVRExists(
            @PathVariable String cvrNumber) {
        
        boolean exists = cvrService.existsByCVRNumber(cvrNumber);
        
        ApiResponse<Boolean> response = ApiResponse.success(
            exists ? "CVR exists" : "CVR not found",
            exists
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get CVR by appointment ID GET /cvr/by-appointment/{appointmentId}
     */
    @GetMapping("/by-appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<CvrDTO>> getCVRByAppointment(
            @PathVariable String appointmentId) {

        log.info("API: Get CVR by appointmentId: {}", appointmentId);

        CvrDTO cvr = cvrService.getCVRByAppointmentId(appointmentId);

        ApiResponse<CvrDTO> response = ApiResponse.success(
                "CVR found for appointment",
                cvr
        );

        return ResponseEntity.ok(response);
    }
    /**
 * Delete vitals for CVR
 * DELETE /cvr/{cvrNumber}/vitals
 */
@DeleteMapping("/{cvrNumber}/vitals")
public ResponseEntity<ApiResponse<String>> deleteVitals(
        @PathVariable String cvrNumber) {

    log.info("API: Delete vitals for CVR: {}", cvrNumber);

    String message = cvrService.deleteVitalsByCVR(cvrNumber);

    ApiResponse<String> response = ApiResponse.success(
            message,
            null
    );

    return ResponseEntity.ok(response);
}
}
