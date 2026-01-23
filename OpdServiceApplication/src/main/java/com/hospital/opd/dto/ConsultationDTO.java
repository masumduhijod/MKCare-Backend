/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== Consultation DTOs ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationDTO {
    private Long id;
    private String consultationId;
    private String appointmentId;
    private String cvrNumber;
    private String pinNumber;
    private String patientName;
    private String doctorId;
    private String doctorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime consultationDate;
    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
    private String chiefComplaint;
    private String presentIllness;
    private String examinationFindings;
    private String diagnosis;
    private String treatmentPlan;
    private Boolean followUpRequired;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate followUpDate;
    private String followUpInstructions;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
