/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.client;

/**
 *
 * @author mduhijod
 */

import com.hospital.cvr.config.FeignClientConfig;  // ⭐ Add this import
import com.hospital.cvr.dto.ApiResponse;
import com.hospital.cvr.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client to communicate with Patient Service
 */
@FeignClient(
    name = "PATIENT-SERVICE",
    configuration = FeignClientConfig.class  // ⭐ Add this line
)
public interface PatientServiceClient {

    /**
     * Get patient by PIN number
     * @param pinNumber - Patient PIN
     * @return Patient details
     */
    @GetMapping("/patients/pin/{pinNumber}")
    ApiResponse<PatientDTO> getPatientByPIN(@PathVariable("pinNumber") String pinNumber);

    /**
     * Check if patient exists
     * @param pinNumber - Patient PIN
     * @return true if exists
     */
    @GetMapping("/patients/exists/pin/{pinNumber}")
    ApiResponse<Boolean> checkPINExists(@PathVariable("pinNumber") String pinNumber);
}