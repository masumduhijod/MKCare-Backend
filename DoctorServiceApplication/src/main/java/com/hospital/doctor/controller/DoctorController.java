/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.controller;

/**
 *
 * @author mduhijod
 */

import com.hospital.doctor.dto.*;
import com.hospital.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Register new doctor
     * POST /doctors/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DoctorDTO>> registerDoctor(
            @Valid @RequestBody DoctorRegistrationDTO registrationDTO) {
        
        log.info("API: Register doctor - {}", registrationDTO.getFirstName());
        DoctorDTO doctor = doctorService.registerDoctor(registrationDTO);
        
        ApiResponse<DoctorDTO> response = ApiResponse.success(
            "Doctor registered successfully: " + doctor.getDoctorId(),
            doctor
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get doctor by ID
     * GET /doctors/{doctorId}
     */
    @GetMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorDTO>> getDoctorById(
            @PathVariable String doctorId) {
        
        log.info("API: Get doctor - {}", doctorId);
        DoctorDTO doctor = doctorService.getDoctorById(doctorId);
        
        ApiResponse<DoctorDTO> response = ApiResponse.success(
            "Doctor found",
            doctor
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update doctor details
     * PUT /doctors/{doctorId}
     */
    @PutMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorDTO>> updateDoctor(
            @PathVariable String doctorId,
            @Valid @RequestBody DoctorRegistrationDTO registrationDTO) {
        
        log.info("API: Update doctor - {}", doctorId);
        DoctorDTO doctor = doctorService.updateDoctor(doctorId, registrationDTO);
        
        ApiResponse<DoctorDTO> response = ApiResponse.success(
            "Doctor updated successfully",
            doctor
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all available doctors
     * GET /doctors/available
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<DoctorSummaryDTO>>> getAvailableDoctors() {
        log.info("API: Get all available doctors");
        List<DoctorSummaryDTO> doctors = doctorService.getAllAvailableDoctors();
        
        String message = doctors.size() + " available doctor(s) found";
        ApiResponse<List<DoctorSummaryDTO>> response = ApiResponse.success(message, doctors);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get available doctors by specialization
     * GET /doctors/available/specialization/{specialization}
     */
    @GetMapping("/available/specialization/{specialization}")
    public ResponseEntity<ApiResponse<List<DoctorSummaryDTO>>> getAvailableDoctorsBySpecialization(
            @PathVariable String specialization) {
        
        log.info("API: Get available doctors by specialization - {}", specialization);
        List<DoctorSummaryDTO> doctors = doctorService.getAvailableDoctorsBySpecialization(specialization);
        
        String message = doctors.size() + " doctor(s) found";
        ApiResponse<List<DoctorSummaryDTO>> response = ApiResponse.success(message, doctors);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get available doctors by department
     * GET /doctors/available/department/{department}
     */
    @GetMapping("/available/department/{department}")
    public ResponseEntity<ApiResponse<List<DoctorSummaryDTO>>> getAvailableDoctorsByDepartment(
            @PathVariable String department) {
        
        log.info("API: Get available doctors by department - {}", department);
        List<DoctorSummaryDTO> doctors = doctorService.getAvailableDoctorsByDepartment(department);
        
        String message = doctors.size() + " doctor(s) found";
        ApiResponse<List<DoctorSummaryDTO>> response = ApiResponse.success(message, doctors);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all doctors by specialization
     * GET /doctors/specialization/{specialization}
     */
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getDoctorsBySpecialization(
            @PathVariable String specialization) {
        
        log.info("API: Get doctors by specialization - {}", specialization);
        List<DoctorDTO> doctors = doctorService.getDoctorsBySpecialization(specialization);
        
        ApiResponse<List<DoctorDTO>> response = ApiResponse.success(
            doctors.size() + " doctor(s) found",
            doctors
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all doctors by department
     * GET /doctors/department/{department}
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getDoctorsByDepartment(
            @PathVariable String department) {
        
        log.info("API: Get doctors by department - {}", department);
        List<DoctorDTO> doctors = doctorService.getDoctorsByDepartment(department);
        
        ApiResponse<List<DoctorDTO>> response = ApiResponse.success(
            doctors.size() + " doctor(s) found",
            doctors
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search doctors by name
     * GET /doctors/search?name=value
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DoctorSearchDTO>>> searchDoctors(
            @RequestParam String name) {
        
        log.info("API: Search doctors - Name: {}", name);
        List<DoctorSearchDTO> doctors = doctorService.searchDoctorsByName(name);
        
        String message = doctors.isEmpty() 
            ? "No doctors found" 
            : doctors.size() + " doctor(s) found";
        
        ApiResponse<List<DoctorSearchDTO>> response = ApiResponse.success(message, doctors);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all active doctors
     * GET /doctors/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAllActiveDoctors() {
        log.info("API: Get all active doctors");
        List<DoctorDTO> doctors = doctorService.getAllActiveDoctors();
        
        ApiResponse<List<DoctorDTO>> response = ApiResponse.success(
            doctors.size() + " active doctor(s) found",
            doctors
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get emergency doctors
     * GET /doctors/emergency
     */
    @GetMapping("/emergency")
    public ResponseEntity<ApiResponse<List<DoctorSummaryDTO>>> getEmergencyDoctors() {
        log.info("API: Get emergency doctors");
        List<DoctorSummaryDTO> doctors = doctorService.getEmergencyDoctors();
        
        ApiResponse<List<DoctorSummaryDTO>> response = ApiResponse.success(
            doctors.size() + " emergency doctor(s) available",
            doctors
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update doctor status
     * PUT /doctors/{doctorId}/status
     */
    @PutMapping("/{doctorId}/status")
    public ResponseEntity<ApiResponse<DoctorDTO>> updateStatus(
            @PathVariable String doctorId,
            @RequestParam String status) {
        
        log.info("API: Update doctor status - {}: {}", doctorId, status);
        DoctorDTO doctor = doctorService.updateDoctorStatus(doctorId, status);
        
        ApiResponse<DoctorDTO> response = ApiResponse.success(
            "Doctor status updated",
            doctor
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark doctor as on leave
     * PUT /doctors/{doctorId}/on-leave
     */
    @PutMapping("/{doctorId}/on-leave")
    public ResponseEntity<ApiResponse<DoctorDTO>> markOnLeave(
            @PathVariable String doctorId) {
        
        log.info("API: Mark doctor on leave - {}", doctorId);
        DoctorDTO doctor = doctorService.markOnLeave(doctorId);
        
        ApiResponse<DoctorDTO> response = ApiResponse.success(
            "Doctor marked as on leave",
            doctor
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark doctor as available
     * PUT /doctors/{doctorId}/available
     */
    @PutMapping("/{doctorId}/available")
    public ResponseEntity<ApiResponse<DoctorDTO>> markAvailable(
            @PathVariable String doctorId) {
        
        log.info("API: Mark doctor as available - {}", doctorId);
        DoctorDTO doctor = doctorService.markAvailable(doctorId);
        
        ApiResponse<DoctorDTO> response = ApiResponse.success(
            "Doctor marked as available",
            doctor
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete doctor
     * DELETE /doctors/{doctorId}
     */
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<String>> deleteDoctor(
            @PathVariable String doctorId) {
        
        log.info("API: Delete doctor - {}", doctorId);
        String message = doctorService.deleteDoctor(doctorId);
        
        ApiResponse<String> response = ApiResponse.success(message, null);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get total active doctors count
     * GET /doctors/count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTotalDoctors() {
        long count = doctorService.getTotalActiveDoctors();
        
        ApiResponse<Long> response = ApiResponse.success(
            "Total active doctors: " + count,
            count
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if doctor exists
     * GET /doctors/exists/{doctorId}
     */
    @GetMapping("/exists/{doctorId}")
    public ResponseEntity<ApiResponse<Boolean>> checkDoctorExists(
            @PathVariable String doctorId) {
        
        boolean exists = doctorService.existsByDoctorId(doctorId);
        
        ApiResponse<Boolean> response = ApiResponse.success(
            exists ? "Doctor exists" : "Doctor not found",
            exists
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all specializations
     * GET /doctors/specializations
     */
    @GetMapping("/specializations")
    public ResponseEntity<ApiResponse<List<String>>> getAllSpecializations() {
        List<String> specializations = doctorService.getAllSpecializations();
        
        ApiResponse<List<String>> response = ApiResponse.success(
            specializations.size() + " specialization(s) found",
            specializations
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all departments
     * GET /doctors/departments
     */
    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<String>>> getAllDepartments() {
        List<String> departments = doctorService.getAllDepartments();
        
        ApiResponse<List<String>> response = ApiResponse.success(
            departments.size() + " department(s) found",
            departments
        );
        
        return ResponseEntity.ok(response);
    }
}