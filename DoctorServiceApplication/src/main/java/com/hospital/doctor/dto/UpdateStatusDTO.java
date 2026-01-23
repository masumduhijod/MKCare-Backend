/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== Update Status DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusDTO {
    
//    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
//    @NotBlank(message = "Status is required")
    private String status; // AVAILABLE, ON_LEAVE, BUSY, INACTIVE
    
    private String reason;
}
