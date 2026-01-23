/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.dto;

/**
 *
 * @author mduhijod
 */
// ========== Doctor Schedule DTO ==========

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class DoctorScheduleDTO {
//    
//    private Long scheduleId;
//    private String doctorId;
//    
//    @NotBlank(message = "Day of week is required")
//    private String dayOfWeek;
//    
//    @NotNull(message = "Start time is required")
//    @JsonFormat(pattern = "HH:mm")
//    private LocalTime startTime;
//    
//    @NotNull(message = "End time is required")
//    @JsonFormat(pattern = "HH:mm")
//    private LocalTime endTime;
//    
//    private Integer slotDurationMinutes;
//    private Integer maxPatientsPerSlot;
//    private Boolean isActive;
//    
//    @JsonFormat(pattern = "HH:mm")
//    private LocalTime breakStartTime;
//    
//    @JsonFormat(pattern = "HH:mm")
//    private LocalTime breakEndTime;
//    
//    private Integer totalSlots;
//}
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleDTO {
    
    private Long scheduleId;
    private String doctorId;
    
    // *** MAIN CHANGE: Replace dayOfWeek with scheduleDate ***
//    @NotNull(message = "Schedule date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;  // CHANGED from String dayOfWeek
    
//    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
//    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    
    private Integer slotDurationMinutes;
    private Integer maxPatientsPerSlot;
    private Boolean isActive;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakStartTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakEndTime;
    
    private Integer totalSlots;
    
    // *** NEW: Helper field for display (read-only) ***
    private String dayName;  // Will be calculated from scheduleDate
}