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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "prescription_id", unique = true, nullable = false, length = 20)
    private String prescriptionId;

//    @NotNull(message = "Consultation ID is required")
    @Column(name = "consultation_id", nullable = false)
    private String consultationId;

    @Column(name = "consultation_number", length = 20)
    private String consultationNumber;

//    @NotNull(message = "Patient ID is required")
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

//    @NotBlank(message = "PIN number is required")
    @Column(name = "pin_number", nullable = false, length = 20)
    private String pinNumber;

//    @NotBlank(message = "Doctor ID is required")
    @Column(name = "doctor_id", nullable = false, length = 20)
    private String doctorId;

    @Column(name = "prescription_date", nullable = false)
    private LocalDateTime prescriptionDate;

    @Column(name = "validity_days")
    private Integer validityDays = 30;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    // Enum
    public enum PrescriptionStatus {
        ACTIVE,
        DISPENSED,
        EXPIRED,
        CANCELLED
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (prescriptionDate == null) {
            prescriptionDate = LocalDateTime.now();
        }
        if (expiryDate == null && validityDays != null) {
            expiryDate = LocalDate.now().plusDays(validityDays);
        }
    }

    // Helper Methods
    public void addItem(PrescriptionItem item) {
        items.add(item);
        item.setPrescription(this);
    }

    public void removeItem(PrescriptionItem item) {
        items.remove(item);
        item.setPrescription(null);
    }

    public void markDispensed() {
        this.status = PrescriptionStatus.DISPENSED;
    }

    public void cancel() {
        this.status = PrescriptionStatus.CANCELLED;
    }

    @Transient
    public boolean isValid() {
        return status == PrescriptionStatus.ACTIVE && 
               expiryDate != null && 
               expiryDate.isAfter(LocalDate.now());
    }
}
