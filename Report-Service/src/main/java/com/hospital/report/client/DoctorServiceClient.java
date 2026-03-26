package com.hospital.report.client;

import com.hospital.report.dto.ApiResponse;
import com.hospital.report.dto.DoctorDTO;
import com.hospital.report.dto.DoctorScheduleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "DOCTOR-SERVICE", path = "/doctors")
public interface DoctorServiceClient {

    @GetMapping("/active")
    ApiResponse<List<DoctorDTO>> getAllActiveDoctors();

    @GetMapping("/available")
    ApiResponse<List<DoctorDTO>> getAvailableDoctors();

    @GetMapping("/{doctorId}")
    ApiResponse<DoctorDTO> getDoctorById(@PathVariable("doctorId") String doctorId);

    @GetMapping("/{doctorId}/schedules")
    ApiResponse<List<DoctorScheduleDTO>> getDoctorSchedules(@PathVariable("doctorId") String doctorId);

    @GetMapping("/{doctorId}/schedules/{scheduleDate}")
    ApiResponse<DoctorScheduleDTO> getScheduleByDate(
            @PathVariable("doctorId") String doctorId,
            @PathVariable("scheduleDate") String scheduleDate);

    @GetMapping("/{doctorId}/schedules/range")
    ApiResponse<List<DoctorScheduleDTO>> getSchedulesByDateRange(
            @PathVariable("doctorId") String doctorId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate);

    @GetMapping("/{doctorId}/schedules/upcoming")
    ApiResponse<List<DoctorScheduleDTO>> getUpcomingSchedules(
            @PathVariable("doctorId") String doctorId,
            @RequestParam(defaultValue = "30") int days);

    @GetMapping("/department/{department}")
    ApiResponse<List<DoctorDTO>> getDoctorsByDepartment(@PathVariable("department") String department);

    @GetMapping("/specialization/{specialization}")
    ApiResponse<List<DoctorDTO>> getDoctorsBySpecialization(@PathVariable("specialization") String specialization);

    @GetMapping("/count")
    ApiResponse<Long> getTotalDoctors();

    @GetMapping("/departments")
    ApiResponse<List<String>> getAllDepartments();

    @GetMapping("/specializations")
    ApiResponse<List<String>> getAllSpecializations();
}
