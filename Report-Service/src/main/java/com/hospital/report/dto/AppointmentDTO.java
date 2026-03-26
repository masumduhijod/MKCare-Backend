package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentDTO {
    private Long id;
    private String appointmentId;
    private Long cvrId;
    private String cvrNumber;
    private Long patientId;
    private String pinNumber;
    private String patientName;
    private Integer patientAge;
    private String patientGender;
    private String patientContact;
    private String doctorId;
    private String doctorName;
    private String specialization;
    private String department;
    private LocalDate appointmentDate;
    private Integer tokenNumber;
    private String appointmentType;
    private String status;
    private String symptoms;
    private String notes;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkedInAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime consultationStartedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime consultationEndedAt;
    private String cancellationReason;
}
