/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.controller;

/**
 *
 * @author mduhijod
 */

import com.hospital.doctor.dto.ApiResponse;
import com.hospital.doctor.dto.AvailabilityDTO;
import com.hospital.doctor.dto.DoctorScheduleDTO;
import com.hospital.doctor.service.DoctorScheduleService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

//@RestController
//@RequestMapping("/doctors")
//@RequiredArgsConstructor
//@Slf4j
////@CrossOrigin(origins = "*")
//public class DoctorScheduleController {
//
//    private final DoctorScheduleService scheduleService;
//
//    /**
//     * Add schedule for doctor
//     * POST /doctors/{doctorId}/schedules
//     */
//    @PostMapping("/{doctorId}/schedules")
//    public ResponseEntity<ApiResponse<DoctorScheduleDTO>> addSchedule(
//            @PathVariable String doctorId,
//            @Valid @RequestBody DoctorScheduleDTO scheduleDTO) {
//        
//        log.info("API: Add schedule for doctor: {} on {}", doctorId, scheduleDTO.getDayOfWeek());
//        DoctorScheduleDTO schedule = scheduleService.addSchedule(doctorId, scheduleDTO);
//        
//        ApiResponse<DoctorScheduleDTO> response = ApiResponse.success(
//            "Schedule added successfully",
//            schedule
//        );
//        
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//
//    /**
//     * Get doctor schedules
//     * GET /doctors/{doctorId}/schedules
//     */
//    @GetMapping("/{doctorId}/schedules")
//    public ResponseEntity<ApiResponse<List<DoctorScheduleDTO>>> getDoctorSchedules(
//            @PathVariable String doctorId) {
//        
//        log.info("API: Get schedules for doctor: {}", doctorId);
//        List<DoctorScheduleDTO> schedules = scheduleService.getDoctorSchedules(doctorId);
//        
//        String message = schedules.isEmpty() 
//            ? "No schedules found" 
//            : schedules.size() + " schedule(s) found";
//        
//        ApiResponse<List<DoctorScheduleDTO>> response = ApiResponse.success(message, schedules);
//        
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Get active schedules
//     * GET /doctors/{doctorId}/schedules/active
//     */
//    @GetMapping("/{doctorId}/schedules/active")
//    public ResponseEntity<ApiResponse<List<DoctorScheduleDTO>>> getActiveSchedules(
//            @PathVariable String doctorId) {
//        
//        log.info("API: Get active schedules for doctor: {}", doctorId);
//        List<DoctorScheduleDTO> schedules = scheduleService.getActiveDoctorSchedules(doctorId);
//        
//        ApiResponse<List<DoctorScheduleDTO>> response = ApiResponse.success(
//            schedules.size() + " active schedule(s) found",
//            schedules
//        );
//        
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Get schedule by doctor and day
//     * GET /doctors/{doctorId}/schedules/{dayOfWeek}
//     */
//    @GetMapping("/{doctorId}/schedules/{dayOfWeek}")
//    public ResponseEntity<ApiResponse<DoctorScheduleDTO>> getScheduleByDay(
//            @PathVariable String doctorId,
//            @PathVariable String dayOfWeek) {
//        
//        log.info("API: Get schedule for doctor: {} on {}", doctorId, dayOfWeek);
//        DoctorScheduleDTO schedule = scheduleService.getScheduleByDoctorAndDay(doctorId, dayOfWeek);
//        
//        ApiResponse<DoctorScheduleDTO> response = ApiResponse.success(
//            "Schedule found",
//            schedule
//        );
//        
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Check doctor availability
//     * GET /doctors/{doctorId}/availability/{dayOfWeek}
//     */
//    @GetMapping("/{doctorId}/availability/{dayOfWeek}")
//    public ResponseEntity<ApiResponse<AvailabilityDTO>> checkAvailability(
//            @PathVariable String doctorId,
//            @PathVariable String dayOfWeek) {
//        
//        log.info("API: Check availability for doctor: {} on {}", doctorId, dayOfWeek);
//        AvailabilityDTO availability = scheduleService.checkAvailability(doctorId, dayOfWeek);
//        
//        String message = availability.getIsAvailable() 
//            ? "Doctor is available" 
//            : "Doctor is not available";
//        
//        ApiResponse<AvailabilityDTO> response = ApiResponse.success(message, availability);
//        
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Update schedule
//     * PUT /doctors/schedules/{scheduleId}
//     */
//    @PutMapping("/schedules/{scheduleId}")
//    public ResponseEntity<ApiResponse<DoctorScheduleDTO>> updateSchedule(
//            @PathVariable Long scheduleId,
//            @Valid @RequestBody DoctorScheduleDTO scheduleDTO) {
//        
//        log.info("API: Update schedule: {}", scheduleId);
//        DoctorScheduleDTO schedule = scheduleService.updateSchedule(scheduleId, scheduleDTO);
//        
//        ApiResponse<DoctorScheduleDTO> response = ApiResponse.success(
//            "Schedule updated successfully",
//            schedule
//        );
//        
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Delete schedule
//     * DELETE /doctors/schedules/{scheduleId}
//     */
//    @DeleteMapping("/schedules/{scheduleId}")
//    public ResponseEntity<ApiResponse<String>> deleteSchedule(
//            @PathVariable Long scheduleId) {
//        
//        log.info("API: Delete schedule: {}", scheduleId);
//        String message = scheduleService.deleteSchedule(scheduleId);
//        
//        ApiResponse<String> response = ApiResponse.success(message, null);
//        
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Toggle schedule status
//     * PUT /doctors/schedules/{scheduleId}/toggle
//     */
//    @PutMapping("/schedules/{scheduleId}/toggle")
//    public ResponseEntity<ApiResponse<DoctorScheduleDTO>> toggleScheduleStatus(
//            @PathVariable Long scheduleId,
//            @RequestParam boolean isActive) {
//        
//        log.info("API: Toggle schedule status: {} to {}", scheduleId, isActive);
//        DoctorScheduleDTO schedule = scheduleService.toggleScheduleStatus(scheduleId, isActive);
//        
//        ApiResponse<DoctorScheduleDTO> response = ApiResponse.success(
//            "Schedule status updated",
//            schedule
//        );
//        
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Get all schedules for a specific day
//     * GET /doctors/schedules/day/{dayOfWeek}
//     */
//    @GetMapping("/schedules/day/{dayOfWeek}")
//    public ResponseEntity<ApiResponse<List<DoctorScheduleDTO>>> getSchedulesByDay(
//            @PathVariable String dayOfWeek) {
//        
//        log.info("API: Get all schedules for: {}", dayOfWeek);
//        List<DoctorScheduleDTO> schedules = scheduleService.getSchedulesByDay(dayOfWeek);
//        
//        ApiResponse<List<DoctorScheduleDTO>> response = ApiResponse.success(
//            schedules.size() + " schedule(s) found for " + dayOfWeek,
//            schedules
//        );
//        
//        return ResponseEntity.ok(response);
//    }
//}


