package com.hospital.report.client;

import com.hospital.report.dto.ApiResponse;
import com.hospital.report.dto.AppointmentDTO;
import com.hospital.report.dto.AppointmentSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "APPOINTMENT-SERVICE", path = "/appointments")
public interface AppointmentServiceClient {

    @GetMapping("/today")
    ApiResponse<List<AppointmentSummaryDTO>> getTodaysAppointments();

    @GetMapping("/range")
    ApiResponse<List<AppointmentSummaryDTO>> getAppointmentsByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate);

    @GetMapping("/doctor/{doctorId}/date/{date}")
    ApiResponse<List<AppointmentSummaryDTO>> getDoctorAppointments(
            @PathVariable("doctorId") String doctorId,
            @PathVariable("date") String date);

    @GetMapping("/patient/{pinNumber}")
    ApiResponse<List<AppointmentDTO>> getPatientAppointments(@PathVariable("pinNumber") String pinNumber);

    @GetMapping("/patient/{pinNumber}/upcoming")
    ApiResponse<List<AppointmentDTO>> getUpcomingAppointments(@PathVariable("pinNumber") String pinNumber);

    @GetMapping("/status/{status}")
    ApiResponse<List<AppointmentDTO>> getAppointmentsByStatus(@PathVariable("status") String status);

    @GetMapping("/search")
    ApiResponse<List<AppointmentSummaryDTO>> searchAppointments(@RequestParam String query);

    @GetMapping("/{appointmentId}")
    ApiResponse<AppointmentDTO> getAppointmentById(@PathVariable("appointmentId") String appointmentId);
}
