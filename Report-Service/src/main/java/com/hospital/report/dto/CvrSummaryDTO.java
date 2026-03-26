package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CvrSummaryDTO {
    private String cvrNumber;
    private String pinNumber;
    private String patientName;
    private LocalDate appointmentDate;
    private LocalDate visitDate;
    private String visitType;
    private String chiefComplaint;
    private String status;
    private String doctorName;
    private Boolean hasVisited;
    private String arrivalStatus;
    private String department;
    private String doctorId;
}
