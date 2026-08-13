/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.dto;

/**
 *
 * @author mduhijod
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// ========== Book Appointment Request DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAppointmentDTO {
    
    private String cvrNumber; // Optional - if CVR already created
    
//    @NotBlank(message = "PIN number is required")
    private String pinNumber;
    
//    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
//    @NotNull(message = "Appointment date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;
    
//    @NotNull(message = "Appointment time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime appointmentTime;
    
    private Long slotId;
    
    private String appointmentType; // NEW, FOLLOW_UP, EMERGENCY
    
    private String symptoms;
    private String notes;
    private String opCaseNumber;
    
//    @NotBlank(message = "Created by is required")
    private String createdBy;
}
