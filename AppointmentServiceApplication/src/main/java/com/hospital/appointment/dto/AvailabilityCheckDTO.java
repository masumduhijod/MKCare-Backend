/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== Availability Check DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityCheckDTO {
    
    private String doctorId;
    private String doctorName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    private Boolean hasSlots;
    private Integer totalSlots;
    private Integer availableSlots;
    private Integer bookedSlots;
}
