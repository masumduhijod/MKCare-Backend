package com.hospital.report.config;

import com.hospital.report.dto.ReportResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ReportResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Report generation failed: {}", ex.getMessage());
        ReportResponse<Void> response = ReportResponse.<Void>builder()
                .success(false)
                .reportName("ERROR")
                .data(null)
                .summary(ex.getMessage())
                .generatedAt(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ReportResponse<Void>> handleFeignException(FeignException ex) {
        log.error("Service call failed: {} - {}", ex.status(), ex.getMessage());
        String message = "Downstream service error: ";
        if (ex.status() == 404) {
            message += "Resource not found. Check the provided IDs/numbers.";
        } else if (ex.status() == 503) {
            message += "Service is currently unavailable. Ensure all microservices are running.";
        } else {
            message += ex.getMessage();
        }
        ReportResponse<Void> response = ReportResponse.<Void>builder()
                .success(false)
                .reportName("SERVICE_ERROR")
                .data(null)
                .summary(message)
                .generatedAt(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ReportResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        ReportResponse<Void> response = ReportResponse.<Void>builder()
                .success(false)
                .reportName("VALIDATION_ERROR")
                .data(null)
                .summary(ex.getMessage())
                .generatedAt(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
