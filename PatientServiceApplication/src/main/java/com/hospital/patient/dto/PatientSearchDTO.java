/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientSearchDTO {
    
    private Long patientId;
    private String pinNumber;
    private String fullName;
    private Integer age;
    private String gender;
    private String contactNumber;
    private String email;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;
    private String status;
    
    // Additional info for search results
    private Integer totalVisits;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastVisitDate;
    private String lastCVRNumber;
}

