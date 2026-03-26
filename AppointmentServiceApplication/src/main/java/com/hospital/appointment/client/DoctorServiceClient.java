/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.client;

import com.hospital.appointment.dto.ApiResponse;
import com.hospital.appointment.dto.DoctorDTO;
import com.hospital.appointment.dto.DoctorScheduleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

/**
 * Feign Client for Doctor Service
 * UPDATED: Changed from day-based to date-based schedule lookup
 */
@FeignClient(name = "DOCTOR-SERVICE")
public interface DoctorServiceClient {

    @GetMapping("/doctors/{doctorId}")
    ApiResponse<DoctorDTO> getDoctorById(@PathVariable("doctorId") String doctorId);

    /**
     * *** CHANGED: Get schedule by DATE instead of dayOfWeek ***
     * OLD: /doctors/{doctorId}/schedules/{dayOfWeek}
     * NEW: /doctors/{doctorId}/schedules/{scheduleDate}
     */
    @GetMapping("/doctors/{doctorId}/schedules/{scheduleDate}")
    ApiResponse<DoctorScheduleDTO> getScheduleByDate(
        @PathVariable("doctorId") String doctorId,
        @PathVariable("scheduleDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate
    );

    @GetMapping("/doctors/{doctorId}/schedules/range")
    ApiResponse<java.util.List<DoctorScheduleDTO>> getSchedulesByDateRange(
        @PathVariable("doctorId") String doctorId,
        @org.springframework.web.bind.annotation.RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @org.springframework.web.bind.annotation.RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    );

    @GetMapping("/doctors/exists/{doctorId}")
    ApiResponse<Boolean> checkDoctorExists(@PathVariable("doctorId") String doctorId);
}