package com.hospital.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.user.dto.ApiResponse;
import com.hospital.user.dto.LoginRequest;
import com.hospital.user.dto.LoginResponse;
import com.hospital.user.service.UserService;
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

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private LoginRequest validLoginRequest;
    private LoginResponse mockLoginResponse;

    @BeforeEach
    void setUp() {
        validLoginRequest = new LoginRequest();
        validLoginRequest.setClinicId("SC001");
        validLoginRequest.setUsername("doctor");
        validLoginRequest.setPassword("Pass@123");

        mockLoginResponse = new LoginResponse();
        mockLoginResponse.setToken("mock-jwt-token-12345");
        mockLoginResponse.setUsername("doctor");
        mockLoginResponse.setRole("DOCTOR");
        mockLoginResponse.setTenantId("SC001");
        mockLoginResponse.setFullName("Dr. Rajesh Verma");
        mockLoginResponse.setPermissions(Arrays.asList("DASHBOARD", "PATIENT_LIST", "OPD_CONSULT", "BILLING"));
    }

    @Test
    @DisplayName("POST /auth/login - Success (200 OK)")
    void testLoginSuccess() throws Exception {
        Mockito.when(userService.login(any(LoginRequest.class))).thenReturn(mockLoginResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token-12345"))
                .andExpect(jsonPath("$.data.username").value("doctor"))
                .andExpect(jsonPath("$.data.role").value("DOCTOR"))
                .andExpect(jsonPath("$.data.tenantId").value("SC001"));
    }

    @Test
    @DisplayName("POST /auth/login - Bad Request when Clinic ID is missing (400 Bad Request)")
    void testLoginMissingClinicId() throws Exception {
        LoginRequest invalidReq = new LoginRequest();
        invalidReq.setUsername("doctor");
        invalidReq.setPassword("Pass@123");
        // clinicId is null

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login - Invalid Credentials Throws Exception (400/500)")
    void testLoginInvalidCredentials() throws Exception {
        Mockito.when(userService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid username or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
