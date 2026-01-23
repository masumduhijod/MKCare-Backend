/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.patient.util;

/**
 *
 * @author mduhijod
 */

import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class PinGenerator {

    /**
     * Generate unique PIN number for patient
     * Format: PIN + Year + 6-digit sequential number
     * Example: PIN2025000001, PIN2025000002
     * 
     * @param lastPinNumber - Last generated PIN from database
     * @return New PIN number
     */
    public String generatePIN(String lastPinNumber) {
        String currentYear = String.valueOf(Year.now().getValue());
        int nextNumber = 1;
        
        if (lastPinNumber != null && !lastPinNumber.isEmpty()) {
            try {
                // Extract year and number from last PIN
                // Format: PIN2025000001
                String lastYear = lastPinNumber.substring(3, 7);
                String lastNumberStr = lastPinNumber.substring(7);
                
                // If same year, increment; otherwise start from 1
                if (lastYear.equals(currentYear)) {
                    nextNumber = Integer.parseInt(lastNumberStr) + 1;
                }
            } catch (Exception e) {
                // If parsing fails, start from 1
                nextNumber = 1;
            }
        }
        
        // Format: PIN + Year + 6-digit number (zero-padded)
        return String.format("PIN%s%06d", currentYear, nextNumber);
    }
    
    /**
     * Validate PIN format
     * @param pin - PIN to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidPIN(String pin) {
        if (pin == null || pin.isEmpty()) {
            return false;
        }
        
        // PIN format: PIN + 4-digit year + 6-digit number = 13 characters
        if (pin.length() != 13) {
            return false;
        }
        
        // Must start with "PIN"
        if (!pin.startsWith("PIN")) {
            return false;
        }
        
        // Rest must be digits
        String numericPart = pin.substring(3);
        return numericPart.matches("\\d{10}");
    }
    
    /**
     * Extract year from PIN
     * @param pin - PIN number
     * @return Year as string
     */
    public String extractYearFromPIN(String pin) {
        if (isValidPIN(pin)) {
            return pin.substring(3, 7);
        }
        return null;
    }
    
    /**
     * Extract sequence number from PIN
     * @param pin - PIN number
     * @return Sequence number
     */
    public int extractSequenceFromPIN(String pin) {
        if (isValidPIN(pin)) {
            String numStr = pin.substring(7);
            return Integer.parseInt(numStr);
        }
        return -1;
    }
}
