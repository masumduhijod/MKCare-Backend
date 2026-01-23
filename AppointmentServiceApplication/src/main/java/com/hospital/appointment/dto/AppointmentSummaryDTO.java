/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.dto;

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
// ========== Appointment Summary DTO (for listing) ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSummaryDTO {
    
    private String appointmentId;
    private String pinNumber;
    private String patientName;
    private String doctorName;
    private String doctorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime appointmentTime;
    
    private Integer tokenNumber;
    private String status;
    private String appointmentType;
    private String cvrNumber;
}
