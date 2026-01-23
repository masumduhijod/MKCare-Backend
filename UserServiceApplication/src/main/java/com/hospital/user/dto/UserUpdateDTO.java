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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user information
 * @author mduhijod
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    
//    @NotBlank(message = "First name is required")
    private String firstName;
    
    private String lastName;
    
//    @NotBlank(message = "Email is required")
//    @Email(message = "Invalid email format")
    private String email;
    
//    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;
    
//    @NotBlank(message = "Role is required")
    private String role;
    
    private String status;
}
