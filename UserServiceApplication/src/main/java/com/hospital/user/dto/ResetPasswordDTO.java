/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.user.dto;

/**
 *
 * @author mduhijod
 */
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for admin resetting user password
 * @author mduhijod
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDTO {
    
//    @NotBlank(message = "New password is required")
//    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}
