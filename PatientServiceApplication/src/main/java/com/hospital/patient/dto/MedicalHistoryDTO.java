/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
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
public class MedicalHistoryDTO {
    
    private Long historyId;
    private Long patientId;
    
    private String allergies;
    private String chronicDiseases;
    private String pastSurgeries;
    private String familyHistory;
    
    private String smokingStatus;
    private String alcoholConsumption;
    
    private String bloodPressure;
    private Double heightCm;
    private Double weightKg;
    private Double bmi;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;
}
