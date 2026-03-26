/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.controller;

import com.hospital.opd.dto.ApiResponse;
import com.hospital.opd.dto.ConsultationDTO;
import com.hospital.opd.dto.CreateConsultationDTO;
import com.hospital.opd.entity.Consultation;
import com.hospital.opd.service.ConsultationService;
import static java.lang.StrictMath.log;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
// ========== ConsultationController ==========
@RestController
@RequestMapping("/opd/consultations")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ConsultationDTO>> createConsultation(
            @Valid @RequestBody CreateConsultationDTO dto) {
        log.info("API: Create consultation for CVR {}", dto.getCvrNumber());
        ConsultationDTO consultation = consultationService.createConsultation(dto);
        return new ResponseEntity<>(
                ApiResponse.success("Consultation created: " + consultation.getConsultationId(), consultation),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{consultationId}")
    public ResponseEntity<ApiResponse<ConsultationDTO>> getConsultation(@PathVariable String consultationId) {
        log.info("API: Get consultation {}", consultationId);
        ConsultationDTO consultation = consultationService.getConsultationById(consultationId);
        return ResponseEntity.ok(ApiResponse.success("Consultation found", consultation));
    }

    @PutMapping("/{consultationId}/complete")
    public ResponseEntity<ApiResponse<ConsultationDTO>> completeConsultation(@PathVariable String consultationId) {
        log.info("API: Complete consultation {}", consultationId);
        ConsultationDTO consultation = consultationService.completeConsultation(consultationId);
        return ResponseEntity.ok(ApiResponse.success("Consultation completed", consultation));
    }

    @GetMapping("/patient/{pinNumber}")
    public ResponseEntity<ApiResponse<List<ConsultationDTO>>> getPatientConsultations(@PathVariable String pinNumber) {
        log.info("API: Get consultations for patient {}", pinNumber);
        List<ConsultationDTO> consultations = consultationService.getPatientConsultations(pinNumber);
        return ResponseEntity.ok(ApiResponse.success(consultations.size() + " consultation(s) found", consultations));
    }

    @PutMapping("/{consultationId}")
    public ResponseEntity<ApiResponse<ConsultationDTO>> updateConsultation(
            @PathVariable String consultationId,
            @RequestBody ConsultationDTO dto) {

        log.info("API: Update consultation {}", consultationId);

        ConsultationDTO updated = consultationService.updateConsultation(consultationId, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Consultation updated successfully", updated)
        );
    }
    @DeleteMapping("/{consultationId}")
    public ResponseEntity<ApiResponse<String>> deleteConsultation(
            @PathVariable String consultationId) {

        log.info("API: Delete consultation {}", consultationId);

        consultationService.deleteConsultation(consultationId);

        return ResponseEntity.ok(
                ApiResponse.success("Consultation deleted successfully", consultationId)
        );
    }

// ================== ConsultationController ==================
    @GetMapping("/by-doctor-date")
    public ResponseEntity<ApiResponse<List<ConsultationDTO>>> getConsultationsByDoctorAndDate(
            @RequestParam("doctorId") String doctorId,
            @RequestParam("date") String date) {

        log.info("API: Get completed consultations for Doctor {} on Date {}", doctorId, date);

        // Parse date string to LocalDate
        LocalDate localDate = LocalDate.parse(date); // expects format yyyy-MM-dd

        // Use service method that calls native query
    List<ConsultationDTO> consultations =
            consultationService.getConsultationsByDoctorAndDate(doctorId, localDate);

        log.info("Consultations Count: {}", consultations.size());

        return ResponseEntity.ok(
                ApiResponse.success("Loaded", consultations)
        );
    }


}

