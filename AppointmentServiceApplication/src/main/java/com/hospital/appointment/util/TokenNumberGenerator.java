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

/**
 * Token Number Generator for Queue Management
 * Token numbers are doctor-specific and reset daily
 */
@Component
public class TokenNumberGenerator {

    /**
     * Generate token number for appointment
     * Token is doctor-specific and resets daily
     * 
     * @param lastTokenNumber - Last token number for the doctor on this date
     * @return Next token number
     */
    public int generateTokenNumber(Integer lastTokenNumber) {
        if (lastTokenNumber == null) {
            return 1;
        }
        return lastTokenNumber + 1;
    }
    
    /**
     * Reset token counter (called at start of day)
     * @return Starting token number
     */
    public int resetTokenCounter() {
        return 1;
    }
}
