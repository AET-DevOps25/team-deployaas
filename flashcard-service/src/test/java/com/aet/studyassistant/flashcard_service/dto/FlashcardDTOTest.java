package com.aet.studyassistant.flashcard_service.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FlashcardDTOTest {

    private static final UUID TEST_FLASHCARD_ID = UUID.randomUUID();
    private static final UUID TEST_DECK_ID = UUID.randomUUID();
    private static final String TEST_FRONT = "What is DevOps?";
    private static final String TEST_BACK = "DevOps is a set of practices that combines software development and IT operations.";

    @Test
    void defaultConstructorCreatesEmptyObject() {
        // Arrange & Act
        FlashcardDTO flashcardDTO = new FlashcardDTO();

        // Assert
        assertNull(flashcardDTO.getId());
        assertNull(flashcardDTO.getDeckId());
        assertNull(flashcardDTO.getFront());
        assertNull(flashcardDTO.getBack());
    }

    @Test
    void parameterizedConstructorCreatesCorrectObject() {
        // Arrange & Act
        FlashcardDTO flashcardDTO = new FlashcardDTO(TEST_FLASHCARD_ID, TEST_DECK_ID, TEST_FRONT, TEST_BACK);

        // Assert
        assertEquals(TEST_FLASHCARD_ID, flashcardDTO.getId());
        assertEquals(TEST_DECK_ID, flashcardDTO.getDeckId());
        assertEquals(TEST_FRONT, flashcardDTO.getFront());
        assertEquals(TEST_BACK, flashcardDTO.getBack());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        // Arrange
        FlashcardDTO flashcardDTO = new FlashcardDTO();

        // Act
        flashcardDTO.setId(TEST_FLASHCARD_ID);
        flashcardDTO.setDeckId(TEST_DECK_ID);
        flashcardDTO.setFront(TEST_FRONT);
        flashcardDTO.setBack(TEST_BACK);

        // Assert
        assertEquals(TEST_FLASHCARD_ID, flashcardDTO.getId());
        assertEquals(TEST_DECK_ID, flashcardDTO.getDeckId());
        assertEquals(TEST_FRONT, flashcardDTO.getFront());
        assertEquals(TEST_BACK, flashcardDTO.getBack());
    }

    @Test
    void gettersReturnCorrectValues() {
        // Arrange
        FlashcardDTO flashcardDTO = new FlashcardDTO(TEST_FLASHCARD_ID, TEST_DECK_ID, TEST_FRONT, TEST_BACK);

        // Act & Assert
        assertEquals(TEST_FLASHCARD_ID, flashcardDTO.getId());
        assertEquals(TEST_DECK_ID, flashcardDTO.getDeckId());
        assertEquals(TEST_FRONT, flashcardDTO.getFront());
        assertEquals(TEST_BACK, flashcardDTO.getBack());
    }

    @Test
    void flashcardDTOHandlesNullValues() {
        // Arrange & Act
        FlashcardDTO flashcardDTO = new FlashcardDTO(null, null, null, null);

        // Assert
        assertNull(flashcardDTO.getId());
        assertNull(flashcardDTO.getDeckId());
        assertNull(flashcardDTO.getFront());
        assertNull(flashcardDTO.getBack());
    }

    @Test
    void flashcardDTOHandlesEmptyStrings() {
        // Arrange & Act
        FlashcardDTO flashcardDTO = new FlashcardDTO(TEST_FLASHCARD_ID, TEST_DECK_ID, "", "");

        // Assert
        assertEquals(TEST_FLASHCARD_ID, flashcardDTO.getId());
        assertEquals(TEST_DECK_ID, flashcardDTO.getDeckId());
        assertEquals("", flashcardDTO.getFront());
        assertEquals("", flashcardDTO.getBack());
    }

    @Test
    void flashcardDTOHandlesLongText() {
        // Arrange
        String longFront = "This is a very long front text that might be used for complex questions in flashcards. ".repeat(10);
        String longBack = "This is a very long back text that might contain detailed explanations or solutions. ".repeat(10);

        // Act
        FlashcardDTO flashcardDTO = new FlashcardDTO(TEST_FLASHCARD_ID, TEST_DECK_ID, longFront, longBack);

        // Assert
        assertEquals(TEST_FLASHCARD_ID, flashcardDTO.getId());
        assertEquals(TEST_DECK_ID, flashcardDTO.getDeckId());
        assertEquals(longFront, flashcardDTO.getFront());
        assertEquals(longBack, flashcardDTO.getBack());
        assertTrue(flashcardDTO.getFront().length() > 500);
        assertTrue(flashcardDTO.getBack().length() > 500);
    }

    @Test
    void flashcardDTOHandlesSpecialCharacters() {
        // Arrange
        String frontWithSpecialChars = "What is CI/CD? (特殊文字テスト) & symbols: @#$%^&*()";
        String backWithSpecialChars = "Continuous Integration/Deployment 🚀 with emojis & 特殊文字";

        // Act
        FlashcardDTO flashcardDTO = new FlashcardDTO(TEST_FLASHCARD_ID, TEST_DECK_ID, frontWithSpecialChars, backWithSpecialChars);

        // Assert
        assertEquals(TEST_FLASHCARD_ID, flashcardDTO.getId());
        assertEquals(TEST_DECK_ID, flashcardDTO.getDeckId());
        assertEquals(frontWithSpecialChars, flashcardDTO.getFront());
        assertEquals(backWithSpecialChars, flashcardDTO.getBack());
    }
}