@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Slf4j
public class DoctorScheduleController {

    private final DoctorScheduleService scheduleService;

    /**
     * Add schedule for doctor
     * POST /doctors/{doctorId}/schedules
     */
    @PostMapping("/{doctorId}/schedules")
    public ResponseEntity<ApiResponse<DoctorScheduleDTO>> addSchedule(
            @PathVariable String doctorId,
            @Valid @RequestBody DoctorScheduleDTO scheduleDTO) {
        
        // *** CHANGED: Log with date ***
        log.info("API: Add schedule for doctor: {} on {}", doctorId, scheduleDTO.getScheduleDate());
        DoctorScheduleDTO schedule = scheduleService.addSchedule(doctorId, scheduleDTO);
        
        ApiResponse<DoctorScheduleDTO> response = ApiResponse.success(
            "Schedule added successfully for " + scheduleDTO.getScheduleDate(),
            schedule
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get doctor schedules
     * GET /doctors/{doctorId}/schedules
     */
    @GetMapping("/{doctorId}/schedules")
    public ResponseEntity<ApiResponse<List<DoctorScheduleDTO>>> getDoctorSchedules(
            @PathVariable String doctorId) {
        
        log.info("API: Get schedules for doctor: {}", doctorId);
        List<DoctorScheduleDTO> schedules = scheduleService.getDoctorSchedules(doctorId);
        
        String message = schedules.isEmpty() 
            ? "No schedules found" 
            : schedules.size() + " schedule(s) found";
        
        ApiResponse<List<DoctorScheduleDTO>> response = ApiResponse.success(message, schedules);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get active schedules
     * GET /doctors/{doctorId}/schedules/active
     */
    @GetMapping("/{doctorId}/schedules/active")
    public ResponseEntity<ApiResponse<List<DoctorScheduleDTO>>> getActiveSchedules(
            @PathVariable String doctorId) {
        
        log.info("API: Get active schedules for doctor: {}", doctorId);
        List<DoctorScheduleDTO> schedules = scheduleService.getActiveDoctorSchedules(doctorId);
        
        ApiResponse<List<DoctorScheduleDTO>> response = ApiResponse.success(
            schedules.size() + " active schedule(s) found",
            schedules
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * *** CHANGED: Get schedule by doctor and DATE ***
     * GET /doctors/{doctorId}/schedules/{scheduleDate}
     * scheduleDate format: yyyy-MM-dd (e.g., 2025-01-15)
     */
    @GetMapping("/{doctorId}/schedules/{scheduleDate}")
    public ResponseEntity<ApiResponse<DoctorScheduleDTO>> getScheduleByDate(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate) {
        
        log.info("API: Get schedule for doctor: {} on {}", doctorId, scheduleDate);
        DoctorScheduleDTO schedule = scheduleService.getScheduleByDoctorAndDate(
            doctorId, scheduleDate
        );
        
        ApiResponse<DoctorScheduleDTO> response = ApiResponse.success(
            "Schedule found",
            schedule
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * *** CHANGED: Check doctor availability by DATE ***
     * GET /doctors/{doctorId}/availability/{scheduleDate}
     */
    @GetMapping("/{doctorId}/availability/{scheduleDate}")
    public ResponseEntity<ApiResponse<AvailabilityDTO>> checkAvailability(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate) {
        
        log.info("API: Check availability for doctor: {} on {}", doctorId, scheduleDate);
        AvailabilityDTO availability = scheduleService.checkAvailability(
            doctorId, scheduleDate
        );
        
        String message = availability.getIsAvailable() 
            ? "Doctor is available" 
            : "Doctor is not available";
        
        ApiResponse<AvailabilityDTO> response = ApiResponse.success(message, availability);
        
        return ResponseEntity.ok(response);
    }

    /**
     * *** NEW: Get schedules in date range ***
     * GET /doctors/{doctorId}/schedules/range?startDate=2025-01-01&endDate=2025-01-31
     */
    @GetMapping("/{doctorId}/schedules/range")
    public ResponseEntity<ApiResponse<List<DoctorScheduleDTO>>> getSchedulesByDateRange(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("API: Get schedules for doctor: {} from {} to {}", 
            doctorId, startDate, endDate);
        
        List<DoctorScheduleDTO> schedules = scheduleService.getSchedulesByDateRange(
            doctorId, startDate, endDate
        );
        
        ApiResponse<List<DoctorScheduleDTO>> response = ApiResponse.success(
            schedules.size() + " schedule(s) found in date range",
            schedules
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * *** NEW: Get upcoming schedules ***
     * GET /doctors/{doctorId}/schedules/upcoming?days=30
     */
    @GetMapping("/{doctorId}/schedules/upcoming")
    public ResponseEntity<ApiResponse<List<DoctorScheduleDTO>>> getUpcomingSchedules(
            @PathVariable String doctorId,
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("API: Get upcoming {} days schedules for doctor: {}", days, doctorId);
        
        List<DoctorScheduleDTO> schedules = scheduleService.getUpcomingSchedules(
            doctorId, days
        );
        
        ApiResponse<List<DoctorScheduleDTO>> response = ApiResponse.success(
            schedules.size() + " upcoming schedule(s) found",
            schedules
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update schedule
     * PUT /doctors/schedules/{scheduleId}
     */
    @PutMapping("/schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<DoctorScheduleDTO>> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody DoctorScheduleDTO scheduleDTO) {
        
        log.info("API: Update schedule: {}", scheduleId);
        DoctorScheduleDTO schedule = scheduleService.updateSchedule(scheduleId, scheduleDTO);
        
        ApiResponse<DoctorScheduleDTO> response = ApiResponse.success(
            "Schedule updated successfully",
            schedule
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete schedule
     * DELETE /doctors/schedules/{scheduleId}
     */
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<String>> deleteSchedule(
            @PathVariable Long scheduleId) {
        
        log.info("API: Delete schedule: {}", scheduleId);
        String message = scheduleService.deleteSchedule(scheduleId);
        
        ApiResponse<String> response = ApiResponse.success(message, null);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Toggle schedule status
     * PUT /doctors/schedules/{scheduleId}/toggle
     */
    @PutMapping("/schedules/{scheduleId}/toggle")
    public ResponseEntity<ApiResponse<DoctorScheduleDTO>> toggleScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestParam boolean isActive) {
        
        log.info("API: Toggle schedule status: {} to {}", scheduleId, isActive);
        DoctorScheduleDTO schedule = scheduleService.toggleScheduleStatus(scheduleId, isActive);
        
        ApiResponse<DoctorScheduleDTO> response = ApiResponse.success(
            "Schedule status updated",
            schedule
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * *** REMOVED: getSchedulesByDay endpoint - no longer needed ***
     * (Was: GET /doctors/schedules/day/{dayOfWeek})
     */
}