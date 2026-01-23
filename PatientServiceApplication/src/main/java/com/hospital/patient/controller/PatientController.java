/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.controller;

/**
 *
 * @author mduhijod
 */

import com.hospital.patient.dto.*;
import com.hospital.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    /**
     * Register new patient
     * POST /patients/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PatientDTO>> registerPatient(
            @Valid @RequestBody PatientRegistrationDTO registrationDTO) {
        
        log.info("API: Register patient - {}", registrationDTO.getFirstName());
        log.info("Incoming DTO Gender: {}", registrationDTO.getGender());
        PatientDTO patient = patientService.registerPatient(registrationDTO);
        
        ApiResponse<PatientDTO> response = ApiResponse.success(
            "Patient registered successfully with PIN: " + patient.getPinNumber(),
            patient
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get patient by PIN
     * GET /patients/pin/{pinNumber}
     */
    @GetMapping("/pin/{pinNumber}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientByPIN(
            @PathVariable String pinNumber) {
        
        log.info("API: Get patient by PIN - {}", pinNumber);
        PatientDTO patient = patientService.getPatientByPIN(pinNumber);
        
        ApiResponse<PatientDTO> response = ApiResponse.success(
            "Patient found",
            patient
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get patient by ID
     * GET /patients/{patientId}
     */
    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientById(
            @PathVariable Long patientId) {
        
        log.info("API: Get patient by ID - {}", patientId);
        PatientDTO patient = patientService.getPatientById(patientId);
        
        ApiResponse<PatientDTO> response = ApiResponse.success(
            "Patient found",
            patient
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search patients
     * GET /patients/search?query=value&type=PIN|CONTACT|EMAIL|NAME
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PatientSearchDTO>>> searchPatients(
            @RequestParam String query,
            @RequestParam(defaultValue = "NAME") String type) {
        
        log.info("API: Search patients - Query: {}, Type: {}", query, type);
        List<PatientSearchDTO> patients = patientService.searchPatients(query, type);
        
        String message = patients.isEmpty() 
            ? "No patients found" 
            : patients.size() + " patient(s) found";
        
        ApiResponse<List<PatientSearchDTO>> response = ApiResponse.success(
            message,
            patients
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search by contact number
     * GET /patients/contact/{contactNumber}
     */
    @GetMapping("/contact/{contactNumber}")
    public ResponseEntity<ApiResponse<PatientDTO>> searchByContact(
            @PathVariable String contactNumber) {
        
        log.info("API: Search by contact - {}", contactNumber);
        PatientDTO patient = patientService.searchByContactNumber(contactNumber);
        
        ApiResponse<PatientDTO> response = ApiResponse.success(
            "Patient found",
            patient
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update patient details
     * PUT /patients/{pinNumber}
     */
    @PutMapping("/{pinNumber}")
    public ResponseEntity<ApiResponse<PatientDTO>> updatePatient(
            @PathVariable String pinNumber,
            @Valid @RequestBody PatientRegistrationDTO registrationDTO) {
        
        log.info("API: Update patient - {}", pinNumber);
        PatientDTO patient = patientService.updatePatient(pinNumber, registrationDTO);
        
        ApiResponse<PatientDTO> response = ApiResponse.success(
            "Patient updated successfully",
            patient
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all active patients
     * GET /patients/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getAllActivePatients() {
        log.info("API: Get all active patients");
        List<PatientDTO> patients = patientService.getAllActivePatients();
        
        ApiResponse<List<PatientDTO>> response = ApiResponse.success(
            patients.size() + " active patient(s) found",
            patients
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get recent patients
     * GET /patients/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getRecentPatients(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("API: Get recent patients - Limit: {}", limit);
        List<PatientDTO> patients = patientService.getRecentPatients(limit);
        
        ApiResponse<List<PatientDTO>> response = ApiResponse.success(
            patients.size() + " recent patient(s) found",
            patients
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get medical history
     * GET /patients/{pinNumber}/medical-history
     */
    @GetMapping("/{pinNumber}/medical-history")
    public ResponseEntity<ApiResponse<MedicalHistoryDTO>> getMedicalHistory(
            @PathVariable String pinNumber) {
        
        log.info("API: Get medical history - {}", pinNumber);
        MedicalHistoryDTO history = patientService.getMedicalHistory(pinNumber);
        
        ApiResponse<MedicalHistoryDTO> response = ApiResponse.success(
            "Medical history retrieved",
            history
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update medical history
     * PUT /patients/{pinNumber}/medical-history
     */
    @PutMapping("/{pinNumber}/medical-history")
    public ResponseEntity<ApiResponse<MedicalHistoryDTO>> updateMedicalHistory(
            @PathVariable String pinNumber,
            @RequestBody MedicalHistoryDTO historyDTO) {
        
        log.info("API: Update medical history - {}", pinNumber);
        MedicalHistoryDTO history = patientService.updateMedicalHistory(pinNumber, historyDTO);
        
        ApiResponse<MedicalHistoryDTO> response = ApiResponse.success(
            "Medical history updated successfully",
            history
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if PIN exists
     * GET /patients/exists/pin/{pinNumber}
     */
    @GetMapping("/exists/pin/{pinNumber}")
    public ResponseEntity<ApiResponse<Boolean>> checkPINExists(
            @PathVariable String pinNumber) {
        
        boolean exists = patientService.existsByPIN(pinNumber);
        
        ApiResponse<Boolean> response = ApiResponse.success(
            exists ? "PIN exists" : "PIN not found",
            exists
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if contact exists
     * GET /patients/exists/contact/{contactNumber}
     */
    @GetMapping("/exists/contact/{contactNumber}")
    public ResponseEntity<ApiResponse<Boolean>> checkContactExists(
            @PathVariable String contactNumber) {
        
        boolean exists = patientService.existsByContactNumber(contactNumber);
        
        ApiResponse<Boolean> response = ApiResponse.success(
            exists ? "Contact number already registered" : "Contact number available",
            exists
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get total active patients count
     * GET /patients/count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTotalPatients() {
        long count = patientService.getTotalActivePatients();
        
        ApiResponse<Long> response = ApiResponse.success(
            "Total active patients: " + count,
            count
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete patient (soft delete)
     * DELETE /patients/{pinNumber}
     */
    @DeleteMapping("/{pinNumber}")
    public ResponseEntity<ApiResponse<String>> deletePatient(
            @PathVariable String pinNumber) {
        
        log.info("API: Delete patient - {}", pinNumber);
        String message = patientService.deletePatient(pinNumber);
        
        ApiResponse<String> response = ApiResponse.success(message, null);
        
        return ResponseEntity.ok(response);
    }
}
