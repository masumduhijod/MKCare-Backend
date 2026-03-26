package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorScheduleDTO {
    private Long scheduleId;
    private String doctorId;
    private LocalDate scheduleDate;
    private String startTime;
    private String endTime;
    private Integer slotDurationMinutes;
    private Integer maxPatientsPerSlot;
    private Boolean isActive;
    private Integer totalSlots;
    private String dayName;
}
