package com.aet.studyassistant.flashcard_service.service;

import com.aet.studyassistant.flashcard_service.dto.FlashcardDeckDTO;
import com.aet.studyassistant.flashcard_service.dto.FlashcardDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private FlashcardService flashcardService;

    @InjectMocks
    private TemplateService templateService;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final UUID TEST_DECK_ID = UUID.randomUUID();

    @Test
    void createDefaultDecksForNewUserCreatesAllDecks() {
        // Arrange
        FlashcardDeckDTO mockDeck = new FlashcardDeckDTO(TEST_DECK_ID, TEST_USER_ID, "Test Deck", 0);
        FlashcardDTO mockFlashcard = new FlashcardDTO(UUID.randomUUID(), TEST_DECK_ID, "Front", "Back");

        when(flashcardService.createDeck(eq(TEST_USER_ID), any(String.class))).thenReturn(mockDeck);
        when(flashcardService.createFlashcard(eq(TEST_DECK_ID), any(String.class), any(String.class)))
                .thenReturn(Optional.of(mockFlashcard));

        // Act
        templateService.createDefaultDecksForNewUser(TEST_USER_ID);

        // Assert
        verify(flashcardService, times(3)).createDeck(eq(TEST_USER_ID), any(String.class));
        
        // Verify specific deck names
        verify(flashcardService).createDeck(TEST_USER_ID, "DevOps Fundamentals");
        verify(flashcardService).createDeck(TEST_USER_ID, "Testing Strategies");
        verify(flashcardService).createDeck(TEST_USER_ID, "CI/CD Pipeline Essentials");
    }

    @Test
    void createDefaultDecksForNewUserHandlesExceptionGracefully() {
        // Arrange
        when(flashcardService.createDeck(eq(TEST_USER_ID), any(String.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert - Should not throw exception
        templateService.createDefaultDecksForNewUser(TEST_USER_ID);

        // Verify that createDeck was still attempted
        verify(flashcardService, atLeastOnce()).createDeck(eq(TEST_USER_ID), any(String.class));
    }

    @Test
    void createDefaultDecksForNewUserHandlesFlashcardCreationFailure() {
        // Arrange
        FlashcardDeckDTO mockDeck = new FlashcardDeckDTO(TEST_DECK_ID, TEST_USER_ID, "Test Deck", 0);
        
        when(flashcardService.createDeck(eq(TEST_USER_ID), any(String.class))).thenReturn(mockDeck);
        when(flashcardService.createFlashcard(eq(TEST_DECK_ID), any(String.class), any(String.class)))
                .thenThrow(new RuntimeException("Flashcard creation failed"));

        // Act & Assert - Should not throw exception
        templateService.createDefaultDecksForNewUser(TEST_USER_ID);

        // Verify that deck creation was attempted
        verify(flashcardService, times(1)).createDeck(eq(TEST_USER_ID), any(String.class));
        
        // When flashcard creation fails, only the first attempt is made before the exception stops the process
        verify(flashcardService, atLeastOnce()).createFlashcard(any(UUID.class), any(String.class), any(String.class));
    }

    @Test
    void createDefaultDecksForNewUserWithNullUserIdHandlesGracefully() {
        // Arrange
        when(flashcardService.createDeck(eq(null), any(String.class)))
                .thenThrow(new IllegalArgumentException("User ID cannot be null"));

        // Act & Assert - Should not throw exception
        templateService.createDefaultDecksForNewUser(null);

        // Verify that createDeck was attempted with null
        verify(flashcardService, atLeastOnce()).createDeck(eq(null), any(String.class));
    }
}
