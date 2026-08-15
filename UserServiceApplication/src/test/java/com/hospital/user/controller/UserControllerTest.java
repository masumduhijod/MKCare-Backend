package com.hospital.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.user.dto.ChangePasswordDTO;
import com.hospital.user.dto.ResetPasswordDTO;
import com.hospital.user.dto.UserDTO;
import com.hospital.user.dto.UserUpdateDTO;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDTO sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new UserDTO();
        sampleUser.setUserId(3L);
        sampleUser.setUsername("doctor");
        sampleUser.setEmail("doctor@saiclinic.com");
        sampleUser.setRole("DOCTOR");
        sampleUser.setFirstName("Rajesh");
        sampleUser.setLastName("Verma");
        sampleUser.setContactNumber("9876543210");
    }

    @Test
    @DisplayName("GET /users - Get all users (200 OK)")
    void testGetAllUsers() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(sampleUser));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].username").value("doctor"))
                .andExpect(jsonPath("$.data[0].role").value("DOCTOR"));
    }

    @Test
    @DisplayName("GET /users/{username} - Get user by username (200 OK)")
    void testGetUserByUsername() throws Exception {
        Mockito.when(userService.getUserByUsername("doctor")).thenReturn(sampleUser);

        mockMvc.perform(get("/users/doctor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("doctor"))
                .andExpect(jsonPath("$.data.email").value("doctor@saiclinic.com"));
    }

    @Test
    @DisplayName("GET /users/roles - Fetch available system roles")
    void testGetRoles() throws Exception {
        Mockito.when(userService.getRoles()).thenReturn(Arrays.asList("ADMIN", "DOCTOR", "RECEPTIONIST"));

        mockMvc.perform(get("/users/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0]").value("ADMIN"));
    }

    @Test
    @DisplayName("GET /users/modules - Fetch system modules")
    void testGetModules() throws Exception {
        Map<String, Object> moduleMap = new HashMap<>();
        moduleMap.put("module_code", "BILLING");
        moduleMap.put("module_name", "Billing Module");

        Mockito.when(userService.getModules()).thenReturn(Collections.singletonList(moduleMap));

        mockMvc.perform(get("/users/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].module_code").value("BILLING"));
    }

    @Test
    @DisplayName("PUT /users/{username} - Update User Details")
    void testUpdateUser() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setFirstName("Rajesh");
        updateDTO.setLastName("Sharma");
        updateDTO.setEmail("rajesh.sharma@saiclinic.com");
        updateDTO.setContactNumber("9876543210");
        updateDTO.setRole("DOCTOR");

        Mockito.when(userService.updateUser(eq("doctor"), any(UserUpdateDTO.class))).thenReturn(sampleUser);

        mockMvc.perform(put("/users/doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("DELETE /users/{username} - Prevent deleting admin user (400 Bad Request)")
    void testDeleteAdminUserForbidden() throws Exception {
        mockMvc.perform(delete("/users/admin"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Cannot delete admin user"));
    }

    @Test
    @DisplayName("DELETE /users/{username} - Delete standard user (200 OK)")
    void testDeleteStandardUserSuccess() throws Exception {
        Mockito.doNothing().when(userService).deleteUser("receptionist");

        mockMvc.perform(delete("/users/receptionist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("receptionist"));
    }
}
