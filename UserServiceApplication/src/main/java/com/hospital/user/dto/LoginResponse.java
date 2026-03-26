/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.user.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Response DTO
 * Now includes tenant/clinic information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String role;
    private String fullName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;
    
    // ⭐ NEW FIELDS - Tenant/Clinic Information
    private String tenantId;      // e.g., "HMS001"
    private String clinicName;    // e.g., "HMS Default Clinic"
    private String clinicLogo;    // e.g., "/logos/hms.png"
    private String clinicAddress;
    private String clinicPhone;
}