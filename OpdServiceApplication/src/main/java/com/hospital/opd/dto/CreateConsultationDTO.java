/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
//import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsultationDTO {
    private String appointmentId;
//    @NotBlank(message = "CVR number is required")
    private String cvrNumber;
//    @NotBlank(message = "PIN is required")
    private String pinNumber;
//    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
//    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;
    private String presentIllness;
    private String examinationFindings;
//    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;
    private String treatmentPlan;
    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
    private Boolean followUpRequired;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate followUpDate;
    private String followUpInstructions;
}

