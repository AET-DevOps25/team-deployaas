package com.aet.studyassistant.auth_service.dto;

import lombok.*;
import java.util.UUID; // =

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UUID userId; // Add
}