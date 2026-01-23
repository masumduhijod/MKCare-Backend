/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.controller;

/**
 *
 * @author mduhijod
 */

import com.hospital.opd.dto.*;
import com.hospital.opd.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

// ========== QueueController ==========
@RestController
@RequestMapping("/opd/queue")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class QueueController {
    
    private final QueueService queueService;
    
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<QueueDTO>> addToQueue(@Valid @RequestBody CreateQueueDTO dto) {
        log.info("API: Add to queue - Appointment {}", dto.getAppointmentId());
        QueueDTO queue = queueService.addToQueue(dto);
        return new ResponseEntity<>(ApiResponse.success("Added to queue", queue), HttpStatus.CREATED);
    }
    
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<ApiResponse<List<QueueDTO>>> getDoctorQueue(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        log.info("API: Get queue for doctor {} on {}", doctorId, date);
        List<QueueDTO> queue = queueService.getDoctorQueue(doctorId, date);
        return ResponseEntity.ok(ApiResponse.success(queue.size() + " patient(s) in queue", queue));
    }
    
    @PutMapping("/doctor/{doctorId}/date/{date}/call-next")
    public ResponseEntity<ApiResponse<QueueDTO>> callNext(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        log.info("API: Call next patient for doctor {}", doctorId);
        QueueDTO next = queueService.callNext(doctorId, date);
        return ResponseEntity.ok(ApiResponse.success("Patient called - Token " + next.getTokenNumber(), next));
    }
    
    @PutMapping("/{queueId}/start-consultation")
    public ResponseEntity<ApiResponse<QueueDTO>> startConsultation(@PathVariable Long queueId) {
        log.info("API: Start consultation for queue {}", queueId);
        QueueDTO queue = queueService.startConsultation(queueId);
        return ResponseEntity.ok(ApiResponse.success("Consultation started", queue));
    }
    
    @PutMapping("/{queueId}/complete")
    public ResponseEntity<ApiResponse<QueueDTO>> completeQueue(@PathVariable Long queueId) {
        log.info("API: Complete queue {}", queueId);
        QueueDTO queue = queueService.completeQueue(queueId);
        return ResponseEntity.ok(ApiResponse.success("Queue completed", queue));
    }
}