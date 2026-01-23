/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.dto;

/**
 *
 * @author mduhijod
 */
// ========== Patient Visit History DTO ==========

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientVisitHistoryDTO {
    
    private String pinNumber;
    private String patientName;
    private Integer totalVisits;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastVisitDate;
    private String lastCvrNumber;
    
    private List<CvrSummaryDTO> recentVisits;
}
