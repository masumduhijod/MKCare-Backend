package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientVisitHistoryDTO {
    private String pinNumber;
    private String patientName;
    private Integer totalVisits;
    private LocalDate lastVisitDate;
    private String lastCvrNumber;
    private List<CvrSummaryDTO> recentVisits;
}
