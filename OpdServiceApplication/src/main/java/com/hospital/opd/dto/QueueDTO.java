/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.dto;

/**
 *
 * @author mduhijod
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// ========== Queue DTOs ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueDTO {
    private Long queueId;
    private String appointmentId;
    private String cvrNumber;
    private String pinNumber;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private Integer tokenNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate queueDate;
    private String status;
    private String priority;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime calledAt;
    private Integer waitingTimeMinutes;
}