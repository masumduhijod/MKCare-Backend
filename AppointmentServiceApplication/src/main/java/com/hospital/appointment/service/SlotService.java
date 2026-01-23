/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.service;

/**
 *
 * @author mduhijod
 */

import com.hospital.appointment.dto.AvailabilityCheckDTO;
import com.hospital.appointment.dto.SlotDTO;

import java.time.LocalDate;
import java.util.List;

public interface SlotService {

    /**
     * Generate slots for doctor on specific date
     * @param doctorId - Doctor ID
     * @param date - Date for slot generation
     * @return List of generated slots
     */
    List<SlotDTO> generateSlots(String doctorId, LocalDate date);

    /**
     * Get available slots for doctor on date
     * @param doctorId - Doctor ID
     * @param date - Date
     * @return List of available slots
     */
    List<SlotDTO> getAvailableSlots(String doctorId, LocalDate date);

    /**
     * Get all slots for doctor on date
     * @param doctorId - Doctor ID
     * @param date - Date
     * @return List of all slots
     */
    List<SlotDTO> getAllSlots(String doctorId, LocalDate date);

    /**
     * Check availability for doctor on date
     * @param doctorId - Doctor ID
     * @param date - Date
     * @return Availability details
     */
    AvailabilityCheckDTO checkAvailability(String doctorId, LocalDate date);

    /**
     * Book a slot
     * @param slotId - Slot ID
     * @param appointmentId - Appointment ID
     * @return Updated slot
     */
    SlotDTO bookSlot(Long slotId, String appointmentId);

    /**
     * Release a slot (on cancellation)
     * @param slotId - Slot ID
     * @return Updated slot
     */
    SlotDTO releaseSlot(Long slotId);

    /**
     * Mark slot as unavailable
     * @param slotId - Slot ID
     * @return Updated slot
     */
    SlotDTO markSlotUnavailable(Long slotId);
}
