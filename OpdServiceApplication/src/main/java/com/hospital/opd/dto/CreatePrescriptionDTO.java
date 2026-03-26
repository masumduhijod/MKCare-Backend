/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.dto;

import java.util.List;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePrescriptionDTO {
//    @NotNull(message = "Consultation ID is required")
    private String consultationId;
    private String consultationNumber;
//    @NotBlank(message = "PIN is required")
    private String pinNumber;
//    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    private Integer validityDays;
    private String instructions;
//    @NotEmpty(message = "At least one medicine is required")
    private List<PrescriptionItemDTO> items;
}