package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CvrDTO {
    private Long cvrId;
    private String cvrNumber;
    private Long patientId;
    private String pinNumber;
    private String patientName;
    private String appointmentId;
    private LocalDate appointmentDate;
    private LocalDate visitDate;
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
    private Boolean hasVisited;
    private Boolean isPending;
}
