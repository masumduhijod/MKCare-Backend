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

import com.hospital.user.dto.*;
import com.hospital.user.entity.User;
import com.hospital.user.repository.UserRepository;
import com.hospital.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
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

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!user.isActive()) {
            throw new RuntimeException("User account is not active");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Update last login
        user.updateLastLogin();
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setFullName(user.getFullName());
        response.setExpiresAt(LocalDateTime.now().plusHours(24));

        log.info("User logged in successfully: {}", user.getUsername());
        return response;
    }

//    public UserDTO registerUser(UserRegistrationDTO dto) {
//        log.info("Registering new user: {}", dto.getUsername());
//
//        if (userRepository.existsByUsername(dto.getUsername())) {
//            throw new RuntimeException("Username already exists");
//        }
//
//        if (userRepository.existsByEmail(dto.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        User user = new User();
//        user.setUsername(dto.getUsername());
//        user.setPassword(passwordEncoder.encode(dto.getPassword()));
//        user.setEmail(dto.getEmail());
//        user.setRole(User.UserRole.valueOf(dto.getRole().toUpperCase()));
//        user.setFirstName(dto.getFirstName());
//        user.setLastName(dto.getLastName());
//        user.setContactNumber(dto.getContactNumber());
//        user.setCreatedBy(dto.getCreatedBy());
//        user.setStatus(User.UserStatus.ACTIVE);
//
//        User saved = userRepository.save(user);
//        log.info("User registered successfully: {}", saved.getUsername());
//
//        return modelMapper.map(saved, UserDTO.class);
//    }
    
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
    
    // ========== Parse Role (Case Insensitive) ==========
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
    
//    /**
//     * Update User (Admin Only)
//     */
//    public UserDTO updateUser(String username, UserUpdateDTO updateDTO) {
//        log.info("Updating user: {}", username);
//        
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found: " + username));
//        
//        // Update fields
//        user.setFirstName(updateDTO.getFirstName());
//        user.setLastName(updateDTO.getLastName());
//        user.setEmail(updateDTO.getEmail());
//        user.setContactNumber(updateDTO.getContactNumber());
//        
//        // Update role
//        try {
//            user.setRole(User.UserRole.valueOf(updateDTO.getRole().toUpperCase()));
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Invalid role: " + updateDTO.getRole());
//        }
//        
//        // Update status if provided
//        if (updateDTO.getStatus() != null && !updateDTO.getStatus().isEmpty()) {
//            try {
//                user.setStatus(User.UserStatus.valueOf(updateDTO.getStatus().toUpperCase()));
//            } catch (IllegalArgumentException e) {
//                throw new RuntimeException("Invalid status: " + updateDTO.getStatus());
//            }
//        }
//        
//        User savedUser = userRepository.save(user);
//        log.info("User updated successfully: {}", username);
//        
//        return modelMapper.map(savedUser, UserDTO.class);
//    }
    
    /**
     * Update User (Admin Only)
     */
    public UserDTO updateUser(String username, UserUpdateDTO updateDTO) {
        log.info("Updating user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Update basic fields
        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        user.setContactNumber(updateDTO.getContactNumber());

        // Check if email is being changed
        if (!user.getEmail().equals(updateDTO.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new RuntimeException("Email already exists: " + updateDTO.getEmail());
            }
            user.setEmail(updateDTO.getEmail());
        }

        // ========== UPDATE ROLE (Case Insensitive) ==========
        if (updateDTO.getRole() != null && !updateDTO.getRole().isEmpty()) {
            User.UserRole newRole = parseUserRole(updateDTO.getRole());
            if (newRole == null) {
                throw new RuntimeException("Invalid role: " + updateDTO.getRole()
                        + ". Valid roles: ADMIN, DOCTOR, RECEPTIONIST, NURSE, PHARMACIST, LAB_TECH, BILLING, PATIENT");
            }
            user.setRole(newRole);
        }

        // ========== UPDATE STATUS (Case Insensitive) ==========
        if (updateDTO.getStatus() != null && !updateDTO.getStatus().isEmpty()) {
            User.UserStatus newStatus = parseUserStatus(updateDTO.getStatus());
            if (newStatus == null) {
                throw new RuntimeException("Invalid status: " + updateDTO.getStatus()
                        + ". Valid statuses: ACTIVE, INACTIVE, LOCKED");
            }
            user.setStatus(newStatus);
        }

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User updated successfully: {}", username);

        // Manual mapping to avoid enum issues
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

    /**
     * Parse UserRole from string (Case Insensitive) Accepts: "doctor",
     * "Doctor", "DOCTOR", etc.
     */
    private User.UserRole parseUserRole(String roleStr) {
        if (roleStr == null || roleStr.isEmpty()) {
            return null;
        }

        // Convert to uppercase and handle special cases
        String normalized = roleStr.toUpperCase().trim();

        // Handle "LAB_TECHNICIAN" variations
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

    /**
     * Parse UserStatus from string (Case Insensitive) Accepts: "active",
     * "Active", "ACTIVE", etc.
     */
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

    /**
     * Delete User (Admin Only)
     */
    public void deleteUser(String username) {
        log.info("Deleting user: {}", username);
        
        // Prevent deleting admin user
        if ("admin".equalsIgnoreCase(username)) {
            throw new RuntimeException("Cannot delete admin user");
        }
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        userRepository.delete(user);
        log.info("User deleted successfully: {}", username);
    }

    /**
     * Reset Password (Admin Only)
     */
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

