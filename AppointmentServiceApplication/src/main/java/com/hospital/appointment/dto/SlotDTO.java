/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDTO {
    
    private Long slotId;
    
    // *** NEW: Track which schedule generated this slot ***
    private Long scheduleId;
    
    private String doctorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate slotDate;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime slotTime;
    
    private Boolean isAvailable;
    private Integer maxPatients;
    private Integer bookedCount;
    private Integer availableCapacity;
}