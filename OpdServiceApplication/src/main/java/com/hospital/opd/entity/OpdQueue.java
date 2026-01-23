/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.entity;

/**
 *
 * @author mduhijod
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "opd_queue")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpdQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id")
    private Long queueId;

    @Column(name = "appointment_id", length = 20)
    private String appointmentId;

    @Column(name = "cvr_number", length = 20)
    private String cvrNumber;

//    @NotNull(message = "Patient ID is required")
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

//    @NotBlank(message = "PIN number is required")
    @Column(name = "pin_number", nullable = false, length = 20)
    private String pinNumber;

//    @NotBlank(message = "Doctor ID is required")
    @Column(name = "doctor_id", nullable = false, length = 20)
    private String doctorId;

//    @NotNull(message = "Token number is required")
    @Column(name = "token_number", nullable = false)
    private Integer tokenNumber;

//    @NotNull(message = "Queue date is required")
    @Column(name = "queue_date", nullable = false)
    private LocalDate queueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QueueStatus status = QueueStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority = Priority.NORMAL;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "called_at")
    private LocalDateTime calledAt;

    @Column(name = "consultation_start_time")
    private LocalDateTime consultationStartTime;

    @Column(name = "consultation_end_time")
    private LocalDateTime consultationEndTime;

    @Column(name = "waiting_time_minutes")
    private Integer waitingTimeMinutes;

    @Column(name = "consultation_duration_minutes")
    private Integer consultationDurationMinutes;

    // Enums
    public enum QueueStatus {
        WAITING,
        SKIPPED,
        IN_CONSULTATION,
        COMPLETED,
        CANCELLED
    }

    public enum Priority {
        NORMAL,
        URGENT,
        EMERGENCY
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (checkInTime == null) {
            checkInTime = LocalDateTime.now();
        }
        if (queueDate == null) {
            queueDate = LocalDate.now();
        }
    }

    // Helper Methods
    public void callPatient() {
        this.calledAt = LocalDateTime.now();
        calculateWaitingTime();
    }

    public void startConsultation() {
        this.status = QueueStatus.IN_CONSULTATION;
        this.consultationStartTime = LocalDateTime.now();
        if (this.calledAt == null) {
            this.calledAt = LocalDateTime.now();
        }
        calculateWaitingTime();
    }

    public void completeConsultation() {
        this.status = QueueStatus.COMPLETED;
        this.consultationEndTime = LocalDateTime.now();
        calculateConsultationDuration();
    }

    public void skipPatient() {
        this.status = QueueStatus.SKIPPED;
    }

    public void cancelQueue() {
        this.status = QueueStatus.CANCELLED;
    }

    private void calculateWaitingTime() {
        if (checkInTime != null && calledAt != null) {
            long minutes = java.time.Duration.between(checkInTime, calledAt).toMinutes();
            this.waitingTimeMinutes = (int) minutes;
        }
    }

    private void calculateConsultationDuration() {
        if (consultationStartTime != null && consultationEndTime != null) {
            long minutes = java.time.Duration.between(consultationStartTime, consultationEndTime).toMinutes();
            this.consultationDurationMinutes = (int) minutes;
        }
    }

    @Transient
    public boolean isActive() {
        return status == QueueStatus.WAITING || status == QueueStatus.IN_CONSULTATION;
    }
}
