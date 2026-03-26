package com.hospital.report.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class OpdDailyReportDTO {
    private LocalDate reportDate;
    private Integer totalPatients;
    private Integer totalVisits;
    private Integer newPatients;
    private Integer revisitPatients;
    private Integer pendingCVRs;
    private Integer completedCVRs;
    private Integer cancelledCVRs;
    private Map<String, Long> departmentWiseCount;
    private Map<String, Long> statusWiseCount;
    private List<CvrSummaryDTO> cvrList;
}
