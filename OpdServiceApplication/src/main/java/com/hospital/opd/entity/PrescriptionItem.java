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
// ========== PrescriptionItem.java ==========

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "prescription_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    @JsonIgnore
    private Prescription prescription;

//    @NotBlank(message = "Medicine name is required")
    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

//    @NotBlank(message = "Dosage is required")
    @Column(name = "dosage", nullable = false, length = 100)
    private String dosage; // e.g., "500mg", "10ml"

//    @NotBlank(message = "Frequency is required")
    @Column(name = "frequency", nullable = false, length = 100)
    private String frequency; // e.g., "Twice daily", "Before meals"

//    @NotBlank(message = "Duration is required")
    @Column(name = "duration", nullable = false, length = 50)
    private String duration; // e.g., "7 days", "2 weeks"

//    @NotNull(message = "Quantity is required")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions; // Special instructions

    @Column(name = "morning")
    private Boolean morning = false;

    @Column(name = "afternoon")
    private Boolean afternoon = false;

    @Column(name = "evening")
    private Boolean evening = false;

    @Column(name = "night")
    private Boolean night = false;

    @Column(name = "before_food")
    private Boolean beforeFood = false;

    @Column(name = "after_food")
    private Boolean afterFood = true;

    // Helper method to get timing as string
    @Transient
    public String getTimingString() {
        List<String> timings = new ArrayList<>();
        if (morning) timings.add("Morning");
        if (afternoon) timings.add("Afternoon");
        if (evening) timings.add("Evening");
        if (night) timings.add("Night");
        return String.join(", ", timings);
    }

    @Transient
    public String getFoodInstructionString() {
        if (beforeFood) return "Before food";
        if (afterFood) return "After food";
        return "Any time";
    }
}