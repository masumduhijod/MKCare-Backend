/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== Cancel Appointment DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelAppointmentDTO {
    
    @NotBlank(message = "Appointment ID is required")
    private String appointmentId;
    
    @NotBlank(message = "Cancellation reason is required")
    private String reason;
    
    @NotBlank(message = "Cancelled by is required")
    private String cancelledBy;
}
