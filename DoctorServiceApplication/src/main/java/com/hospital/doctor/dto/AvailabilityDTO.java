/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== Doctor Availability DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDTO {
    
    private String doctorId;
    private String doctorName;
    private String specialization;
    private String dayOfWeek;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    
    private Integer availableSlots;
    private Boolean isAvailable;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate; 
}