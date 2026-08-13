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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @Autowired
    @Qualifier("masterJdbcTemplate")
    private JdbcTemplate masterJdbcTemplate;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("🔐 Login request - Clinic: {}, User: {}", request.getClinicId(), request.getUsername());

        try {
            // ⭐ CHECK: Validate subscription & active status from master DB directly
            String sql = "SELECT is_active, subscription_expiry FROM tenants WHERE tenant_id = ?";
            Map<String, Object> row;
            try {
                row = masterJdbcTemplate.queryForMap(sql, request.getClinicId());
            } catch (Exception ex) {
                throw new RuntimeException("Invalid Clinic ID: " + request.getClinicId());
            }

            Object isActiveObj = row.get("is_active");
            boolean isActive = (isActiveObj instanceof Boolean) ? (Boolean) isActiveObj
                    : (isActiveObj instanceof Number && ((Number) isActiveObj).intValue() == 1);
            if (!isActive) {
                log.warn("❌ Clinic {} is INACTIVE", request.getClinicId());
                throw new RuntimeException("Clinic is currently turned OFF. Please contact your Super Admin.");
            }

            Object expiryObj = row.get("subscription_expiry");
            if (expiryObj instanceof java.sql.Timestamp) {
                LocalDateTime expiry = ((java.sql.Timestamp) expiryObj).toLocalDateTime();
                if (expiry.isBefore(LocalDateTime.now())) {
                    String expiredAt = expiry.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
                    log.warn("❌ Clinic {} subscription expired at {}", request.getClinicId(), expiredAt);
                    throw new RuntimeException("Subscription Expired! Your clinic plan ended on " + expiredAt + ". Please contact your Super Admin.");
                }
            }

            // ⭐ Set tenant context BEFORE calling service
            TenantContext.setTenantId(request.getClinicId());
            log.info("✅ Tenant context set to: {}", request.getClinicId());

            LoginResponse response = userService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));

        } catch (Exception e) {
            log.error("❌ Login failed for user: {} at clinic: {}", request.getUsername(), request.getClinicId(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        } finally {
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