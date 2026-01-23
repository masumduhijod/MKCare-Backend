/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== Reschedule Appointment DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleAppointmentDTO {
    
    @NotBlank(message = "Appointment ID is required")
    private String appointmentId;
    
    @NotNull(message = "New date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate newDate;
    
    @NotNull(message = "New time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime newTime;
    
    private Long newSlotId;
    
    private String reason;
    
    @NotBlank(message = "Rescheduled by is required")
    private String rescheduledBy;
}
