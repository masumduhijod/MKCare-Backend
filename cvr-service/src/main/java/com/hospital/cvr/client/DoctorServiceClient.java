package com.hospital.cvr.client;

import com.hospital.cvr.config.FeignClientConfig;
import com.hospital.cvr.dto.ApiResponse;
import com.hospital.cvr.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "DOCTOR-SERVICE",
    configuration = FeignClientConfig.class
)
public interface DoctorServiceClient {

    @GetMapping("/doctors/id/{doctorId}")
    ApiResponse<DoctorDTO> getDoctorById(@PathVariable("doctorId") String doctorId);
}
