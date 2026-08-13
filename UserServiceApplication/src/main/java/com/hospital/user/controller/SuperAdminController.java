package com.hospital.user.controller;

import com.hospital.user.dto.*;
import com.hospital.user.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

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

            // ✅ CHECK 1: Is clinic active?
            if (!clinic.isActive()) {
                log.warn("❌ Clinic {} is INACTIVE", tenantId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Clinic is currently turned OFF. Please contact your Super Admin."));
            }

            // ✅ CHECK 2: Is subscription expired?
            if (clinic.getSubscriptionExpiry() != null &&
                    clinic.getSubscriptionExpiry().isBefore(java.time.LocalDateTime.now())) {
                String expiredAt = clinic.getSubscriptionExpiry()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
                log.warn("❌ Clinic {} subscription expired at {}", tenantId, expiredAt);
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                        .body(ApiResponse.error("Subscription Expired! Your clinic plan ended on " + expiredAt + ". Please contact your Super Admin to renew."));
            }

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

    // =====================================================================
    // 9. RENEW/UPDATE CLINIC SUBSCRIPTION
    // =====================================================================
    @PostMapping("/superadmin/clinics/{tenantId}/renew")
    public ResponseEntity<ApiResponse<ClinicDTO>> renewClinicSubscription(
            @PathVariable String tenantId, @RequestBody ClinicDTO clinicDTO) {
        log.info("📅 Renewing/Updating subscription for clinic: {}", tenantId);
        try {
            ClinicDTO updated = superAdminService.renewClinicSubscription(tenantId, clinicDTO);
            return ResponseEntity.ok(ApiResponse.success("Clinic subscription updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to renew subscription: " + e.getMessage()));
        }
    }
    // =====================================================================
    // 10. RBAC: GET ALL ROLES
    // =====================================================================
    @GetMapping("/superadmin/roles")
    public ResponseEntity<ApiResponse<List<String>>> getAllRoles() {
        log.info("📋 Fetching all user roles");
        return ResponseEntity.ok(ApiResponse.success("Roles fetched", superAdminService.getAllRoles()));
    }

    // =====================================================================
    // 11. RBAC: GET ALL MODULES
    // =====================================================================
    @GetMapping("/superadmin/modules")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllModules() {
        log.info("📋 Fetching all system modules");
        return ResponseEntity.ok(ApiResponse.success("Modules fetched", superAdminService.getAllModules()));
    }

    // =====================================================================
    // 12. RBAC: GET PERMISSIONS FOR A ROLE
    // =====================================================================
    @GetMapping("/superadmin/role-permissions/{roleName}")
    public ResponseEntity<ApiResponse<List<String>>> getRolePermissions(@PathVariable String roleName) {
        log.info("🔍 Fetching permissions for role: {}", roleName);
        return ResponseEntity.ok(ApiResponse.success("Permissions fetched", superAdminService.getRolePermissions(roleName)));
    }

    // =====================================================================
    // 13. RBAC: UPDATE ROLE PERMISSIONS
    // =====================================================================
    @PostMapping("/superadmin/role-permissions/{roleName}")
    public ResponseEntity<ApiResponse<String>> updateRolePermissions(
            @PathVariable String roleName, @RequestBody List<String> moduleCodes) {
        log.info("📝 Updating permissions for role: {}", roleName);
        try {
            superAdminService.updateRolePermissions(roleName, moduleCodes);
            return ResponseEntity.ok(ApiResponse.success("Permissions updated successfully", roleName));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update permissions: " + e.getMessage()));
        }
    }

    // =====================================================================
    // 14. RBAC: GET USERS FOR A CLINIC
    // =====================================================================
    @GetMapping("/superadmin/clinics/{tenantId}/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getClinicUsers(@PathVariable String tenantId) {
        log.info("👥 Request to fetch users for clinic: {}", tenantId);
        return ResponseEntity.ok(ApiResponse.success("Users fetched", superAdminService.getClinicUsers(tenantId)));
    }

    // =====================================================================
    // 15. RBAC: GET PERMISSIONS FOR A SPECIFIC USER
    // =====================================================================
    @GetMapping("/superadmin/clinics/{tenantId}/users/{userId}/permissions")
    public ResponseEntity<ApiResponse<List<String>>> getUserPermissions(
            @PathVariable String tenantId, @PathVariable Long userId) {
        log.info("🔍 Request to fetch permissions for User ID {} in Clinic {}", userId, tenantId);
        return ResponseEntity.ok(ApiResponse.success("User permissions fetched", 
                superAdminService.getUserPermissions(tenantId, userId)));
    }

    // =====================================================================
    // 16. RBAC: UPDATE USER PERMISSIONS
    // =====================================================================
    @PostMapping("/superadmin/clinics/{tenantId}/users/{userId}/permissions")
    public ResponseEntity<ApiResponse<String>> updateUserPermissions(
            @PathVariable String tenantId, @PathVariable Long userId, @RequestBody List<String> moduleCodes) {
        log.info("📝 Request to update permissions for User ID {} in Clinic {}", userId, tenantId);
        try {
            superAdminService.updateUserPermissions(tenantId, userId, moduleCodes);
            return ResponseEntity.ok(ApiResponse.success("User permissions updated successfully", tenantId));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update user permissions: " + e.getMessage()));
        }
    }
}
