package com.hospital.report.client;

import com.hospital.report.dto.ApiResponse;
import com.hospital.report.dto.CvrDTO;
import com.hospital.report.dto.CvrSummaryDTO;
import com.hospital.report.dto.PatientVisitHistoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "CVR-SERVICE", path = "/cvr")
public interface CvrServiceClient {

    @GetMapping("/today")
    ApiResponse<List<CvrSummaryDTO>> getTodaysCVRs();

    @GetMapping("/date/{date}")
    ApiResponse<List<CvrSummaryDTO>> getCVRsByDate(@PathVariable("date") String date);

    @GetMapping("/doctor/{doctorId}/date/{date}")
    ApiResponse<List<CvrSummaryDTO>> getCVRsByDoctorAndDate(
            @PathVariable("doctorId") String doctorId,
            @PathVariable("date") String date);

    @GetMapping("/patient/{pinNumber}/history")
    ApiResponse<PatientVisitHistoryDTO> getPatientHistory(@PathVariable("pinNumber") String pinNumber);

    @GetMapping("/patient/{pinNumber}/count")
    ApiResponse<Long> countPatientVisits(@PathVariable("pinNumber") String pinNumber);

    @GetMapping("/recent")
    ApiResponse<List<CvrSummaryDTO>> getRecentCVRs(
            @RequestParam(value = "limit", defaultValue = "10") int limit);

    @GetMapping("/search")
    ApiResponse<List<CvrSummaryDTO>> searchCVRs(
            @RequestParam("query") String query);

    @GetMapping("/{cvrNumber}")
    ApiResponse<CvrDTO> getCVRByNumber(@PathVariable("cvrNumber") String cvrNumber);

    @GetMapping("/by-appointment/{appointmentId}")
    ApiResponse<CvrDTO> getCVRByAppointment(@PathVariable("appointmentId") String appointmentId);
}