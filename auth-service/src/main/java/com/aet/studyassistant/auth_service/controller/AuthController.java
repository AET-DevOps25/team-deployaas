package com.aet.studyassistant.auth_service.controller;

import com.aet.studyassistant.auth_service.dto.AuthRequest;
import com.aet.studyassistant.auth_service.dto.AuthResponse;
import com.aet.studyassistant.auth_service.model.User;
import com.aet.studyassistant.auth_service.repository.UserRepository;
import com.aet.studyassistant.auth_service.security.JwtUtil;
import com.aet.studyassistant.auth_service.security.UserDetailsImpl;
import com.aet.studyassistant.auth_service.security.UserDetailsServiceImpl;
import com.aet.studyassistant.auth_service.service.FlashcardServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID; 

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FlashcardServiceClient flashcardServiceClient;

    // Test endpoint to check service connection
    @GetMapping("/test")
    public String testConnection() {
        return "Auth Service is connected successfully!\n";
    }

    // User registration endpoint
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use\n");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName()); 
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepo.save(user); 

        // Set up default flashcard decks for the new user
        flashcardServiceClient.setupDefaultDecksForUser(savedUser.getUuid());

        return ResponseEntity.ok("User registered successfully\n");
    }

    // User login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        System.out.println(">>> Login endpoint hit with email: " + request.getEmail());
        try {
            // Authenticate user credentials using Spring Security's AuthenticationManager
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

            // Load user details, which will now return your custom UserDetailsImpl
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            UUID userId = null;

            if (userDetails instanceof UserDetailsImpl) {
                UserDetailsImpl customUserDetails = (UserDetailsImpl) userDetails;
                userId = customUserDetails.getId(); 
                // If you added getEmail() to UserDetailsImpl and want to use it:
                // userEmail = customUserDetails.getEmail();
            } else {
                System.err.println("Error: Authenticated UserDetails is not an instance of UserDetailsImpl. Cannot retrieve UUID.");
            }

            // Generate JWT token using the loaded UserDetails and userId
            String token = jwtUtil.generateToken(userDetails, userId);

            // Return the AuthResponse containing the token, userId, and userEmail
            return ResponseEntity.ok(new AuthResponse(token, userId)); 

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid email or password");
        } catch (Exception ex) {
            ex.printStackTrace(); 
            return ResponseEntity.status(500).body("Internal server error: " + ex.getMessage());
        }
    }
}