/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.entity;

/**
 *
 * @author mduhijod
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_medical_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientMedicalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Known Conditions (stored as JSON strings)
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies; // JSON array: ["Penicillin", "Peanuts"]

    @Column(name = "chronic_diseases", columnDefinition = "TEXT")
    private String chronicDiseases; // JSON array: ["Diabetes", "Hypertension"]

    @Column(name = "past_surgeries", columnDefinition = "TEXT")
    private String pastSurgeries; // JSON array: ["Appendectomy - 2015"]

    @Column(name = "family_history", columnDefinition = "TEXT")
    private String familyHistory; // JSON array: ["Father - Heart Disease"]

    // Habits
    @Enumerated(EnumType.STRING)
    @Column(name = "smoking_status")
    private SmokingStatus smokingStatus = SmokingStatus.NEVER;

    @Enumerated(EnumType.STRING)
    @Column(name = "alcohol_consumption")
    private AlcoholConsumption alcoholConsumption = AlcoholConsumption.NEVER;

    // Physical Metrics (Latest)
    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure; // e.g., "120/80"

    @Column(name = "height_cm", precision = 5, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "bmi", precision = 5, scale = 2)
    private BigDecimal bmi;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Enums
    public enum SmokingStatus {
        NEVER, FORMER, CURRENT
    }

    public enum AlcoholConsumption {
        NEVER, OCCASIONAL, REGULAR
    }

    // Lifecycle Callbacks
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
        calculateBMI();
    }

    // Calculate BMI
    private void calculateBMI() {
        if (heightCm != null && weightKg != null && 
            heightCm.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightInMeters = heightCm.divide(
                new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP
            );
            bmi = weightKg.divide(
                heightInMeters.multiply(heightInMeters), 
                2, BigDecimal.ROUND_HALF_UP
            );
        }
    }
}