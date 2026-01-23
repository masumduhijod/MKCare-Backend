/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.user.controller;

import com.hospital.user.dto.ApiResponse;
import com.hospital.user.dto.ChangePasswordDTO;
import com.hospital.user.dto.UserDTO;
import com.hospital.user.service.UserService;
import static java.lang.StrictMath.log;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.user.dto.ResetPasswordDTO;
import com.hospital.user.dto.UserUpdateDTO;
import org.springframework.web.bind.annotation.DeleteMapping;

/**
 *
 * @author mduhijod
 */
// ========== UserController ==========
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "http://localhost:8383")
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(@PathVariable String username) {
        log.info("API: Get user: {}", username);
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("User found", user));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        log.info("API: Get all users");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users.size() + " user(s) found", users));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable String role) {
        log.info("API: Get users by role: {}", role);
        List<UserDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success(users.size() + " user(s) found", users));
    }

    @PutMapping("/{username}/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @PathVariable String username,
            @Valid @RequestBody ChangePasswordDTO dto) {
        log.info("API: Change password for user: {}", username);
        userService.changePassword(username, dto);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
    
    /**
     * Update User (Admin Only)
     * PUT /users/{username}
     */
    @PutMapping("/{username}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable String username,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("API: Update user: {}", username);
        UserDTO updatedUser = userService.updateUser(username, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }

    /**
     * Delete User (Admin Only)
     * DELETE /users/{username}
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String username) {
        log.info("API: Delete user: {}", username);
        
        if ("admin".equalsIgnoreCase(username)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Cannot delete admin user"));
        }
        
        userService.deleteUser(username);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", username));
    }

    /**
     * Reset Password (Admin Only)
     * PUT /users/{username}/reset-password
     */
    @PutMapping("/{username}/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @PathVariable String username,
            @Valid @RequestBody ResetPasswordDTO dto) {
        log.info("API: Reset password for user: {}", username);
        userService.resetPassword(username, dto.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }
}
