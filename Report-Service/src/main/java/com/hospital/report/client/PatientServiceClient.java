package com.hospital.report.client;

import com.hospital.report.dto.ApiResponse;
import com.hospital.report.dto.PatientDTO;
import com.hospital.report.dto.PatientVisitHistoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "PATIENT-SERVICE", path = "/patients")
public interface PatientServiceClient {

    @GetMapping("/active")
    ApiResponse<List<PatientDTO>> getAllActivePatients();

    @GetMapping("/{patientId}")
    ApiResponse<PatientDTO> getPatientById(@PathVariable("patientId") Long patientId);

    @GetMapping("/pin/{pinNumber}")
    ApiResponse<PatientDTO> getPatientByPIN(@PathVariable("pinNumber") String pinNumber);

    @GetMapping("/recent")
    ApiResponse<List<PatientDTO>> getRecentPatients(
            @RequestParam(value = "limit", defaultValue = "10") int limit
    );

    @GetMapping("/count")
    ApiResponse<Long> getTotalPatients();

    @GetMapping("/search")
    ApiResponse<List<PatientDTO>> searchPatients(
            @RequestParam("query") String query, // ✅ specify the query parameter name
            @RequestParam(value = "type", required = false, defaultValue = "NAME") String type
    );
}
