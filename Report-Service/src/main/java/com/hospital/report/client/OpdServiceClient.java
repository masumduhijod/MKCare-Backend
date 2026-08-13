package com.hospital.report.client;

import com.hospital.report.dto.ApiResponse;
import com.hospital.report.dto.ConsultationDTO;
import com.hospital.report.dto.PrescriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "OPD-SERVICE", path = "/opd")
public interface OpdServiceClient {

    @GetMapping("/consultations/patient/{pinNumber}")
    ApiResponse<List<ConsultationDTO>> getPatientConsultations(@PathVariable("pinNumber") String pinNumber);

    @GetMapping("/consultations/{consultationId}")
    ApiResponse<ConsultationDTO> getConsultation(@PathVariable("consultationId") String consultationId);

    @GetMapping("/consultations/by-doctor-date")
    ApiResponse<List<ConsultationDTO>> getConsultationsByDoctorAndDate(
            @RequestParam("doctorId") String doctorId,
            @RequestParam("date") String date);

    @GetMapping("/prescriptions/patient/{pinNumber}")
    ApiResponse<List<PrescriptionDTO>> getPatientPrescriptions(@PathVariable("pinNumber") String pinNumber);

    @GetMapping("/prescriptions/{prescriptionId}")
    ApiResponse<PrescriptionDTO> getPrescription(@PathVariable("prescriptionId") String prescriptionId);

    @GetMapping("/prescriptions/consultation/{consultationId}")
    ApiResponse<PrescriptionDTO> getPrescriptionByConsultation(@PathVariable("consultationId") String consultationId);

    @GetMapping("/prescriptions/by-date")
    ApiResponse<List<PrescriptionDTO>> getPrescriptionsByDate(
            @RequestParam("date") String date);
}
