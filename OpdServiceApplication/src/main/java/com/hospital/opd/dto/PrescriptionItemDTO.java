/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.dto;

//import javax.validation.constraints.NotBlank;
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
public class PrescriptionItemDTO {
    private Long itemId;
//    @NotBlank(message = "Medicine name is required")
    private String medicineName;
//    @NotBlank(message = "Dosage is required")
    private String dosage;
//    @NotBlank(message = "Frequency is required")
    private String frequency;
//    @NotBlank(message = "Duration is required")
    private String duration;
//    @NotNull(message = "Quantity is required")
    private Integer quantity;
    private String instructions;
    private Boolean morning;
    private Boolean afternoon;
    private Boolean evening;
    private Boolean night;
    private Boolean beforeFood;
    private Boolean afterFood;
}
