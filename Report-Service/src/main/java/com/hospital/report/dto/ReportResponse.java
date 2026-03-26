package com.hospital.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse<T> {
    private boolean success;
    private String reportName;
    private String reportType;
    private Object filters;
    private T data;
    private Object summary;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime generatedAt;
    private String generatedBy;

    public static <T> ReportResponse<T> success(String reportName, T data) {
        return ReportResponse.<T>builder()
                .success(true)
                .reportName(reportName)
                .data(data)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    public static <T> ReportResponse<T> success(String reportName, T data, Object summary) {
        return ReportResponse.<T>builder()
                .success(true)
                .reportName(reportName)
                .data(data)
                .summary(summary)
                .generatedAt(LocalDateTime.now())
                .build();
    }
}
