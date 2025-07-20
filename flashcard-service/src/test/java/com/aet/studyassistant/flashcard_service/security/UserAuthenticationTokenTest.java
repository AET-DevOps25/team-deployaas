package com.aet.studyassistant.flashcard_service.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthenticationTokenTest {

    private static final String TEST_PRINCIPAL = "testuser@example.com";
    private static final String TEST_CREDENTIALS = "password";
    private static final UUID TEST_USER_ID = UUID.randomUUID();

    @Test
    void constructorCreatesTokenWithCorrectValues() {
        // Arrange
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        // Act
        UserAuthenticationToken token = new UserAuthenticationToken(
                TEST_PRINCIPAL, TEST_CREDENTIALS, authorities, TEST_USER_ID);

        // Assert
        assertEquals(TEST_PRINCIPAL, token.getPrincipal());
        assertEquals(TEST_CREDENTIALS, token.getCredentials());
        assertEquals(TEST_USER_ID, token.getUserId());
        assertEquals(authorities, token.getAuthorities());
        assertTrue(token.isAuthenticated());
    }

    @Test
    void getUserIdReturnsCorrectUserId() {
        // Arrange
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        UserAuthenticationToken token = new UserAuthenticationToken(
                TEST_PRINCIPAL, TEST_CREDENTIALS, authorities, TEST_USER_ID);

        // Act
        UUID result = token.getUserId();

        // Assert
        assertEquals(TEST_USER_ID, result);
    }

    @Test
    void constructorWithNullUserIdHandlesGracefully() {
        // Arrange
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

        // Act
        UserAuthenticationToken token = new UserAuthenticationToken(
                TEST_PRINCIPAL, TEST_CREDENTIALS, authorities, null);

        // Assert
        assertEquals(TEST_PRINCIPAL, token.getPrincipal());
        assertEquals(TEST_CREDENTIALS, token.getCredentials());
        assertNull(token.getUserId());
        assertEquals(authorities, token.getAuthorities());
    }

    @Test
    void constructorWithEmptyAuthoritiesWorks() {
        // Arrange
        Collection<GrantedAuthority> emptyAuthorities = Arrays.asList();

        // Act
        UserAuthenticationToken token = new UserAuthenticationToken(
                TEST_PRINCIPAL, TEST_CREDENTIALS, emptyAuthorities, TEST_USER_ID);

        // Assert
        assertEquals(TEST_PRINCIPAL, token.getPrincipal());
        assertEquals(TEST_CREDENTIALS, token.getCredentials());
        assertEquals(TEST_USER_ID, token.getUserId());
        assertTrue(token.getAuthorities().isEmpty());
    }

    @Test
    void constructorWithNullPrincipalWorks() {
        // Arrange
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

        // Act
        UserAuthenticationToken token = new UserAuthenticationToken(
                null, TEST_CREDENTIALS, authorities, TEST_USER_ID);

        // Assert
        assertNull(token.getPrincipal());
        assertEquals(TEST_CREDENTIALS, token.getCredentials());
        assertEquals(TEST_USER_ID, token.getUserId());
        assertEquals(authorities, token.getAuthorities());
    }

    @Test
    void constructorWithNullCredentialsWorks() {
        // Arrange
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

        // Act
        UserAuthenticationToken token = new UserAuthenticationToken(
                TEST_PRINCIPAL, null, authorities, TEST_USER_ID);

        // Assert
        assertEquals(TEST_PRINCIPAL, token.getPrincipal());
        assertNull(token.getCredentials());
        assertEquals(TEST_USER_ID, token.getUserId());
        assertEquals(authorities, token.getAuthorities());
    }

    @Test
    void constructorWithNullAuthoritiesWorks() {
        // Act
        UserAuthenticationToken token = new UserAuthenticationToken(
                TEST_PRINCIPAL, TEST_CREDENTIALS, null, TEST_USER_ID);

        // Assert
        assertEquals(TEST_PRINCIPAL, token.getPrincipal());
        assertEquals(TEST_CREDENTIALS, token.getCredentials());
        assertEquals(TEST_USER_ID, token.getUserId());
        assertTrue(token.getAuthorities() == null || token.getAuthorities().isEmpty());
    }

    @Test
    void tokenIsAuthenticatedByDefault() {
        // Arrange
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

        // Act
        UserAuthenticationToken token = new UserAuthenticationToken(
                TEST_PRINCIPAL, TEST_CREDENTIALS, authorities, TEST_USER_ID);

        // Assert
        assertTrue(token.isAuthenticated());
    }

    @Test
    void tokenWithMultipleAuthoritiesHandlesCorrectly() {
        // Arrange
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_MODERATOR")
        );

        // Act
        UserAuthenticationToken token = new UserAuthenticationToken(
                TEST_PRINCIPAL, TEST_CREDENTIALS, authorities, TEST_USER_ID);

        // Assert
        assertEquals(3, token.getAuthorities().size());
        assertTrue(token.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(token.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(token.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MODERATOR")));
    }

    @Test
    void tokenWithSpecialCharactersInPrincipalWorks() {
        // Arrange
        String specialPrincipal = "test+user@example-domain.com";
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

        // Act
        UserAuthenticationToken token = new UserAuthenticationToken(
                specialPrincipal, TEST_CREDENTIALS, authorities, TEST_USER_ID);

        // Assert
        assertEquals(specialPrincipal, token.getPrincipal());
        assertEquals(TEST_USER_ID, token.getUserId());
    }

    @Test
    void tokenWithEdgeCaseUUIDWorks() {
        // Arrange
        UUID edgeCaseUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

        // Act
        UserAuthenticationToken token = new UserAuthenticationToken(
                TEST_PRINCIPAL, TEST_CREDENTIALS, authorities, edgeCaseUUID);

        // Assert
        assertEquals(edgeCaseUUID, token.getUserId());
        assertEquals(TEST_PRINCIPAL, token.getPrincipal());
    }

    @Test
    void tokenInheritsFromUsernamePasswordAuthenticationToken() {
        // Arrange
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        UserAuthenticationToken token = new UserAuthenticationToken(
                TEST_PRINCIPAL, TEST_CREDENTIALS, authorities, TEST_USER_ID);

        // Assert
        assertTrue(token instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken);
    }
}
