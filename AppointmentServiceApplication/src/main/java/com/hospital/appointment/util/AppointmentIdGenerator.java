/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.util;

/**
 *
 * @author mduhijod
 */

import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class AppointmentIdGenerator {

    /**
     * Generate unique Appointment ID
     * Format: APT + Year + Sequential Number (6 digits)
     * Example: APT2025000001, APT2025000002
     * 
     * @param lastAppointmentId - Last generated Appointment ID from database
     * @return New Appointment ID
     */
    public String generateAppointmentId(String lastAppointmentId) {
        String currentYear = String.valueOf(Year.now().getValue());
        int nextNumber = 1;
        
        if (lastAppointmentId != null && !lastAppointmentId.isEmpty()) {
            try {
                // Extract year and number from last Appointment ID
                // Format: APT2025000001
                String lastYear = lastAppointmentId.substring(3, 7);
                String lastNumberStr = lastAppointmentId.substring(7);
                
                // If same year, increment; otherwise start from 1
                if (lastYear.equals(currentYear)) {
                    nextNumber = Integer.parseInt(lastNumberStr) + 1;
                }
            } catch (Exception e) {
                // If parsing fails, start from 1
                nextNumber = 1;
            }
        }
        
        // Format: APT + Year + 6-digit number (zero-padded)
        return String.format("APT%s%06d", currentYear, nextNumber);
    }
    
    /**
     * Validate Appointment ID format
     * @param appointmentId - Appointment ID to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidAppointmentId(String appointmentId) {
        if (appointmentId == null || appointmentId.isEmpty()) {
            return false;
        }
        
        // Appointment ID format: APT + 4-digit year + 6-digit number = 13 characters
        if (appointmentId.length() != 13) {
            return false;
        }
        
        // Must start with "APT"
        if (!appointmentId.startsWith("APT")) {
            return false;
        }
        
        // Rest must be digits
        String numericPart = appointmentId.substring(3);
        return numericPart.matches("\\d{10}");
    }
}
