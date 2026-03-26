package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultationDTO {
    private Long id;
    private String consultationId;
    private String appointmentId;
    private String cvrNumber;
    private String pinNumber;
    private String patientName;
    private String doctorId;
    private String doctorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime consultationDate;
    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
    private String chiefComplaint;
    private String presentIllness;
    private String examinationFindings;
    private String diagnosis;
    private String treatmentPlan;
    private Boolean followUpRequired;
    private LocalDate followUpDate;
    private String followUpInstructions;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
