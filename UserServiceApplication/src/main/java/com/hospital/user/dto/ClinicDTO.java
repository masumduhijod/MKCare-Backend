package com.hospital.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * DTO for creating/updating a clinic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDTO {

    private String tenantId; // e.g., "HMS001" - Auto-generated if not provided then

    @NotBlank(message = "Clinic code is required")
    private String clinicCode; // URL slug: "hms", "apollo"

    @NotBlank(message = "Clinic name is required")
    private String clinicName; // e.g., "HMS Default Clinic"

    private String organizationId; // Organization/Group ID
    private String operationalId; // Operational license ID

    @NotBlank(message = "Database name is required")
    private String dbName; // e.g., "clinic_hms"

    private String address;
    private String phone;
    private String email;
    private String logoPath;
    private boolean active;

    // Admin user details for new clinic
    private String adminUsername;
    private String adminPassword;
    private String adminEmail;
    private String adminFirstName;
    private String adminLastName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime subscriptionStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime subscriptionExpiry;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;
}
