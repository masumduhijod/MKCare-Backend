/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.entity;

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
import java.util.ArrayList;
import java.util.List;

//@Entity
//@Table(name = "case_visit_records")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class CaseVisitRecord {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "cvr_id")
//    private Long cvrId;
//
//    @Column(name = "cvr_number", unique = true, nullable = false, length = 20)
//    private String cvrNumber;
//
//    @NotNull(message = "Patient ID is required")
//    @Column(name = "patient_id", nullable = false)
//    private Long patientId;
//
//    @NotBlank(message = "PIN number is required")
//    @Column(name = "pin_number", nullable = false, length = 20)
//    private String pinNumber;
//
//    // Visit Details
//    @NotNull(message = "Visit date is required")
//    @Column(name = "visit_date", nullable = false)
//    private LocalDate visitDate;
//
//    @NotNull(message = "Visit time is required")
//    @Column(name = "visit_time", nullable = false)
//    private LocalTime visitTime;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "visit_type", nullable = false)
//    private VisitType visitType = VisitType.OPD;
//
//    @Column(name = "department", length = 100)
//    private String department;
//
//    @Column(name = "doctor_id", length = 20)
//    private String doctorId;
//
//    // Chief Complaint
//    @NotBlank(message = "Chief complaint is required")
//    @Column(name = "chief_complaint", columnDefinition = "TEXT", nullable = false)
//    private String chiefComplaint;
//
//    @Column(name = "symptoms", columnDefinition = "TEXT")
//    private String symptoms;
//
//    // Visit Status
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private CvrStatus status = CvrStatus.REGISTERED;
//
//    // Timestamps
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "created_by", length = 100)
//    private String createdBy;
//
//    @Column(name = "checked_in_at")
//    private LocalDateTime checkedInAt;
//
//    @Column(name = "consultation_started_at")
//    private LocalDateTime consultationStartedAt;
//
//    @Column(name = "consultation_completed_at")
//    private LocalDateTime consultationCompletedAt;
//
//    // Billing Link
//    @Column(name = "is_billed")
//    private Boolean isBilled = false;
//
//    @Column(name = "billing_id", length = 20)
//    private String billingId;
//
//    // Vitals (One-to-Many relationship)
//    @OneToMany(mappedBy = "caseVisitRecord", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CvrVitals> vitals = new ArrayList<>();
//
//    // Enums
//    public enum VisitType {
//        OPD, IPD, EMERGENCY, FOLLOW_UP
//    }
//
//    public enum CvrStatus {
//        REGISTERED,
//        APPOINTMENT_SCHEDULED,
//        CHECKED_IN,
//        CONSULTING,
//        COMPLETED,
//        CANCELLED
//    }
//
//    // Lifecycle Callbacks
//    @PrePersist
//    protected void onCreate() {
//        if (createdAt == null) {
//            createdAt = LocalDateTime.now();
//        }
//        if (visitDate == null) {
//            visitDate = LocalDate.now();
//        }
//        if (visitTime == null) {
//            visitTime = LocalTime.now();
//        }
//    }
//
//    // Helper Methods
//    public void addVitals(CvrVitals vital) {
//        vitals.add(vital);
//        vital.setCaseVisitRecord(this);
//    }
//
//    public void removeVitals(CvrVitals vital) {
//        vitals.remove(vital);
//        vital.setCaseVisitRecord(null);
//    }
//
//    // Check-in method
//    public void checkIn() {
//        this.status = CvrStatus.CHECKED_IN;
//        this.checkedInAt = LocalDateTime.now();
//    }
//
//    // Start consultation
//    public void startConsultation() {
//        this.status = CvrStatus.CONSULTING;
//        this.consultationStartedAt = LocalDateTime.now();
//    }
//
//    // Complete consultation
//    public void completeConsultation() {
//        this.status = CvrStatus.COMPLETED;
//        this.consultationCompletedAt = LocalDateTime.now();
//    }
//
//    // Cancel visit
//    public void cancel() {
//        this.status = CvrStatus.CANCELLED;
//    }
//}


