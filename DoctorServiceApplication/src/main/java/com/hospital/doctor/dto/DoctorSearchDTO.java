/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
// ========== Doctor Search DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSearchDTO {
    
    private String doctorId;
    private String fullName;
    private String specialization;
    private String department;
    private String contactNumber;
    private String status;
}
