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
// ========== CvrServiceClient.java ==========

import com.hospital.appointment.dto.ApiResponse;
import com.hospital.appointment.dto.CreateCvrRequestDTO;
import com.hospital.appointment.dto.CvrDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "CVR-SERVICE")
public interface CvrServiceClient {

    @GetMapping("/cvr/{cvrNumber}")
    ApiResponse<CvrDTO> getCVRByNumber(
            @PathVariable("cvrNumber") String cvrNumber
    );

    @GetMapping("/cvr/exists/{cvrNumber}")
    ApiResponse<Boolean> checkCVRExists(
            @PathVariable("cvrNumber") String cvrNumber
    );

    // ✅ FIXED URL
    @PostMapping("/cvr/create")
    ApiResponse<CvrDTO> createCVR(
            @RequestBody CreateCvrRequestDTO request
    );

    // ✅ MOST IMPORTANT (appointment → CVR)
    @GetMapping("/cvr/by-appointment/{appointmentId}")
    ApiResponse<CvrDTO> getCVRByAppointmentId(
            @PathVariable("appointmentId") String appointmentId
    );
}

