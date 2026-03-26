package com.hospital.report.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DoctorConsultationReportDTO {
    private String doctorId;
    private String doctorName;
    private String specialization;
    private String department;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer totalConsultations;
    private Integer completedConsultations;
    private Integer cancelledConsultations;
    private Integer noShowConsultations;
    private Integer followUpRequired;
    private BigDecimal totalRevenue;
    private List<ConsultationDTO> consultations;
    private List<AppointmentDTO> appointments;
}
