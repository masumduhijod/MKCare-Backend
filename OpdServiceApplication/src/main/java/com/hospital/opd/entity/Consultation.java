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
@Table(name = "consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "consultation_id", unique = true, nullable = false, length = 20)
    private String consultationId;

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

    @Column(name = "consultation_date", nullable = false)
    private LocalDateTime consultationDate;

    // SOAP Notes (Subjective, Objective, Assessment, Plan)
    @Column(name = "subjective", columnDefinition = "TEXT")
    private String subjective; // Patient's complaint in their words

    @Column(name = "objective", columnDefinition = "TEXT")
    private String objective; // Doctor's examination findings

    @Column(name = "assessment", columnDefinition = "TEXT")
    private String assessment; // Diagnosis

    @Column(name = "plan", columnDefinition = "TEXT")
    private String plan; // Treatment plan

    // Clinical Details
//    @NotBlank(message = "Chief complaint is required")
    @Column(name = "chief_complaint", columnDefinition = "TEXT", nullable = false)
    private String chiefComplaint;

    @Column(name = "present_illness", columnDefinition = "TEXT")
    private String presentIllness;

    @Column(name = "examination_findings", columnDefinition = "TEXT")
    private String examinationFindings;

//    @NotBlank(message = "Diagnosis is required")
    @Column(name = "diagnosis", columnDefinition = "TEXT", nullable = false)
    private String diagnosis;

    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    // Vitals (stored as JSON or separate fields)
    @Column(name = "vitals_recorded", columnDefinition = "JSON")
    private String vitalsRecorded;

    // Follow-up
    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "follow_up_instructions", columnDefinition = "TEXT")
    private String followUpInstructions;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConsultationStatus status = ConsultationStatus.IN_PROGRESS;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Enum
    public enum ConsultationStatus {
        IN_PROGRESS,
        COMPLETED
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (consultationDate == null) {
            consultationDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper Methods
    public void complete() {
        this.status = ConsultationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    @Transient
    public boolean isCompleted() {
        return status == ConsultationStatus.COMPLETED;
    }
}
