package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrescriptionItemDTO {
    private Long itemId;
    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;
    private Integer quantity;
    private String instructions;
    private Boolean morning;
    private Boolean afternoon;
    private Boolean evening;
    private Boolean night;
    private Boolean beforeFood;
    private Boolean afterFood;
}
