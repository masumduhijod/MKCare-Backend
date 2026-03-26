package com.hospital.user.controller;

import com.hospital.user.dto.*;
import com.hospital.user.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Super Admin Controller
 * Handles super admin login, clinic CRUD, and admin user creation
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    // =====================================================================
    // 1. SUPER ADMIN LOGIN
    // =====================================================================
    @PostMapping("/auth/superadmin/login")
    public ResponseEntity<ApiResponse<LoginResponse>> superAdminLogin(
            @Valid @RequestBody SuperAdminLoginRequest request) {
        log.info("🔐 Super Admin login request: {}", request.getUsername());
        try {
            LoginResponse response = superAdminService.superAdminLogin(request);
            return ResponseEntity.ok(ApiResponse.success("Super Admin login successful", response));
        } catch (Exception e) {
            log.error("❌ Super Admin login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }

    // =====================================================================
    // 2. LIST ALL CLINICS
    // =====================================================================
    @GetMapping("/superadmin/clinics")
    public ResponseEntity<ApiResponse<List<ClinicDTO>>> getAllClinics() {
        log.info("📋 Fetching all clinics");
        try {
            List<ClinicDTO> clinics = superAdminService.getAllClinics();
            return ResponseEntity.ok(ApiResponse.success("Clinics fetched successfully", clinics));
        } catch (Exception e) {
            log.error("❌ Failed to fetch clinics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch clinics: " + e.getMessage()));
        }
    }

    // =====================================================================
    // 3. GET CLINIC BY TENANT ID
    // =====================================================================
    @GetMapping("/superadmin/clinics/{tenantId}")
    public ResponseEntity<ApiResponse<ClinicDTO>> getClinic(@PathVariable String tenantId) {
        log.info("🔍 Fetching clinic: {}", tenantId);
        try {
            ClinicDTO clinic = superAdminService.getClinicByTenantId(tenantId);
            return ResponseEntity.ok(ApiResponse.success("Clinic fetched successfully", clinic));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Clinic not found: " + e.getMessage()));
        }
    }

    // =====================================================================
    // 4. GET CLINIC BY CLINIC CODE (for URL-based tenant identification)
    // =====================================================================
    @GetMapping("/auth/clinic-info")
    public ResponseEntity<ApiResponse<ClinicDTO>> getClinicByCode(@RequestParam String code) {
        log.info("🔍 Fetching clinic by code: {}", code);
        try {
            ClinicDTO clinic = superAdminService.getClinicByCode(code);
            return ResponseEntity.ok(ApiResponse.success("Clinic found", clinic));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Clinic not found: " + e.getMessage()));
        }
    }

    // =====================================================================
    // 4b. VALIDATE CLINIC BY TENANT ID (public, no auth needed)
    // =====================================================================
    @GetMapping("/auth/validate-clinic")
    public ResponseEntity<ApiResponse<ClinicDTO>> validateClinic(@RequestParam String tenantId) {
        log.info("🔍 Validating clinic by tenantId: {}", tenantId);
        try {
            ClinicDTO clinic = superAdminService.getClinicByTenantId(tenantId);
            return ResponseEntity.ok(ApiResponse.success("Clinic found", clinic));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Invalid Clinic ID. Please check and try again."));
        }
    }

    // =====================================================================
    // 5. CREATE NEW CLINIC
    // =====================================================================
    @PostMapping("/superadmin/clinics")
    public ResponseEntity<ApiResponse<ClinicDTO>> createClinic(@Valid @RequestBody ClinicDTO clinicDTO) {
        log.info("🏥 Creating new clinic: {}", clinicDTO.getClinicName());
        try {
            ClinicDTO created = superAdminService.createClinic(clinicDTO);
            return new ResponseEntity<>(ApiResponse.success("Clinic created successfully", created),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("❌ Failed to create clinic: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create clinic: " + e.getMessage()));
        }
    }

    // =====================================================================
    // 6. UPDATE CLINIC
    // =====================================================================
    @PutMapping("/superadmin/clinics/{tenantId}")
    public ResponseEntity<ApiResponse<ClinicDTO>> updateClinic(
            @PathVariable String tenantId, @RequestBody ClinicDTO clinicDTO) {
        log.info("📝 Updating clinic: {}", tenantId);
        try {
            ClinicDTO updated = superAdminService.updateClinic(tenantId, clinicDTO);
            return ResponseEntity.ok(ApiResponse.success("Clinic updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update clinic: " + e.getMessage()));
        }
    }

    // =====================================================================
    // 7. ACTIVATE/DEACTIVATE CLINIC
    // =====================================================================
    @PutMapping("/superadmin/clinics/{tenantId}/status")
    public ResponseEntity<ApiResponse<String>> toggleClinicStatus(
            @PathVariable String tenantId, @RequestParam boolean active) {
        log.info("🔄 Toggling clinic {} status to: {}", tenantId, active);
        try {
            superAdminService.toggleClinicStatus(tenantId, active);
            return ResponseEntity.ok(ApiResponse.success(
                    "Clinic " + (active ? "activated" : "deactivated") + " successfully",
                    active ? "ACTIVE" : "INACTIVE"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update status: " + e.getMessage()));
        }
    }

    // =====================================================================
    // 8. CREATE ADMIN USER FOR A CLINIC
    // =====================================================================
    @PostMapping("/superadmin/clinics/{tenantId}/admin")
    public ResponseEntity<ApiResponse<String>> createClinicAdmin(
            @PathVariable String tenantId, @RequestBody ClinicDTO adminDto) {
        log.info("👤 Creating admin for clinic: {}", tenantId);
        try {
            superAdminService.createClinicAdminUser(tenantId, adminDto);
            return new ResponseEntity<>(
                    ApiResponse.success("Admin user created successfully", adminDto.getAdminUsername()),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create admin: " + e.getMessage()));
        }
    }
}
