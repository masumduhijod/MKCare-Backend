/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== Record Vitals Request DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordVitalsDTO {
    
    @NotBlank(message = "CVR number is required")
    private String cvrNumber;
    
    private Double temperatureF;
    private String bloodPressure; // "120/80"
    private Integer pulseRate;
    private Integer respiratoryRate;
    private Integer spo2Percentage;
    private Double weightKg;
    private Double heightCm;
    
    @NotBlank(message = "Recorded by is required")
    private String recordedBy;
}

