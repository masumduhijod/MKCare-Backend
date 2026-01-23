/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== Doctor Summary DTO (for listing) ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSummaryDTO {
    
    private String doctorId;
    private String fullName;
    private String specialization;
    private String department;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private String status;
    private Boolean availableForOPD;
}
