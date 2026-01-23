/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.controller;

/**
 *
 * @author mduhijod
 */

import com.hospital.appointment.dto.*;
import com.hospital.appointment.service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class SlotController {

    private final SlotService slotService;

    /**
     * Generate slots for doctor on specific date
     * POST /slots/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<SlotDTO>>> generateSlots(
            @Valid @RequestBody GenerateSlotsDTO generateSlotsDTO) {
        
        log.info("API: Generate slots for doctor: {} on {}", 
            generateSlotsDTO.getDoctorId(), generateSlotsDTO.getDate());
        
        List<SlotDTO> slots = slotService.generateSlots(
            generateSlotsDTO.getDoctorId(), 
            generateSlotsDTO.getDate()
        );
        
        ApiResponse<List<SlotDTO>> response = ApiResponse.success(
            slots.size() + " slot(s) generated",
            slots
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get available slots for doctor on date
     * GET /slots/doctor/{doctorId}/date/{date}/available
     */
    @GetMapping("/doctor/{doctorId}/date/{date}/available")
    public ResponseEntity<ApiResponse<List<SlotDTO>>> getAvailableSlots(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        log.info("API: Get available slots for doctor: {} on {}", doctorId, date);
        List<SlotDTO> slots = slotService.getAvailableSlots(doctorId, date);
        
        String message = slots.isEmpty() 
            ? "No available slots" 
            : slots.size() + " available slot(s)";
        
        ApiResponse<List<SlotDTO>> response = ApiResponse.success(message, slots);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all slots for doctor on date
     * GET /slots/doctor/{doctorId}/date/{date}
     */
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<ApiResponse<List<SlotDTO>>> getAllSlots(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        log.info("API: Get all slots for doctor: {} on {}", doctorId, date);
        List<SlotDTO> slots = slotService.getAllSlots(doctorId, date);
        
        ApiResponse<List<SlotDTO>> response = ApiResponse.success(
            slots.size() + " slot(s) found",
            slots
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check availability for doctor on date
     * GET /slots/doctor/{doctorId}/date/{date}/availability
     */
    @GetMapping("/doctor/{doctorId}/date/{date}/availability")
    public ResponseEntity<ApiResponse<AvailabilityCheckDTO>> checkAvailability(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        log.info("API: Check availability for doctor: {} on {}", doctorId, date);
        AvailabilityCheckDTO availability = slotService.checkAvailability(doctorId, date);
        
        String message = availability.getHasSlots() 
            ? "Doctor has available slots" 
            : "No slots available";
        
        ApiResponse<AvailabilityCheckDTO> response = ApiResponse.success(message, availability);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark slot as unavailable
     * PUT /slots/{slotId}/unavailable
     */
    @PutMapping("/{slotId}/unavailable")
    public ResponseEntity<ApiResponse<SlotDTO>> markSlotUnavailable(
            @PathVariable Long slotId) {
        
        log.info("API: Mark slot unavailable - {}", slotId);
        SlotDTO slot = slotService.markSlotUnavailable(slotId);
        
        ApiResponse<SlotDTO> response = ApiResponse.success(
            "Slot marked as unavailable",
            slot
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Release slot (on appointment cancellation)
     * PUT /slots/{slotId}/release
     */
    @PutMapping("/{slotId}/release")
    public ResponseEntity<ApiResponse<SlotDTO>> releaseSlot(
            @PathVariable Long slotId) {
        
        log.info("API: Release slot - {}", slotId);
        SlotDTO slot = slotService.releaseSlot(slotId);
        
        ApiResponse<SlotDTO> response = ApiResponse.success(
            "Slot released successfully",
            slot
        );
        
        return ResponseEntity.ok(response);
    }
}
