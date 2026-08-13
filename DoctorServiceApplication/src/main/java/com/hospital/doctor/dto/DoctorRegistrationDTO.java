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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

// ========== Doctor Registration DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegistrationDTO {
    
//    @NotBlank(message = "First name is required")
    private String firstName;
    
    private String lastName;
    
//    @NotBlank(message = "Specialization is required")
    private String specialization;
    
    private String qualification;
    
//    @Min(value = 0, message = "Experience must be non-negative")
    private Integer experienceYears;
    
    private String department;
    
//    @NotBlank(message = "Contact number is required")
//    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;
    
//    @Email(message = "Invalid email format")
    private String email;
    
    private String licenseNumber;
    private String registrationNumber;
    
//    @NotNull(message = "Consultation fee is required")
//    @DecimalMin(value = "0.0", message = "Fee must be positive")
    private BigDecimal consultationFee;
    
    private BigDecimal followUpFee;
    private Integer followUpDaysLimit;
    
    private Boolean availableForOPD;
    private Boolean availableForEmergency;
    
    private String photoUrl;
    private String bio;
    private String languagesSpoken;
    private String roomNumber;
    
//    @NotBlank(message = "Created by is required")
    private String createdBy;
}
