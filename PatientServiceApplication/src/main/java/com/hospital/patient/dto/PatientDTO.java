/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class PatientDTO {
    
    private Long patientId;
    private String pinNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private Integer age;
    private String gender;
    private String bloodGroup;
    
    private String contactNumber;
    private String alternateContact;
    private String email;
    private String aadharNumber;
    
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String fullAddress;
    
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String emergencyContactRelation;
    
    private String insuranceProvider;
    private String insuranceId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate insuranceExpiryDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationDate;
    private String registeredBy;
    private String status;
    private String photoUrl;
    private String remarks;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
