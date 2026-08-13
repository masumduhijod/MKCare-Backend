/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.cvr.util;

/**
 *
 * @author mduhijod
 */

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CvrNumberGenerator {

    /**
     * Generate unique CVR number
     * Format: CVR + YYYYMMDD + Sequential Number (3 digits)
     * Example: CVR20251030001, CVR20251030002
     * 
     * @param lastCvrNumber - Last generated CVR from database
     * @param visitDate - Date of visit
     * @return New CVR number
     */
    public String generateCVR(String lastCvrNumber, LocalDate visitDate) {
        // Format date as YYYYMMDD
        String dateStr = visitDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        int nextNumber = 1;
        
        if (lastCvrNumber != null && !lastCvrNumber.isEmpty()) {
            try {
                // Extract date and number from last CVR
                // Format: CVR20251030001
                // Position: 012345678901234
                String lastDate = lastCvrNumber.substring(3, 11); // YYYYMMDD
                String lastNumberStr = lastCvrNumber.substring(11); // Sequential number
                
                // If same date, increment; otherwise start from 1
                if (lastDate.equals(dateStr)) {
                    nextNumber = Integer.parseInt(lastNumberStr) + 1;
                }
            } catch (Exception e) {
                // If parsing fails, start from 1
                nextNumber = 1;
            }
        }
        
        // Format: CVR + Date(8 digits) + Number(3 digits zero-padded)
        return String.format("CVR%s%03d", dateStr, nextNumber);
    }
    
    /**
     * Validate CVR format
     * @param cvrNumber - CVR to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidCVR(String cvrNumber) {
        if (cvrNumber == null || cvrNumber.isEmpty()) {
            return false;
        }
        
        // CVR format: CVR + 8-digit date + 3-digit number = 14 characters
        if (cvrNumber.length() != 14) {
            return false;
        }
        
        // Must start with "CVR"
        if (!cvrNumber.startsWith("CVR")) {
            return false;
        }
        
        // Rest must be digits
        String numericPart = cvrNumber.substring(3);
        return numericPart.matches("\\d{11}");
    }
    
    /**
     * Extract date from CVR number
     * @param cvrNumber - CVR number
     * @return Date as LocalDate
     */
    public LocalDate extractDateFromCVR(String cvrNumber) {
        if (isValidCVR(cvrNumber)) {
            String dateStr = cvrNumber.substring(3, 11); // YYYYMMDD
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        return null;
    }
    
    /**
     * Extract sequence number from CVR
     * @param cvrNumber - CVR number
     * @return Sequence number
     */
    public int extractSequenceFromCVR(String cvrNumber) {
        if (isValidCVR(cvrNumber)) {
            String numStr = cvrNumber.substring(11);
            return Integer.parseInt(numStr);
        }
        return -1;
    }
    
    /**
     * Generate CVR for today's date
     * @param lastCvrNumber - Last CVR number
     * @return New CVR for today
     */
    public String generateCVRForToday(String lastCvrNumber) {
        return generateCVR(lastCvrNumber, LocalDate.now());
    }

    /**
     * Generate unique OP Case number
     * Format: OPC + YYYYMMDD + Sequential Number (3 digits)
     */
    public String generateOpCase(String lastOpCaseNumber, LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int nextNumber = 1;
        if (lastOpCaseNumber != null && !lastOpCaseNumber.isEmpty()) {
            try {
                String lastDate = lastOpCaseNumber.substring(3, 11);
                String lastNumberStr = lastOpCaseNumber.substring(11);
                if (lastDate.equals(dateStr)) {
                    nextNumber = Integer.parseInt(lastNumberStr) + 1;
                }
            } catch (Exception e) {
                nextNumber = 1;
            }
        }
        return String.format("OPC%s%03d", dateStr, nextNumber);
    }
}