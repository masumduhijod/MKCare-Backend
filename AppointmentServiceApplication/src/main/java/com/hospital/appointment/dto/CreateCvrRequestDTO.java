/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mduhijod
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCvrRequestDTO {

    private String pinNumber;
    private String visitType;
    private String chiefComplaint;
    private String symptoms;
    private String doctorId;

    private String appointmentId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    private String createdBy;
}
