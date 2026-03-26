/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.user.controller;

/**
 *
 * @author mduhijod
 */
// ========== AuthController ==========


import com.hospital.user.config.TenantContext;
import com.hospital.user.dto.*;
import com.hospital.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("🔐 Login request - Clinic: {}, User: {}", request.getClinicId(), request.getUsername());
        
        try {
            // ⭐⭐⭐ CRITICAL: Set tenant context BEFORE calling service
            TenantContext.setTenantId(request.getClinicId());
            log.info("✅ Tenant context set to: {}", request.getClinicId());
            
            // Now service can access correct database
            LoginResponse response = userService.login(request);
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
            
        } catch (Exception e) {
            log.error("❌ Login failed for user: {} at clinic: {}", request.getUsername(), request.getClinicId(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        } finally {
            // ⭐ Always clear context after request
            TenantContext.clear();
            log.debug("🧹 Tenant context cleared");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody UserRegistrationDTO dto) {
        log.info("📝 Register user: {}", dto.getUsername());
        try {
            UserDTO user = userService.registerUser(dto);
            return new ResponseEntity<>(ApiResponse.success("User registered successfully", user), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("❌ Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            boolean valid = userService.validateToken(jwt);
            return ResponseEntity.ok(ApiResponse.success(valid ? "Token is valid" : "Token is invalid", valid));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("Token is invalid", false));
        }
    }
}