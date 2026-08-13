/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.dto;

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
// ========== CVR Summary DTO (for listing) ==========
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class CvrSummaryDTO {
//    
//    private String cvrNumber;
//    private String pinNumber;
//    private String patientName;
//    
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate visitDate;
//    
//    @JsonFormat(pattern = "HH:mm:ss")
//    private LocalTime visitTime;
//    
//    private String visitType;
//    private String chiefComplaint;
//    private String status;
//    private String doctorName;
//}


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvrSummaryDTO {
    
    private String cvrNumber;
    private String pinNumber;
    private String patientName;
    
    // Show both appointment and actual visit details
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate; // Scheduled
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDate; // Actual (NULL if not visited)
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime appointmentTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime visitTime;
    
    private String visitType;
    private String chiefComplaint;
    private String status;
    private String doctorName;
    private String doctorId;
    private String department;
    
    // Helper
    private Boolean hasVisited;
    
    // Late arrival indicator
    private String arrivalStatus; // "On Time", "Late", "Not Arrived"
}