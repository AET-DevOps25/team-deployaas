package com.aet.studyassistant.auth_service.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
    private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Test
    void defaultConstructorCreatesEmptyObject() {
        // Arrange & Act
        AuthResponse authResponse = new AuthResponse();

        // Assert
        assertNull(authResponse.getToken());
        assertNull(authResponse.getUserId());
    }

    @Test
    void parameterizedConstructorCreatesCorrectObject() {
        // Arrange & Act
        AuthResponse authResponse = new AuthResponse(TEST_TOKEN, TEST_USER_ID);

        // Assert
        assertEquals(TEST_TOKEN, authResponse.getToken());
        assertEquals(TEST_USER_ID, authResponse.getUserId());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        // Arrange
        AuthResponse authResponse = new AuthResponse();

        // Act
        authResponse.setToken(TEST_TOKEN);
        authResponse.setUserId(TEST_USER_ID);

        // Assert
        assertEquals(TEST_TOKEN, authResponse.getToken());
        assertEquals(TEST_USER_ID, authResponse.getUserId());
    }

    @Test
    void gettersReturnCorrectValues() {
        // Arrange
        AuthResponse authResponse = new AuthResponse(TEST_TOKEN, TEST_USER_ID);

        // Act & Assert
        assertEquals(TEST_TOKEN, authResponse.getToken());
        assertEquals(TEST_USER_ID, authResponse.getUserId());
    }

    @Test
    void authResponseHandlesNullToken() {
        // Arrange & Act
        AuthResponse authResponse = new AuthResponse(null, TEST_USER_ID);

        // Assert
        assertNull(authResponse.getToken());
        assertEquals(TEST_USER_ID, authResponse.getUserId());
    }

    @Test
    void authResponseHandlesNullUserId() {
        // Arrange & Act
        AuthResponse authResponse = new AuthResponse(TEST_TOKEN, null);

        // Assert
        assertEquals(TEST_TOKEN, authResponse.getToken());
        assertNull(authResponse.getUserId());
    }

    @Test
    void authResponseHandlesEmptyToken() {
        // Arrange & Act
        AuthResponse authResponse = new AuthResponse("", TEST_USER_ID);

        // Assert
        assertEquals("", authResponse.getToken());
        assertEquals(TEST_USER_ID, authResponse.getUserId());
    }

    @Test
    void authResponseHandlesAllNullValues() {
        // Arrange & Act
        AuthResponse authResponse = new AuthResponse(null, null);

        // Assert
        assertNull(authResponse.getToken());
        assertNull(authResponse.getUserId());
    }

    @Test
    void authResponseWithValidJwtToken() {
        // Arrange
        String validJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        // Act
        AuthResponse authResponse = new AuthResponse(validJwtToken, TEST_USER_ID);

        // Assert
        assertEquals(validJwtToken, authResponse.getToken());
        assertEquals(TEST_USER_ID, authResponse.getUserId());
    }

    @Test
    void authResponseWithDifferentUuidFormats() {
        // Arrange
        UUID randomUuid = UUID.randomUUID();
        UUID nilUuid = new UUID(0L, 0L);

        // Act
        AuthResponse authResponse1 = new AuthResponse(TEST_TOKEN, randomUuid);
        AuthResponse authResponse2 = new AuthResponse(TEST_TOKEN, nilUuid);

        // Assert
        assertEquals(randomUuid, authResponse1.getUserId());
        assertEquals(nilUuid, authResponse2.getUserId());
    }

    @Test
    void authResponseWithLongToken() {
        // Arrange
        StringBuilder longTokenBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longTokenBuilder.append("a");
        }
        String longToken = longTokenBuilder.toString();

        // Act
        AuthResponse authResponse = new AuthResponse(longToken, TEST_USER_ID);

        // Assert
        assertEquals(longToken, authResponse.getToken());
        assertEquals(TEST_USER_ID, authResponse.getUserId());
    }

    @Test
    void authResponseWithSpecialCharactersInToken() {
        // Arrange
        String tokenWithSpecialChars = "token.with-special_chars123!@#$%^&*()";

        // Act
        AuthResponse authResponse = new AuthResponse(tokenWithSpecialChars, TEST_USER_ID);

        // Assert
        assertEquals(tokenWithSpecialChars, authResponse.getToken());
        assertEquals(TEST_USER_ID, authResponse.getUserId());
    }

    @Test
    void authResponseMutabilityTest() {
        // Arrange
        AuthResponse authResponse = new AuthResponse();
        String newToken = "new-token";
        UUID newUserId = UUID.randomUUID();

        // Act
        authResponse.setToken(newToken);
        authResponse.setUserId(newUserId);

        // Assert
        assertEquals(newToken, authResponse.getToken());
        assertEquals(newUserId, authResponse.getUserId());
    }

    @Test
    void authResponseImmutabilityOfUuid() {
        // Arrange
        UUID originalUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        AuthResponse authResponse = new AuthResponse(TEST_TOKEN, originalUuid);

        // Act
        UUID retrievedUuid = authResponse.getUserId();

        // Assert
        assertEquals(originalUuid, retrievedUuid);
        assertSame(originalUuid, retrievedUuid); // UUID should be the same reference
    }

    @Test
    void authResponseWithWhitespaceToken() {
        // Arrange
        String whitespaceToken = "   " + TEST_TOKEN + "   ";

        // Act
        AuthResponse authResponse = new AuthResponse(whitespaceToken, TEST_USER_ID);

        // Assert
        assertEquals(whitespaceToken, authResponse.getToken());
        assertEquals(TEST_USER_ID, authResponse.getUserId());
    }
}
