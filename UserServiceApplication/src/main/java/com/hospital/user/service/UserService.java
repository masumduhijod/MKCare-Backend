/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.user.service;

/**
 *
 * @author mduhijod
 */
// ========== UserService ==========

import com.hospital.user.config.TenantContext;
import com.hospital.user.dto.*;
import com.hospital.user.entity.User;
import com.hospital.user.repository.UserRepository;
import com.hospital.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    // ⭐ Master database JDBC template for tenant lookup
    @Autowired
    @Qualifier("masterJdbcTemplate")
    private JdbcTemplate masterJdbcTemplate;

    /**
     * Login with multi-tenant support
     */
    public LoginResponse login(LoginRequest request) {
        String currentTenant = TenantContext.getTenantId();
        log.info("🔍 Login service called - Tenant from context: {}", currentTenant);
        log.info("🔍 Login request - User: {}, Clinic: {}", request.getUsername(), request.getClinicId());

        if (currentTenant == null || currentTenant.isEmpty()) {
            log.error("❌ CRITICAL: Tenant context is NULL in service layer!");
            throw new RuntimeException("Tenant context not set. Please try again.");
        }

        // Find user in tenant database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("❌ User not found: {} in tenant: {}", request.getUsername(), currentTenant);
                    return new RuntimeException("Invalid username or password");
                });

        log.info("✅ User found: {} (ID: {}) in database", user.getUsername(), user.getUserId());

        if (!user.isActive()) {
            log.error("❌ User account is not active: {}", user.getUsername());
            throw new RuntimeException("User account is not active");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("❌ Password mismatch for user: {}", user.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        log.info("✅ Password verified for user: {}", user.getUsername());

        // Update last login
        user.updateLastLogin();
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        // Fetch tenant/clinic info from master database
        log.info("🔍 Fetching clinic info from master DB for tenant: {}", request.getClinicId());
        String sql = "SELECT tenant_id, clinic_code, clinic_name, logo_path, address, phone FROM tenants WHERE tenant_id = ?";

        Map<String, Object> tenantInfo;
        try {
            tenantInfo = masterJdbcTemplate.queryForMap(sql, request.getClinicId());
            log.info("✅ Clinic info fetched: {}", tenantInfo.get("clinic_name"));
        } catch (Exception e) {
            log.error("❌ Failed to fetch clinic info for tenant: {}", request.getClinicId(), e);
            throw new RuntimeException("Failed to fetch clinic information");
        }

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setFullName(user.getFullName());
        response.setExpiresAt(LocalDateTime.now().plusHours(24));

        // Add tenant/clinic information
        response.setTenantId((String) tenantInfo.get("tenant_id"));
        response.setClinicName((String) tenantInfo.get("clinic_name"));
        response.setClinicLogo((String) tenantInfo.get("logo_path"));
        response.setClinicAddress((String) tenantInfo.get("address"));
        response.setClinicPhone((String) tenantInfo.get("phone"));

        log.info("✅ Login successful - User: {} at Clinic: {}", user.getUsername(), tenantInfo.get("clinic_name"));
        return response;
    }

    public UserDTO registerUser(UserRegistrationDTO dto) {
        log.info("Registering new user: {}", dto.getUsername());

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());

        // Parse Role (Case Insensitive)
        User.UserRole role = parseUserRole(dto.getRole());
        if (role == null) {
            throw new RuntimeException("Invalid role: " + dto.getRole());
        }
        user.setRole(role);

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setContactNumber(dto.getContactNumber());
        user.setCreatedBy(dto.getCreatedBy());
        user.setStatus(User.UserStatus.ACTIVE);

        User saved = userRepository.save(user);
        log.info("User registered successfully: {}", saved.getUsername());

        return modelMapper.map(saved, UserDTO.class);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByRole(String role) {
        User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
        return userRepository.findByRole(userRole).stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    public void changePassword(String username, ChangePasswordDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", username);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public UserDTO updateUser(String username, UserUpdateDTO updateDTO) {
        log.info("Updating user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        user.setContactNumber(updateDTO.getContactNumber());

        if (!user.getEmail().equals(updateDTO.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new RuntimeException("Email already exists: " + updateDTO.getEmail());
            }
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getRole() != null && !updateDTO.getRole().isEmpty()) {
            User.UserRole newRole = parseUserRole(updateDTO.getRole());
            if (newRole == null) {
                throw new RuntimeException("Invalid role: " + updateDTO.getRole());
            }
            user.setRole(newRole);
        }

        if (updateDTO.getStatus() != null && !updateDTO.getStatus().isEmpty()) {
            User.UserStatus newStatus = parseUserStatus(updateDTO.getStatus());
            if (newStatus == null) {
                throw new RuntimeException("Invalid status: " + updateDTO.getStatus());
            }
            user.setStatus(newStatus);
        }

        User savedUser = userRepository.save(user);
        log.info("User updated successfully: {}", username);

        UserDTO dto = new UserDTO();
        dto.setUserId(savedUser.getUserId());
        dto.setUsername(savedUser.getUsername());
        dto.setEmail(savedUser.getEmail());
        dto.setRole(savedUser.getRole().name());
        dto.setStatus(savedUser.getStatus().name());
        dto.setFirstName(savedUser.getFirstName());
        dto.setLastName(savedUser.getLastName());
        dto.setFullName(savedUser.getFullName());
        dto.setContactNumber(savedUser.getContactNumber());
        dto.setLastLogin(savedUser.getLastLogin());
        dto.setCreatedAt(savedUser.getCreatedAt());

        return dto;
    }

    private User.UserRole parseUserRole(String roleStr) {
        if (roleStr == null || roleStr.isEmpty()) {
            return null;
        }

        String normalized = roleStr.toUpperCase().trim();

        if (normalized.equals("LAB_TECHNICIAN") || normalized.equals("LAB TECHNICIAN")) {
            normalized = "LAB_TECH";
        }

        try {
            return User.UserRole.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role string: {}", roleStr);
            return null;
        }
    }

    private User.UserStatus parseUserStatus(String statusStr) {
        if (statusStr == null || statusStr.isEmpty()) {
            return null;
        }

        try {
            return User.UserStatus.valueOf(statusStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            log.error("Invalid status string: {}", statusStr);
            return null;
        }
    }

    public void deleteUser(String username) {
        log.info("Deleting user: {}", username);

        if ("admin".equalsIgnoreCase(username)) {
            throw new RuntimeException("Cannot delete admin user");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        userRepository.delete(user);
        log.info("User deleted successfully: {}", username);
    }

    public void resetPassword(String username, String newPassword) {
        log.info("Resetting password for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", username);
    }
}