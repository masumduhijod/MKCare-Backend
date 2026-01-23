/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.client;

/**
 *
 * @author mduhijod
 */

import com.hospital.appointment.dto.ApiResponse;
import com.hospital.appointment.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PATIENT-SERVICE")
public interface PatientServiceClient {

    @GetMapping("/patients/pin/{pinNumber}")
    ApiResponse<PatientDTO> getPatientByPIN(@PathVariable("pinNumber") String pinNumber);

    @GetMapping("/patients/exists/pin/{pinNumber}")
    ApiResponse<Boolean> checkPINExists(@PathVariable("pinNumber") String pinNumber);
}

