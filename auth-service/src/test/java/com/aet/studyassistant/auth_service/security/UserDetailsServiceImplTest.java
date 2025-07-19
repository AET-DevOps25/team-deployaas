package com.aet.studyassistant.auth_service.security;

import com.aet.studyassistant.auth_service.model.User;
import com.aet.studyassistant.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_PASSWORD_HASH = "hashed_password";
    private static final UUID TEST_USER_ID = UUID.randomUUID();

    @Test
    void loadUserByUsernameWhenUserExistsReturnsUserDetails() {
        // Arrange
        User user = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME, TEST_PASSWORD_HASH);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

        // Assert
        assertNotNull(userDetails);
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        assertEquals(TEST_PASSWORD_HASH, userDetails.getPassword());
        assertTrue(userDetails instanceof UserDetailsImpl);
        
        UserDetailsImpl customUserDetails = (UserDetailsImpl) userDetails;
        assertEquals(TEST_USER_ID, customUserDetails.getId());
        assertEquals(TEST_EMAIL, customUserDetails.getEmail());
        
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    void loadUserByUsernameWhenUserDoesNotExistThrowsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(TEST_EMAIL)
        );
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    void loadUserByUsernameWithNullEmailThrowsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(null)
        );
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(null);
    }

    @Test
    void loadUserByUsernameWithEmptyEmailThrowsUsernameNotFoundException() {
        // Arrange
        String emptyEmail = "";
        when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(emptyEmail)
        );
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(emptyEmail);
    }

    @Test
    void loadUserByUsernameWithDifferentCaseEmailFindsUser() {
        // Arrange
        String upperCaseEmail = "TEST@EXAMPLE.COM";
        User user = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME, TEST_PASSWORD_HASH);
        when(userRepository.findByEmail(upperCaseEmail)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(upperCaseEmail);

        // Assert
        assertNotNull(userDetails);
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail(upperCaseEmail);
    }

    @Test
    void loadUserByUsernameWithValidEmailReturnsCorrectUserDetailsType() {
        // Arrange
        User user = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME, TEST_PASSWORD_HASH);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

        // Assert
        assertTrue(userDetails instanceof UserDetailsImpl);
        assertTrue(userDetails.getAuthorities().isEmpty());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsernameWhenRepositoryThrowsExceptionPropagatesException() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userDetailsService.loadUserByUsername(TEST_EMAIL)
        );
        
        assertEquals("Database connection error", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    void loadUserByUsernameWithSpecialCharactersInEmailHandlesCorrectly() {
        // Arrange
        String specialEmail = "user+test@example-domain.co.uk";
        User user = createMockUser(TEST_USER_ID, specialEmail, TEST_NAME, TEST_PASSWORD_HASH);
        when(userRepository.findByEmail(specialEmail)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(specialEmail);

        // Assert
        assertNotNull(userDetails);
        assertEquals(specialEmail, userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail(specialEmail);
    }

    @Test
    void loadUserByUsernameWithLongEmailHandlesCorrectly() {
        // Arrange
        StringBuilder longEmailBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longEmailBuilder.append("user");
        }
        longEmailBuilder.append("@example.com");
        String longEmail = longEmailBuilder.toString();
        
        User user = createMockUser(TEST_USER_ID, longEmail, TEST_NAME, TEST_PASSWORD_HASH);
        when(userRepository.findByEmail(longEmail)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(longEmail);

        // Assert
        assertNotNull(userDetails);
        assertEquals(longEmail, userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail(longEmail);
    }

    @Test
    void loadUserByUsernameMultipleCallsWithSameEmailCallsRepositoryMultipleTimes() {
        // Arrange
        User user = createMockUser(TEST_USER_ID, TEST_EMAIL, TEST_NAME, TEST_PASSWORD_HASH);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        // Act
        userDetailsService.loadUserByUsername(TEST_EMAIL);
        userDetailsService.loadUserByUsername(TEST_EMAIL);
        userDetailsService.loadUserByUsername(TEST_EMAIL);

        // Assert
        verify(userRepository, times(3)).findByEmail(TEST_EMAIL);
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
