package com.aet.studyassistant.flashcard_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testSecret;
    private static final String TEST_USERNAME = "testuser@example.com";
    private static final UUID TEST_USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        testSecret = "mySecretKeyForTestingPurposesOnlyMustBeLongEnough";
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
    }

    @Test
    void extractUsernameFromValidTokenReturnsCorrectUsername() {
        // Arrange
        String token = createTestToken(TEST_USERNAME, TEST_USER_ID);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    void extractUserIdFromValidTokenReturnsCorrectUserId() {
        // Arrange
        String token = createTestToken(TEST_USERNAME, TEST_USER_ID);

        // Act
        UUID extractedUserId = jwtUtil.extractUserId(token);

        // Assert
        assertEquals(TEST_USER_ID, extractedUserId);
    }

    @Test
    void extractAllClaimsFromValidTokenReturnsAllClaims() {
        // Arrange
        String token = createTestToken(TEST_USERNAME, TEST_USER_ID);

        // Act
        Claims claims = jwtUtil.extractAllClaims(token);

        // Assert
        assertNotNull(claims);
        assertEquals(TEST_USERNAME, claims.getSubject());
        assertEquals(TEST_USER_ID.toString(), claims.get("userId", String.class));
    }

    @Test
    void validateTokenWithValidTokenReturnsTrue() {
        // Arrange
        String token = createTestToken(TEST_USERNAME, TEST_USER_ID);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateTokenWithInvalidTokenReturnsFalse() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateTokenWithExpiredTokenReturnsFalse() {
        // Arrange
        String expiredToken = createExpiredTestToken(TEST_USERNAME, TEST_USER_ID);

        // Act
        boolean isValid = jwtUtil.validateToken(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateTokenWithNullTokenReturnsFalse() {
        // Act
        boolean isValid = jwtUtil.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateTokenWithEmptyTokenReturnsFalse() {
        // Act
        boolean isValid = jwtUtil.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractUsernameFromInvalidTokenThrowsException() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtUtil.extractUsername(invalidToken));
    }

    @Test
    void extractUserIdFromInvalidTokenThrowsException() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtUtil.extractUserId(invalidToken));
    }

    @Test
    void extractAllClaimsFromInvalidTokenThrowsException() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtUtil.extractAllClaims(invalidToken));
    }

    @Test
    void extractUserIdFromTokenWithoutUserIdReturnsNull() {
        // Arrange
        String tokenWithoutUserId = createTestTokenWithoutUserId(TEST_USERNAME);

        // Act
        UUID extractedUserId = jwtUtil.extractUserId(tokenWithoutUserId);

        // Assert
        assertNull(extractedUserId);
    }

    @Test
    void extractUserIdFromTokenWithInvalidUserIdFormatThrowsException() {
        // Arrange
        String tokenWithInvalidUserId = createTestTokenWithInvalidUserId(TEST_USERNAME);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.extractUserId(tokenWithInvalidUserId));
    }

    @Test
    void validateTokenWithMalformedTokenReturnsFalse() {
        // Arrange
        String malformedToken = "header.payload"; // Missing signature

        // Act
        boolean isValid = jwtUtil.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateTokenWithTamperedSignatureReturnsFalse() {
        // Arrange
        String validToken = createTestToken(TEST_USERNAME, TEST_USER_ID);
        String tamperedToken = validToken.substring(0, validToken.lastIndexOf('.')) + ".tamperedsignature";

        // Act
        boolean isValid = jwtUtil.validateToken(tamperedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractUsernameHandlesSpecialCharacters() {
        // Arrange
        String usernameWithSpecialChars = "test+user@example-domain.com";
        String token = createTestToken(usernameWithSpecialChars, TEST_USER_ID);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(usernameWithSpecialChars, extractedUsername);
    }

    @Test
    void extractUserIdHandlesUUIDEdgeCases() {
        // Arrange
        UUID edgeCaseUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        String token = createTestToken(TEST_USERNAME, edgeCaseUUID);

        // Act
        UUID extractedUserId = jwtUtil.extractUserId(token);

        // Assert
        assertEquals(edgeCaseUUID, extractedUserId);
    }

    // Helper methods for creating test tokens
    private String createTestToken(String username, UUID userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(Keys.hmacShaKeyFor(testSecret.getBytes()))
                .compact();
    }

    private String createExpiredTestToken(String username, UUID userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 hour ago
                .signWith(Keys.hmacShaKeyFor(testSecret.getBytes()))
                .compact();
    }

    private String createTestTokenWithoutUserId(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(testSecret.getBytes()))
                .compact();
    }

    private String createTestTokenWithInvalidUserId(String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", "invalid-uuid-format")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(testSecret.getBytes()))
                .compact();
    }
}
