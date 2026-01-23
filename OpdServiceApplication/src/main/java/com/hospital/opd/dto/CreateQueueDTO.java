/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class CreateQueueDTO {
//    @NotBlank(message = "Appointment ID is required")
    private String appointmentId;
    private String cvrNumber;
//    @NotBlank(message = "PIN is required")
    private String pinNumber;
//    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
//    @NotNull(message = "Token number is required")
    private Integer tokenNumber;
    private String priority; // NORMAL, URGENT, EMERGENCY
}
