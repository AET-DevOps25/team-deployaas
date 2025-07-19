package com.aet.studyassistant.auth_service.security;

import com.aet.studyassistant.auth_service.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_PASSWORD_HASH = "hashed_password";

    @Test
    void constructorWithValidUserCreatesCorrectUserDetails() {
        // Arrange
        User user = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME, TEST_PASSWORD_HASH);

        // Act
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // Assert
        assertEquals(TEST_USER_ID, userDetails.getId());
        assertEquals(TEST_EMAIL, userDetails.getEmail());
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        assertEquals(TEST_PASSWORD_HASH, userDetails.getPassword());
        assertNotNull(userDetails.getAuthorities());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void getAuthoritiesReturnsEmptyCollection() {
        // Arrange
        User user = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME, TEST_PASSWORD_HASH);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // Act
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void equalsWithSameUserIdReturnsTrue() {
        // Arrange
        User user1 = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME, TEST_PASSWORD_HASH);
        User user2 = createMockUser(TEST_USER_ID, "different@email.com", "Different Name", "different_hash");
        
        UserDetailsImpl userDetails1 = new UserDetailsImpl(user1);
        UserDetailsImpl userDetails2 = new UserDetailsImpl(user2);

        // Act & Assert
        assertEquals(userDetails1, userDetails2);
        assertEquals(userDetails1.hashCode(), userDetails2.hashCode());
    }

    @Test
    void equalsWithDifferentUserIdReturnsFalse() {
        // Arrange
        UUID differentUserId = UUID.randomUUID();
        User user1 = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME, TEST_PASSWORD_HASH);
        User user2 = createMockUser(differentUserId, TEST_EMAIL, TEST_NAME, TEST_PASSWORD_HASH);
        
        UserDetailsImpl userDetails1 = new UserDetailsImpl(user1);
        UserDetailsImpl userDetails2 = new UserDetailsImpl(user2);

        // Act & Assert
        assertNotEquals(userDetails1, userDetails2);
        assertNotEquals(userDetails1.hashCode(), userDetails2.hashCode());
    }

    @Test
    void equalsWithNullReturnsFalse() {
        // Arrange
        User user = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME, TEST_PASSWORD_HASH);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // Act & Assert
        assertNotEquals(userDetails, null);
    }

    // Helper method for creating mock users
    private User createMockUser(UUID id, String email, String name, String passwordHash) {
        User user = new User();
        user.setUuid(id);
        user.setEmail(email);
        user.setName(name);
        user.setPasswordHash(passwordHash);
        return user;
    }
}
