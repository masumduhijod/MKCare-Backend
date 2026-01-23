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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cvr_vitals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvrVitals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vital_id")
    private Long vitalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cvr_id", nullable = false)
    @JsonIgnore
    private CaseVisitRecord caseVisitRecord;

    // Vital Signs
    @Column(name = "temperature_f", precision = 4, scale = 2)
    private BigDecimal temperatureF;

    @Column(name = "blood_pressure_systolic")
    private Integer bloodPressureSystolic;

    @Column(name = "blood_pressure_diastolic")
    private Integer bloodPressureDiastolic;

    @Column(name = "pulse_rate")
    private Integer pulseRate;

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;

    @Column(name = "spo2_percentage")
    private Integer spo2Percentage;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "height_cm", precision = 5, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "bmi", precision = 5, scale = 2)
    private BigDecimal bmi;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "recorded_by", length = 100)
    private String recordedBy;

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
        calculateBMI();
    }

    @PreUpdate
    protected void onUpdate() {
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

    // Helper method to get blood pressure as string
    @Transient
    public String getBloodPressure() {
        if (bloodPressureSystolic != null && bloodPressureDiastolic != null) {
            return bloodPressureSystolic + "/" + bloodPressureDiastolic;
        }
        return null;
    }

    // Set blood pressure from string
    public void setBloodPressure(String bp) {
        if (bp != null && bp.contains("/")) {
            String[] parts = bp.split("/");
            this.bloodPressureSystolic = Integer.parseInt(parts[0].trim());
            this.bloodPressureDiastolic = Integer.parseInt(parts[1].trim());
        }
    }
}
