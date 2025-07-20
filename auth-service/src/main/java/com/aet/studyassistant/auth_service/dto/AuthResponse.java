package com.aet.studyassistant.auth_service.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response containing JWT token and user information")
public class AuthResponse {
    
    @Schema(description = "JWT access token for API authentication", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2MjM5MDIyLCJleHAiOjE2MTYyNDI2MjJ9.signature")
    private String token;
    
    @Schema(description = "Unique user identifier", 
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;
}