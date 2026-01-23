/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== CVR Response DTO ==========
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class CvrDTO {
//    
//    private Long cvrId;
//    private String cvrNumber;
//    private Long patientId;
//    private String pinNumber;
//    
//    // Patient Details (from Patient Service)
//    private String patientName;
//    private Integer patientAge;
//    private String patientGender;
//    private String patientContact;
//    
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate visitDate;
//    
//    @JsonFormat(pattern = "HH:mm:ss")
//    private LocalTime visitTime;
//    
//    private String visitType;
//    private String department;
//    private String doctorId;
//    private String doctorName;
//    
//    private String chiefComplaint;
//    private String symptoms;
//    
//    private String status;
//    
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime createdAt;
//    private String createdBy;
//    
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime checkedInAt;
//    
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime consultationStartedAt;
//    
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime consultationCompletedAt;
//    
//    private Boolean isBilled;
//    private String billingId;
//    
//    private List<CvrVitalsDTO> vitals;
//}


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvrDTO {
    
    private Long cvrId;
    private String cvrNumber;
    private Long patientId;
    private String pinNumber;
    private String patientName;
    
    // Appointment details
    private String appointmentId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate; // Scheduled
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime appointmentTime; // Scheduled
    
    // Visit details (NULL if not visited yet)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDate; // Actual (NULL = not visited)
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime visitTime; // Actual (NULL = not visited)
    
    private String visitType;
    private String department;
    private String doctorId;
    private String doctorName;
    private String chiefComplaint;
    private String symptoms;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String createdBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkedInAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime consultationStartedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime consultationCompletedAt;
    
    private Boolean isBilled;
    private String billingId;
    private List<CvrVitalsDTO> vitals;
    
    // Helper fields
    private Boolean hasVisited; // Computed: visitDate != null
    private Boolean isPending;  // Computed: status == APPOINTMENT_SCHEDULED && !hasVisited
}