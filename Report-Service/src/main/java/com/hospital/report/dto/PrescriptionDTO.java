package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrescriptionDTO {
    private Long id;
    private String prescriptionId;
    private String consultationNumber;
    private String pinNumber;
    private String patientName;
    private String doctorId;
    private String doctorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime prescriptionDate;
    private Integer validityDays;
    private LocalDate expiryDate;
    private String instructions;
    private String status;
    private List<PrescriptionItemDTO> items;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
