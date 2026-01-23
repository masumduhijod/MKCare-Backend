/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.util;

/**
 *
 * @author mduhijod
 */

import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class DoctorIdGenerator {

    /**
     * Generate unique Doctor ID
     * Format: DOC + Year + Sequential Number (4 digits)
     * Example: DOC2025001, DOC2025002
     * 
     * @param lastDoctorId - Last generated Doctor ID from database
     * @return New Doctor ID
     */
    public String generateDoctorId(String lastDoctorId) {
        String currentYear = String.valueOf(Year.now().getValue());
        int nextNumber = 1;
        
        if (lastDoctorId != null && !lastDoctorId.isEmpty()) {
            try {
                // Extract year and number from last Doctor ID
                // Format: DOC2025001
                String lastYear = lastDoctorId.substring(3, 7); // Year
                String lastNumberStr = lastDoctorId.substring(7); // Sequential number
                
                // If same year, increment; otherwise start from 1
                if (lastYear.equals(currentYear)) {
                    nextNumber = Integer.parseInt(lastNumberStr) + 1;
                }
            } catch (Exception e) {
                // If parsing fails, start from 1
                nextNumber = 1;
            }
        }
        
        // Format: DOC + Year + 4-digit number (zero-padded)
        return String.format("DOC%s%04d", currentYear, nextNumber);
    }
    
    /**
     * Validate Doctor ID format
     * @param doctorId - Doctor ID to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidDoctorId(String doctorId) {
        if (doctorId == null || doctorId.isEmpty()) {
            return false;
        }
        
        // Doctor ID format: DOC + 4-digit year + 4-digit number = 11 characters
        if (doctorId.length() != 11) {
            return false;
        }
        
        // Must start with "DOC"
        if (!doctorId.startsWith("DOC")) {
            return false;
        }
        
        // Rest must be digits
        String numericPart = doctorId.substring(3);
        return numericPart.matches("\\d{8}");
    }
    
    /**
     * Extract year from Doctor ID
     * @param doctorId - Doctor ID
     * @return Year as string
     */
    public String extractYearFromDoctorId(String doctorId) {
        if (isValidDoctorId(doctorId)) {
            return doctorId.substring(3, 7);
        }
        return null;
    }
    
    /**
     * Extract sequence number from Doctor ID
     * @param doctorId - Doctor ID
     * @return Sequence number
     */
    public int extractSequenceFromDoctorId(String doctorId) {
        if (isValidDoctorId(doctorId)) {
            String numStr = doctorId.substring(7);
            return Integer.parseInt(numStr);
        }
        return -1;
    }
}