/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Doctor Schedule DTO for Appointment Service
 * UPDATED: Changed from dayOfWeek to scheduleDate
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleDTO {
    
    private Long scheduleId;
    
    // *** CHANGED: From dayOfWeek to scheduleDate ***
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    
    private Integer slotDurationMinutes;
    private Integer maxPatientsPerSlot;
    
    // *** NEW: Optional break times ***
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakStartTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakEndTime;
    
    private Boolean isActive;
    
    // *** NEW: Helper field for display ***
    private String dayName;
}