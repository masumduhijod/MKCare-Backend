/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== CVR Vitals DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvrVitalsDTO {
    
    private Long vitalId;
    private Long cvrId;
    
    private Double temperatureF;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private String bloodPressure; // "120/80"
    private Integer pulseRate;
    private Integer respiratoryRate;
    private Integer spo2Percentage;
    private Double weightKg;
    private Double heightCm;
    private Double bmi;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordedAt;
    private String recordedBy;
}
