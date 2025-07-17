package com.aet.studyassistant.flashcard_service.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FlashcardTest {

    private static final String TEST_FRONT = "What is DevOps?";
    private static final String TEST_BACK = "DevOps is a set of practices that combines software development and IT operations.";
    private static final UUID TEST_FLASHCARD_ID = UUID.randomUUID();
    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_DECK_NAME = "Test Deck";

    @Test
    void defaultConstructorCreatesValidFlashcard() {
        // Arrange & Act
        Flashcard flashcard = new Flashcard();

        // Assert
        assertNull(flashcard.getId());
        assertNull(flashcard.getFront());
        assertNull(flashcard.getBack());
        assertNull(flashcard.getDeck());
    }

    @Test
    void parameterizedConstructorCreatesCorrectFlashcard() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);

        // Act
        Flashcard flashcard = new Flashcard(TEST_FRONT, TEST_BACK, deck);

        // Assert
        assertNull(flashcard.getId());
        assertEquals(TEST_FRONT, flashcard.getFront());
        assertEquals(TEST_BACK, flashcard.getBack());
        assertEquals(deck, flashcard.getDeck());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        // Arrange
        Flashcard flashcard = new Flashcard();
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);

        // Act
        flashcard.setId(TEST_FLASHCARD_ID);
        flashcard.setFront(TEST_FRONT);
        flashcard.setBack(TEST_BACK);
        flashcard.setDeck(deck);

        // Assert
        assertEquals(TEST_FLASHCARD_ID, flashcard.getId());
        assertEquals(TEST_FRONT, flashcard.getFront());
        assertEquals(TEST_BACK, flashcard.getBack());
        assertEquals(deck, flashcard.getDeck());
    }

    @Test
    void gettersReturnCorrectValues() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        Flashcard flashcard = new Flashcard(TEST_FRONT, TEST_BACK, deck);
        flashcard.setId(TEST_FLASHCARD_ID);

        // Act & Assert
        assertEquals(TEST_FLASHCARD_ID, flashcard.getId());
        assertEquals(TEST_FRONT, flashcard.getFront());
        assertEquals(TEST_BACK, flashcard.getBack());
        assertEquals(deck, flashcard.getDeck());
    }

    @Test
    void flashcardHandlesNullFront() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);

        // Act
        Flashcard flashcard = new Flashcard(null, TEST_BACK, deck);

        // Assert
        assertNull(flashcard.getFront());
        assertEquals(TEST_BACK, flashcard.getBack());
        assertEquals(deck, flashcard.getDeck());
    }

    @Test
    void flashcardHandlesNullBack() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);

        // Act
        Flashcard flashcard = new Flashcard(TEST_FRONT, null, deck);

        // Assert
        assertEquals(TEST_FRONT, flashcard.getFront());
        assertNull(flashcard.getBack());
        assertEquals(deck, flashcard.getDeck());
    }

    @Test
    void flashcardHandlesNullDeck() {
        // Arrange & Act
        Flashcard flashcard = new Flashcard(TEST_FRONT, TEST_BACK, null);

        // Assert
        assertEquals(TEST_FRONT, flashcard.getFront());
        assertEquals(TEST_BACK, flashcard.getBack());
        assertNull(flashcard.getDeck());
    }

    @Test
    void flashcardHandlesEmptyFront() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);

        // Act
        Flashcard flashcard = new Flashcard("", TEST_BACK, deck);

        // Assert
        assertEquals("", flashcard.getFront());
        assertEquals(TEST_BACK, flashcard.getBack());
        assertEquals(deck, flashcard.getDeck());
    }

    @Test
    void flashcardHandlesEmptyBack() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);

        // Act
        Flashcard flashcard = new Flashcard(TEST_FRONT, "", deck);

        // Assert
        assertEquals(TEST_FRONT, flashcard.getFront());
        assertEquals("", flashcard.getBack());
        assertEquals(deck, flashcard.getDeck());
    }

    @Test
    void flashcardHandlesAllNullParameters() {
        // Arrange & Act
        Flashcard flashcard = new Flashcard(null, null, null);

        // Assert
        assertNull(flashcard.getFront());
        assertNull(flashcard.getBack());
        assertNull(flashcard.getDeck());
    }
}
