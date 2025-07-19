package com.aet.studyassistant.auth_service.controller;

import com.aet.studyassistant.auth_service.dto.AuthRequest;
import com.aet.studyassistant.auth_service.dto.AuthResponse;
import com.aet.studyassistant.auth_service.model.User;
import com.aet.studyassistant.auth_service.repository.UserRepository;
import com.aet.studyassistant.auth_service.security.JwtUtil;
import com.aet.studyassistant.auth_service.security.UserDetailsImpl;
import com.aet.studyassistant.auth_service.security.UserDetailsServiceImpl;
import com.aet.studyassistant.auth_service.service.FlashcardServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_TOKEN = "test-jwt-token";
    private static final String API_AUTH_BASE = "/api/auth";
    private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private FlashcardServiceClient flashcardServiceClient;

    @Test
    @WithMockUser
    void testConnectionReturnsSuccessMessage() throws Exception {
        // Arrange - No specific setup needed for this simple endpoint

        // Act & Assert
        mockMvc.perform(get(API_AUTH_BASE + "/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Auth Service is connected successfully!\n"));
    }

    @Test
    @WithMockUser
    void registerWithValidDataReturnsSuccessMessage() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME);
        User savedUser = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME);
        
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encoded-password");
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(flashcardServiceClient.setupDefaultDecksForUser(TEST_USER_ID)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post(API_AUTH_BASE + "/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully\n"));

        verify(userRepo, times(1)).findByEmail(TEST_EMAIL);
        verify(passwordEncoder, times(1)).encode(TEST_PASSWORD);
        verify(userRepo, times(1)).save(any(User.class));
        verify(flashcardServiceClient, times(1)).setupDefaultDecksForUser(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void registerWithExistingEmailReturnsBadRequest() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME);
        User existingUser = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME);
        
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        mockMvc.perform(post(API_AUTH_BASE + "/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already in use\n"));

        verify(userRepo, times(1)).findByEmail(TEST_EMAIL);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepo, never()).save(any(User.class));
        verify(flashcardServiceClient, never()).setupDefaultDecksForUser(any());
    }

    @Test
    @WithMockUser
    void loginWithValidCredentialsReturnsAuthResponse() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, null);
        UserDetailsImpl userDetails = createMockUserDetails(TEST_USER_ID, TEST_EMAIL);
        
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(TEST_EMAIL, TEST_PASSWORD));
        when(userDetailsService.loadUserByUsername(TEST_EMAIL)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails, TEST_USER_ID)).thenReturn(TEST_TOKEN);

        // Act & Assert
        mockMvc.perform(post(API_AUTH_BASE + "/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TEST_TOKEN))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()));

        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername(TEST_EMAIL);
        verify(jwtUtil, times(1)).generateToken(userDetails, TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void loginWithInvalidCredentialsReturnsUnauthorized() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest(TEST_EMAIL, "wrong-password", null);
        
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post(API_AUTH_BASE + "/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));

        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    @WithMockUser
    void loginWithEmptyEmailAttempts() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest("", TEST_PASSWORD, null);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post(API_AUTH_BASE + "/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authManager, times(1)).authenticate(any());
    }

    @Test
    @WithMockUser
    void loginWithEmptyPasswordAttempts() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest(TEST_EMAIL, "", null);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post(API_AUTH_BASE + "/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authManager, times(1)).authenticate(any());
    }

    @Test
    @WithMockUser
    void loginWhenAuthenticationServiceFailsReturnsInternalServerError() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, null);
        
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        mockMvc.perform(post(API_AUTH_BASE + "/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Internal server error")));

        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    @WithMockUser
    void registerWhenFlashcardServiceFailsStillReturnsSuccess() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME);
        User savedUser = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME);
        
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encoded-password");
        when(userRepo.save(any(User.class))).thenReturn(savedUser);
        when(flashcardServiceClient.setupDefaultDecksForUser(TEST_USER_ID)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post(API_AUTH_BASE + "/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully\n"));

        verify(userRepo, times(1)).save(any(User.class));
        verify(flashcardServiceClient, times(1)).setupDefaultDecksForUser(TEST_USER_ID);
    }

    // Helper methods for creating mock objects
    private User createMockUser(UUID id, String email, String name) {
        User user = new User();
        user.setUuid(id);
        user.setEmail(email);
        user.setName(name);
        user.setPasswordHash("encoded-password");
        return user;
    }

    private UserDetailsImpl createMockUserDetails(UUID id, String email) {
        User user = createMockUser(id, email, TEST_NAME);
        return new UserDetailsImpl(user);
    }
}
