/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.controller;

import com.hospital.opd.dto.ApiResponse;
import com.hospital.opd.dto.CreatePrescriptionDTO;
import com.hospital.opd.dto.PrescriptionDTO;
import com.hospital.opd.service.PrescriptionService;
import static java.lang.StrictMath.log;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mduhijod
 */
// ========== PrescriptionController ==========
@RestController
@RequestMapping("/opd/prescriptions")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PrescriptionDTO>> createPrescription(
            @Valid @RequestBody CreatePrescriptionDTO dto) {
        log.info("API: Create prescription for PIN {}", dto.getPinNumber());
        PrescriptionDTO prescription = prescriptionService.createPrescription(dto);
        return new ResponseEntity<>(
                ApiResponse.success("Prescription created: " + prescription.getPrescriptionId(), prescription),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{prescriptionId}")
    public ResponseEntity<ApiResponse<PrescriptionDTO>> getPrescription(@PathVariable String prescriptionId) {
        log.info("API: Get prescription {}", prescriptionId);
        PrescriptionDTO prescription = prescriptionService.getPrescriptionById(prescriptionId);
        return ResponseEntity.ok(ApiResponse.success("Prescription found", prescription));
    }

    @GetMapping("/patient/{pinNumber}")
    public ResponseEntity<ApiResponse<List<PrescriptionDTO>>> getPatientPrescriptions(@PathVariable String pinNumber) {
        log.info("API: Get prescriptions for patient {}", pinNumber);
        List<PrescriptionDTO> prescriptions = prescriptionService.getPatientPrescriptions(pinNumber);
        return ResponseEntity.ok(ApiResponse.success(prescriptions.size() + " prescription(s) found", prescriptions));
    }

    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<ApiResponse<PrescriptionDTO>> getByConsultationId(
            @PathVariable String consultationId) {

        log.info("API: Get prescription by consultation {}", consultationId);

        PrescriptionDTO prescription
                = prescriptionService.getByConsultationId(consultationId);

        return ResponseEntity.ok(
                ApiResponse.success("Prescription found", prescription)
        );
    }

    @DeleteMapping("/{prescriptionId}")
    public ResponseEntity<ApiResponse<String>> deletePrescription(
            @PathVariable String prescriptionId) {

        log.info("API: Delete prescription {}", prescriptionId);

        prescriptionService.deletePrescription(prescriptionId);

        return ResponseEntity.ok(
                ApiResponse.success("Prescription deleted successfully", prescriptionId)
        );
    }

    @PutMapping("/update/{prescriptionId}")
    public ResponseEntity<ApiResponse<PrescriptionDTO>> updatePrescription(
            @PathVariable String prescriptionId,
            @Valid @RequestBody CreatePrescriptionDTO dto) {

        log.info("API: Update prescription {}", prescriptionId);

        PrescriptionDTO updated
                = prescriptionService.updatePrescription(prescriptionId, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Prescription updated successfully", updated)
        );
    }

}
