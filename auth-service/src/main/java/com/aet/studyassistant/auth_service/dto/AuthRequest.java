package com.aet.studyassistant.auth_service.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication request containing user credentials")
public class AuthRequest {
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Schema(description = "User email address", 
            example = "user@example.com", 
            required = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Schema(description = "User password", 
            example = "securePassword123", 
            required = true,
            minLength = 6)
    private String password;
    
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "User full name (required for registration)", 
            example = "John Doe", 
            required = false,
            maxLength = 100,
            minLength = 2)
    private String name; // Optional, can be used for registration
}
