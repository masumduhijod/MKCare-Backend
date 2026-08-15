package com.hospital.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.user.dto.ClinicDTO;
import com.hospital.user.dto.LoginResponse;
import com.hospital.user.dto.SuperAdminLoginRequest;
import com.hospital.user.service.SuperAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SuperAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SuperAdminService superAdminService;

    private ClinicDTO sampleClinic;

    @BeforeEach
    void setUp() {
        sampleClinic = new ClinicDTO();
        sampleClinic.setTenantId("SC001");
        sampleClinic.setClinicCode("sai");
        sampleClinic.setClinicName("Sai Clinic");
        sampleClinic.setDbName("clinic_sai");
        sampleClinic.setPhone("9876543210");
        sampleClinic.setActive(true);
    }

    @Test
    @DisplayName("POST /auth/superadmin/login - SuperAdmin login success (200 OK)")
    void testSuperAdminLoginSuccess() throws Exception {
        SuperAdminLoginRequest req = new SuperAdminLoginRequest();
        req.setUsername("superadmin");
        req.setPassword("Pass@123");

        LoginResponse res = new LoginResponse();
        res.setToken("superadmin-jwt-token");
        res.setUsername("superadmin");
        res.setRole("SUPER_ADMIN");

        Mockito.when(superAdminService.superAdminLogin(any(SuperAdminLoginRequest.class))).thenReturn(res);

        mockMvc.perform(post("/auth/superadmin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("superadmin-jwt-token"));
    }

    @Test
    @DisplayName("GET /superadmin/clinics - List all clinics")
    void testGetAllClinics() throws Exception {
        Mockito.when(superAdminService.getAllClinics()).thenReturn(Collections.singletonList(sampleClinic));

        mockMvc.perform(get("/superadmin/clinics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].clinicName").value("Sai Clinic"))
                .andExpect(jsonPath("$.data[0].tenantId").value("SC001"));
    }

    @Test
    @DisplayName("POST /superadmin/clinics - Create clinic (201 Created)")
    void testCreateClinic() throws Exception {
        Mockito.when(superAdminService.createClinic(any(ClinicDTO.class))).thenReturn(sampleClinic);

        mockMvc.perform(post("/superadmin/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClinic)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.clinicName").value("Sai Clinic"));
    }

    @Test
    @DisplayName("PUT /superadmin/clinics/{tenantId} - Update clinic & logo (200 OK)")
    void testUpdateClinic() throws Exception {
        sampleClinic.setLogoPath("data:image/png;base64,sampleBase64LogoData");
        Mockito.when(superAdminService.updateClinic(eq("SC001"), any(ClinicDTO.class))).thenReturn(sampleClinic);

        mockMvc.perform(put("/superadmin/clinics/SC001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClinic)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.logoPath").value("data:image/png;base64,sampleBase64LogoData"));
    }

    @Test
    @DisplayName("GET /auth/validate-clinic - Validate active clinic")
    void testValidateClinicSuccess() throws Exception {
        Mockito.when(superAdminService.getClinicByTenantId("SC001")).thenReturn(sampleClinic);

        mockMvc.perform(get("/auth/validate-clinic").param("tenantId", "SC001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.clinicName").value("Sai Clinic"));
    }
}