/**
 * CVR Entity - UPDATED FOR CORRECT FLOW
 * Key Changes:
 * - Added appointment_id, appointment_date, appointment_time
 * - Made visit_date and visit_time NULLABLE
 * - visit_date/visit_time are set ONLY on check-in
 */
@Entity
@Table(name = "case_visit_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseVisitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cvr_id")
    private Long cvrId;

    @Column(name = "cvr_number", unique = true, nullable = false, length = 20)
    private String cvrNumber;

//    @NotNull(message = "Patient ID is required")
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

//    @NotBlank(message = "PIN number is required")
    @Column(name = "pin_number", nullable = false, length = 20)
    private String pinNumber;

    // ============================================
    // APPOINTMENT DETAILS (Scheduled)
    // ============================================
    @Column(name = "appointment_id", length = 20)
    private String appointmentId;

    @Column(name = "appointment_date")
    private LocalDate appointmentDate; // When appointment is scheduled

    @Column(name = "appointment_time")
    private LocalTime appointmentTime; // When appointment is scheduled

    // ============================================
    // VISIT DETAILS (Actual - NULL until check-in)
    // ============================================
    @Column(name = "visit_date")
    private LocalDate visitDate; // When patient actually visits (NULL = not visited yet)

    @Column(name = "visit_time")
    private LocalTime visitTime; // When patient actually visits (NULL = not visited yet)

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_type", nullable = false)
    private VisitType visitType = VisitType.OPD;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "doctor_id", length = 20)
    private String doctorId;

    // Chief Complaint
//    @NotBlank(message = "Chief complaint is required")
    @Column(name = "chief_complaint", columnDefinition = "TEXT", nullable = false)
    private String chiefComplaint;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    // Visit Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CvrStatus status = CvrStatus.REGISTERED;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    @Column(name = "consultation_started_at")
    private LocalDateTime consultationStartedAt;

    @Column(name = "consultation_completed_at")
    private LocalDateTime consultationCompletedAt;

    // Billing Link
    @Column(name = "is_billed")
    private Boolean isBilled = false;

    @Column(name = "billing_id", length = 20)
    private String billingId;

    // Vitals (One-to-Many relationship)
    @OneToMany(mappedBy = "caseVisitRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CvrVitals> vitals = new ArrayList<>();

    // Enums
    public enum VisitType {
        OPD, IPD, EMERGENCY, FOLLOW_UP
    }

    public enum CvrStatus {
        REGISTERED,           // CVR created, no appointment yet
        APPOINTMENT_SCHEDULED, // Appointment booked, patient not arrived
        CHECKED_IN,           // Patient arrived at OPD
        CONSULTING,           // Doctor is examining
        COMPLETED,            // Visit completed
        CANCELLED             // Cancelled
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        // *** IMPORTANT: Do NOT set visit_date/visit_time here ***
        // They are set ONLY when patient checks in
    }

    // Helper Methods
    public void addVitals(CvrVitals vital) {
        vitals.add(vital);
        vital.setCaseVisitRecord(this);
    }

    public void removeVitals(CvrVitals vital) {
        vitals.remove(vital);
        vital.setCaseVisitRecord(null);
    }

    /**
     * Check-in method - THIS IS WHERE VISIT DATE/TIME ARE SET
     */
    public void checkIn() {
        this.status = CvrStatus.CHECKED_IN;
        this.checkedInAt = LocalDateTime.now();
        
        // *** SET VISIT DATE/TIME WHEN PATIENT ACTUALLY ARRIVES ***
        this.visitDate = LocalDate.now();
        this.visitTime = LocalTime.now();
    }

    public void startConsultation() {
        this.status = CvrStatus.CONSULTING;
        this.consultationStartedAt = LocalDateTime.now();
    }

    public void completeConsultation() {
        this.status = CvrStatus.COMPLETED;
        this.consultationCompletedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = CvrStatus.CANCELLED;
    }

    /**
     * Check if patient has visited (check-in done)
     */
    @Transient
    public boolean hasVisited() {
        return visitDate != null && visitTime != null;
    }

    /**
     * Check if appointment is pending (booked but not visited)
     */
    @Transient
    public boolean isPending() {
        return status == CvrStatus.APPOINTMENT_SCHEDULED && !hasVisited();
    }
}