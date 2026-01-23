/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.dto;

/**
 *
 * @author mduhijod
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

// ========== Create CVR Request DTO ==========
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class CreateCvrDTO {
//    
//    @NotBlank(message = "PIN number is required")
//    private String pinNumber;
//    
//    @NotBlank(message = "Visit type is required")
//    private String visitType; // OPD, IPD, EMERGENCY, FOLLOW_UP
//    
//    @NotBlank(message = "Chief complaint is required")
//    private String chiefComplaint;
//    
//    private String symptoms;
//    
//    private String department;
//    
//    private String doctorId;
//    
//    @NotBlank(message = "Created by is required")
//    private String createdBy;
//    
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate visitDate;
//    
//    @JsonFormat(pattern = "HH:mm:ss")
//    private LocalTime visitTime;
//}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCvrDTO {
    
//    @NotBlank(message = "PIN number is required")
    private String pinNumber;
    
//    @NotBlank(message = "Visit type is required")
    private String visitType; // OPD, EMERGENCY, FOLLOW_UP, IPD
    
//    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;
    
    private String symptoms;
    private String department;
    private String doctorId;
    
    // *** APPOINTMENT DETAILS (Optional - used when creating from appointment) ***
    private String appointmentId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate; // Scheduled date
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime appointmentTime; // Scheduled time
    
    // *** NO visit_date / visit_time ***
    // These will be NULL and set only on check-in
    
//    @NotBlank(message = "Created by is required")
    private String createdBy;
}