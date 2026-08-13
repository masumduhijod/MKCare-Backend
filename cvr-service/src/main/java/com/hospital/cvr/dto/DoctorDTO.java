package com.hospital.cvr.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DoctorDTO {
    private String doctorId;
    private String firstName;
    private String lastName;
    private String fullName;
    private BigDecimal consultationFee;
    private BigDecimal followUpFee;
    private Integer followUpDaysLimit;
}
