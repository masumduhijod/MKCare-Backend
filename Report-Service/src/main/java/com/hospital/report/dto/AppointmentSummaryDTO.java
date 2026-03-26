package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentSummaryDTO {
    private String appointmentId;
    private String pinNumber;
    private String patientName;
    private String doctorName;
    private String doctorId;
    private LocalDate appointmentDate;
    private Integer tokenNumber;
    private String status;
    private String appointmentType;
    private String cvrNumber;
    private String specialization;
    private String department;
}
