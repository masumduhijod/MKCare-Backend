/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.entity;

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
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "appointment_id", unique = true, nullable = false, length = 20)
    private String appointmentId;

    // CVR Link
    @Column(name = "cvr_id")
    private Long cvrId;

    @Column(name = "cvr_number", length = 20)
    private String cvrNumber;

    // Patient Details
//    @NotNull(message = "Patient ID is required")
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

//    @NotBlank(message = "PIN number is required")
    @Column(name = "pin_number", nullable = false, length = 20)
    private String pinNumber;

    // Doctor Details
//    @NotBlank(message = "Doctor ID is required")
    @Column(name = "doctor_id", nullable = false, length = 20)
    private String doctorId;

    // Appointment Date & Time
//    @NotNull(message = "Appointment date is required")
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

//    @NotNull(message = "Appointment time is required")
    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Column(name = "slot_id")
    private Long slotId;

    // Token Number (for queue management)
    @Column(name = "token_number")
    private Integer tokenNumber;

    // Appointment Type
    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type")
    private AppointmentType appointmentType = AppointmentType.NEW;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    // Additional Info
    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "op_case_number", length = 20)
    private String opCaseNumber;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    @Column(name = "consultation_started_at")
    private LocalDateTime consultationStartedAt;

    @Column(name = "consultation_ended_at")
    private LocalDateTime consultationEndedAt;

    // Cancellation Details
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_by", length = 100)
    private String cancelledBy;

    // Enums
    public enum AppointmentType {
        NEW, FOLLOW_UP, EMERGENCY,CONSULTATION
    }

    public enum AppointmentStatus {
        SCHEDULED,
        CHECKED_IN,
        CONSULTING,
        COMPLETED,
        CANCELLED,
        NO_SHOW,
        RESCHEDULED
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Helper Methods
    public void checkIn() {
        this.status = AppointmentStatus.CHECKED_IN;
        this.checkedInAt = LocalDateTime.now();
    }

    public void startConsultation() {
        this.status = AppointmentStatus.CONSULTING;
        this.consultationStartedAt = LocalDateTime.now();
    }

    public void completeConsultation() {
        this.status = AppointmentStatus.COMPLETED;
        this.consultationEndedAt = LocalDateTime.now();
    }

    public void cancel(String reason, String cancelledBy) {
        this.status = AppointmentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.cancelledBy = cancelledBy;
    }

    public void markNoShow() {
        this.status = AppointmentStatus.NO_SHOW;
    }

    public void reschedule() {
        this.status = AppointmentStatus.RESCHEDULED;
    }

    @Transient
    public boolean isActive() {
        return status == AppointmentStatus.SCHEDULED || 
               status == AppointmentStatus.CHECKED_IN || 
               status == AppointmentStatus.CONSULTING;
    }
}
