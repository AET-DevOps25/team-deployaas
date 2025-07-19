package com.aet.studyassistant.flashcard_service.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FlashcardDeckDTOTest {

    private static final UUID TEST_DECK_ID = UUID.randomUUID();
    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_DECK_NAME = "DevOps Fundamentals";
    private static final int TEST_FLASHCARD_COUNT = 5;

    @Test
    void defaultConstructorCreatesEmptyObject() {
        // Arrange & Act
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO();

        // Assert
        assertNull(deckDTO.getId());
        assertNull(deckDTO.getUserId());
        assertNull(deckDTO.getName());
        assertNull(deckDTO.getFlashcards());
        assertEquals(0, deckDTO.getFlashcardCount());
    }

    @Test
    void parameterizedConstructorWithFlashcardsCreatesCorrectObject() {
        // Arrange
        FlashcardDTO flashcard1 = new FlashcardDTO(UUID.randomUUID(), TEST_DECK_ID, "Front 1", "Back 1");
        FlashcardDTO flashcard2 = new FlashcardDTO(UUID.randomUUID(), TEST_DECK_ID, "Front 2", "Back 2");
        List<FlashcardDTO> flashcards = Arrays.asList(flashcard1, flashcard2);

        // Act
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO(TEST_DECK_ID, TEST_USER_ID, TEST_DECK_NAME, flashcards);

        // Assert
        assertEquals(TEST_DECK_ID, deckDTO.getId());
        assertEquals(TEST_USER_ID, deckDTO.getUserId());
        assertEquals(TEST_DECK_NAME, deckDTO.getName());
        assertEquals(flashcards, deckDTO.getFlashcards());
        assertEquals(2, deckDTO.getFlashcards().size());
    }

    @Test
    void parameterizedConstructorWithCountCreatesCorrectObject() {
        // Arrange & Act
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO(TEST_DECK_ID, TEST_USER_ID, TEST_DECK_NAME, TEST_FLASHCARD_COUNT);

        // Assert
        assertEquals(TEST_DECK_ID, deckDTO.getId());
        assertEquals(TEST_USER_ID, deckDTO.getUserId());
        assertEquals(TEST_DECK_NAME, deckDTO.getName());
        assertNull(deckDTO.getFlashcards());
        assertEquals(TEST_FLASHCARD_COUNT, deckDTO.getFlashcardCount());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        // Arrange
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO();
        FlashcardDTO flashcard = new FlashcardDTO(UUID.randomUUID(), TEST_DECK_ID, "Front", "Back");
        List<FlashcardDTO> flashcards = Collections.singletonList(flashcard);

        // Act
        deckDTO.setId(TEST_DECK_ID);
        deckDTO.setUserId(TEST_USER_ID);
        deckDTO.setName(TEST_DECK_NAME);
        deckDTO.setFlashcards(flashcards);
        deckDTO.setFlashcardCount(TEST_FLASHCARD_COUNT);

        // Assert
        assertEquals(TEST_DECK_ID, deckDTO.getId());
        assertEquals(TEST_USER_ID, deckDTO.getUserId());
        assertEquals(TEST_DECK_NAME, deckDTO.getName());
        assertEquals(flashcards, deckDTO.getFlashcards());
        assertEquals(TEST_FLASHCARD_COUNT, deckDTO.getFlashcardCount());
    }

    @Test
    void gettersReturnCorrectValues() {
        // Arrange
        FlashcardDTO flashcard1 = new FlashcardDTO(UUID.randomUUID(), TEST_DECK_ID, "What is Docker?", "A containerization platform");
        FlashcardDTO flashcard2 = new FlashcardDTO(UUID.randomUUID(), TEST_DECK_ID, "What is Kubernetes?", "A container orchestration platform");
        List<FlashcardDTO> flashcards = Arrays.asList(flashcard1, flashcard2);
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO(TEST_DECK_ID, TEST_USER_ID, TEST_DECK_NAME, flashcards);
        deckDTO.setFlashcardCount(TEST_FLASHCARD_COUNT);

        // Act & Assert
        assertEquals(TEST_DECK_ID, deckDTO.getId());
        assertEquals(TEST_USER_ID, deckDTO.getUserId());
        assertEquals(TEST_DECK_NAME, deckDTO.getName());
        assertEquals(flashcards, deckDTO.getFlashcards());
        assertEquals(2, deckDTO.getFlashcards().size());
        assertEquals(TEST_FLASHCARD_COUNT, deckDTO.getFlashcardCount());
    }

    @Test
    void flashcardDeckDTOHandlesNullValues() {
        // Arrange & Act
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO(null, null, null, (List<FlashcardDTO>) null);

        // Assert
        assertNull(deckDTO.getId());
        assertNull(deckDTO.getUserId());
        assertNull(deckDTO.getName());
        assertNull(deckDTO.getFlashcards());
    }

    @Test
    void flashcardDeckDTOHandlesEmptyValues() {
        // Arrange
        List<FlashcardDTO> emptyFlashcards = Collections.emptyList();

        // Act
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO(TEST_DECK_ID, TEST_USER_ID, "", emptyFlashcards);

        // Assert
        assertEquals(TEST_DECK_ID, deckDTO.getId());
        assertEquals(TEST_USER_ID, deckDTO.getUserId());
        assertEquals("", deckDTO.getName());
        assertEquals(emptyFlashcards, deckDTO.getFlashcards());
        assertTrue(deckDTO.getFlashcards().isEmpty());
    }

    @Test
    void flashcardDeckDTOHandlesZeroCount() {
        // Arrange & Act
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO(TEST_DECK_ID, TEST_USER_ID, TEST_DECK_NAME, 0);

        // Assert
        assertEquals(TEST_DECK_ID, deckDTO.getId());
        assertEquals(TEST_USER_ID, deckDTO.getUserId());
        assertEquals(TEST_DECK_NAME, deckDTO.getName());
        assertNull(deckDTO.getFlashcards());
        assertEquals(0, deckDTO.getFlashcardCount());
    }

    @Test
    void flashcardDeckDTOHandlesNegativeCount() {
        // Arrange & Act
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO(TEST_DECK_ID, TEST_USER_ID, TEST_DECK_NAME, -1);

        // Assert
        assertEquals(TEST_DECK_ID, deckDTO.getId());
        assertEquals(TEST_USER_ID, deckDTO.getUserId());
        assertEquals(TEST_DECK_NAME, deckDTO.getName());
        assertNull(deckDTO.getFlashcards());
        assertEquals(-1, deckDTO.getFlashcardCount());
    }

    @Test
    void flashcardDeckDTOHandlesLargeDeckName() {
        // Arrange
        String longDeckName = "Very Long Deck Name ".repeat(20);

        // Act
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO(TEST_DECK_ID, TEST_USER_ID, longDeckName, TEST_FLASHCARD_COUNT);

        // Assert
        assertEquals(TEST_DECK_ID, deckDTO.getId());
        assertEquals(TEST_USER_ID, deckDTO.getUserId());
        assertEquals(longDeckName, deckDTO.getName());
        assertTrue(deckDTO.getName().length() > 300);
        assertEquals(TEST_FLASHCARD_COUNT, deckDTO.getFlashcardCount());
    }

    @Test
    void flashcardDeckDTOHandlesSpecialCharactersInName() {
        // Arrange
        String nameWithSpecialChars = "DevOps & Cloud ☁️ テスト @#$%^&*()";

        // Act
        FlashcardDeckDTO deckDTO = new FlashcardDeckDTO(TEST_DECK_ID, TEST_USER_ID, nameWithSpecialChars, TEST_FLASHCARD_COUNT);

        // Assert
        assertEquals(TEST_DECK_ID, deckDTO.getId());
        assertEquals(TEST_USER_ID, deckDTO.getUserId());
        assertEquals(nameWithSpecialChars, deckDTO.getName());
        assertEquals(TEST_FLASHCARD_COUNT, deckDTO.getFlashcardCount());
    }
}
