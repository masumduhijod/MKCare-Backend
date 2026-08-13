package com.hospital.cvr.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OpCaseDTO {
    private String opCaseNumber;
    private String cvrNumber;
    private LocalDate lastVisitDate;
    private java.time.LocalTime visitTime;
    private String doctorId;
    private String visitType;
    private String chiefComplaint;
    private Integer daysAgo;
}
