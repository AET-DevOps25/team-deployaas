package com.aet.studyassistant.auth_service.controller;

import com.aet.studyassistant.auth_service.dto.AuthRequest;
import com.aet.studyassistant.auth_service.dto.AuthResponse;
import com.aet.studyassistant.auth_service.model.User;
import com.aet.studyassistant.auth_service.repository.UserRepository;
import com.aet.studyassistant.auth_service.security.JwtUtil;
import com.aet.studyassistant.auth_service.security.UserDetailsImpl; // Import your custom UserDetailsImpl
import com.aet.studyassistant.auth_service.security.UserDetailsServiceImpl;
import com.aet.studyassistant.auth_service.service.FlashcardServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID; // Import UUID for the userId

@RestController
@RequestMapping("/api/auth") // Consistent mapping for all auth endpoints
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Your custom UserDetailsService

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
        // Check if email is already in use
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use\n");
        }

        // Create new user entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName()); // Assuming AuthRequest has a getName() method
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepo.save(user); // Save user to database and get the saved user with ID

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

            // Cast to your custom UserDetailsImpl to access the UUID
            if (userDetails instanceof UserDetailsImpl) {
                UserDetailsImpl customUserDetails = (UserDetailsImpl) userDetails;
                userId = customUserDetails.getId(); // Retrieve the UUID using your custom getter
                // If you added getEmail() to UserDetailsImpl and want to use it:
                // userEmail = customUserDetails.getEmail();
            } else {
                // This block should ideally not be reached if UserDetailsService is configured
                // correctly.
                // Log an error or warning, as it indicates a configuration issue.
                System.err.println(
                        "Error: Authenticated UserDetails is not an instance of UserDetailsImpl. Cannot retrieve UUID.");
                // You might choose to return an internal server error or a more generic "login
                // failed"
                // if the UUID is absolutely critical for the frontend to proceed.
            }

            // Generate JWT token using the loaded UserDetails and userId
            String token = jwtUtil.generateToken(userDetails, userId);

            // Return the AuthResponse containing the token, userId, and userEmail
            return ResponseEntity.ok(new AuthResponse(token, userId)); // Updated constructor call

        } catch (BadCredentialsException ex) {
            // Handle invalid email or password
            return ResponseEntity.status(401).body("Invalid email or password");
        } catch (Exception ex) {
            // Handle any other unexpected errors during the login process
            ex.printStackTrace(); // Print stack trace for debugging purposes
            return ResponseEntity.status(500).body("Internal server error: " + ex.getMessage());
        }
    }

    // Endpoint to generate a test token (useful for development)
    @GetMapping("/test-token")
    public String getTokenForTesting() {
        System.out.println("==> /test-token endpoint hit");
        // Load a known test user to generate a token
        UserDetails user = userDetailsService.loadUserByUsername("test@example.com"); // Ensure "test@example.com"
                                                                                      // exists
        String token = jwtUtil.generateToken(user);
        System.out.println("Generated token: " + token);
        return token;
    }
}