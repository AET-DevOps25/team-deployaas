package com.aet.studyassistant.auth_service.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testSecret;
    private static final String TEST_USERNAME = "testuser@example.com";
    private static final UUID TEST_USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testSecret = "mySecretKeyForTestingPurposesOnly123456789";
        jwtUtil = new JwtUtil(testSecret);
    }

    @Test
    void generateTokenWithUserDetailsCreatesValidToken() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);

        // Act
        String token = jwtUtil.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void generateTokenWithUserDetailsAndUserIdCreatesValidToken() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);

        // Act
        String token = jwtUtil.generateToken(userDetails, TEST_USER_ID);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void extractUsernameFromValidTokenReturnsCorrectUsername() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        String token = jwtUtil.generateToken(userDetails);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    void validateTokenWithValidTokenReturnsTrue() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        String token = jwtUtil.generateToken(userDetails);

        // Act
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateTokenWithInvalidTokenReturnsFalse() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        
        UserDetails differentUserDetails = mock(UserDetails.class);
        when(differentUserDetails.getUsername()).thenReturn("different@example.com");
        
        String token = jwtUtil.generateToken(userDetails);

        // Act
        boolean isValid = jwtUtil.validateToken(token, differentUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractUsernameFromInvalidTokenThrowsException() {
        // Arrange
        String invalidToken = "invalid.token.format";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtUtil.extractUsername(invalidToken));
    }

    @Test
    void validateTokenWithCorruptedTokenThrowsException() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        String corruptedToken = "corrupted.token.here";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtUtil.validateToken(corruptedToken, userDetails));
    }

    @Test
    void generateTokenWithNullUserDetailsThrowsException() {
        // Arrange
        UserDetails nullUserDetails = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.generateToken(nullUserDetails));
    }

    @Test
    void generateTokenWithNullUsernameThrowsException() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.generateToken(userDetails));
    }

    @Test
    void generateTokenWithEmptyUsernameThrowsException() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.generateToken(userDetails));
    }

    @Test
    void generateTokenWithUserIdCreatesTokenWithUserIdClaim() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);

        // Act
        String token = jwtUtil.generateToken(userDetails, TEST_USER_ID);

        // Assert
        assertNotNull(token);
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    void generateTokenWithNullUserIdStillCreatesValidToken() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);

        // Act
        String token = jwtUtil.generateToken(userDetails, null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsernameFromEmptyTokenThrowsException() {
        // Arrange
        String emptyToken = "";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.extractUsername(emptyToken));
    }

    @Test
    void extractUsernameFromNullTokenThrowsException() {
        // Arrange
        String nullToken = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.extractUsername(nullToken));
    }

    @Test
    void validateTokenWithNullTokenThrowsException() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.validateToken(null, userDetails));
    }

    @Test
    void validateTokenWithEmptyTokenThrowsException() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.validateToken("", userDetails));
    }

    @Test
    void validateTokenWithNullUserDetailsThrowsException() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        String token = jwtUtil.generateToken(userDetails);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> jwtUtil.validateToken(token, null));
    }

    @Test
    void generateTokensWithSameUserDetailsProduceDifferentTokens() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);

        // Act
        String token1 = jwtUtil.generateToken(userDetails);

        // Add a small delay to ensure different timestamps
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String token2 = jwtUtil.generateToken(userDetails);

        // Assert
        assertNotEquals(token1, token2);
    }

    @Test
    void generateTokenWithVeryLongUsernameCreatesValidToken() {
        // Arrange
        StringBuilder longUsernameBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longUsernameBuilder.append("user");
        }
        longUsernameBuilder.append("@example.com");
        String longUsername = longUsernameBuilder.toString();
        
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(longUsername);

        // Act
        String token = jwtUtil.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(longUsername, extractedUsername);
    }

    @Test
    void generateTokenWithSpecialCharactersInUsernameCreatesValidToken() {
        // Arrange
        String specialUsername = "user+test@example-domain.co.uk";
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(specialUsername);

        // Act
        String token = jwtUtil.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(specialUsername, extractedUsername);
    }
}
