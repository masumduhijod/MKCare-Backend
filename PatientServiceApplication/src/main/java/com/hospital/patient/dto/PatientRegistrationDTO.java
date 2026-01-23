/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.dto;

/**
 *
 * @author mduhijod
 */
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.patient.entity.Patient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ============ Patient Registration DTO ============
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegistrationDTO {
    
//    @NotNull(message = "First name is required")
//    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;
    
//    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;
    
//    @NotNull(message = "Date of birth is required")
//    @Past(message = "Date of birth must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
//    @NotNull(message = "Gender is required")
    private String gender; // MALE, FEMALE, OTHER
    
    private String bloodGroup;
    
//    @NotNull(message = "Contact number is required")
//    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;
    
    private String alternateContact;
    
//    @Email(message = "Invalid email format")
    private String email;
    
//    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhar number must be 12 digits")
    private String aadharNumber;
    
    // Address
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    
    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String emergencyContactRelation;
    
    // Insurance
    private String insuranceProvider;
    private String insuranceId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate insuranceExpiryDate;
    
    private String photoUrl;
    private String remarks;
    
//    @NotNull(message = "Registered by is required")
    private String registeredBy;
}
