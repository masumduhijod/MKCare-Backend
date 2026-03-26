package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorDTO {
    private Long id;
    private String doctorId;
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
    private String status;
    private Boolean availableForOPD;
    private Boolean availableForEmergency;
    private String roomNumber;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String createdBy;
}
