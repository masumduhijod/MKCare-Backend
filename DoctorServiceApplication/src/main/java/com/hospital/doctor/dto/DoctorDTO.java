/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== Doctor Response DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    
    private Long id;
    private String doctorId;
    private Long userId;
    
    private String firstName;
    private String lastName;
    private String fullName;
    
    private String specialization;
    private String qualification;
    private Integer experienceYears;
    private String department;
    
    private String contactNumber;
    private String email;
    
    private String licenseNumber;
    private String registrationNumber;
    
    private BigDecimal consultationFee;
    private BigDecimal followUpFee;
    private Integer followUpDaysLimit;
    
    private String status;
    private Boolean availableForOPD;
    private Boolean availableForEmergency;
    
    private String photoUrl;
    private String bio;
    private String languagesSpoken;
    private String roomNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private List<DoctorScheduleDTO> schedules;
}
