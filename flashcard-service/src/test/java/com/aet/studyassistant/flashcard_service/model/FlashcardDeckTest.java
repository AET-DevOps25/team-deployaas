package com.aet.studyassistant.flashcard_service.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FlashcardDeckTest {

    private static final String TEST_DECK_NAME = "DevOps Fundamentals";
    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final UUID TEST_DECK_ID = UUID.randomUUID();

    @Test
    void defaultConstructorCreatesValidFlashcardDeck() {
        // Arrange & Act
        FlashcardDeck deck = new FlashcardDeck();

        // Assert
        assertNull(deck.getId());
        assertNull(deck.getUserId());
        assertNull(deck.getName());
        assertNull(deck.getFlashcards());
    }

    @Test
    void parameterizedConstructorCreatesCorrectFlashcardDeck() {
        // Arrange & Act
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);

        // Assert
        assertNull(deck.getId());
        assertEquals(TEST_USER_ID, deck.getUserId());
        assertEquals(TEST_DECK_NAME, deck.getName());
        assertNull(deck.getFlashcards());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck();
        Flashcard flashcard1 = new Flashcard("Front 1", "Back 1", deck);
        Flashcard flashcard2 = new Flashcard("Front 2", "Back 2", deck);
        List<Flashcard> flashcards = Arrays.asList(flashcard1, flashcard2);

        // Act
        deck.setId(TEST_DECK_ID);
        deck.setUserId(TEST_USER_ID);
        deck.setName(TEST_DECK_NAME);
        deck.setFlashcards(flashcards);

        // Assert
        assertEquals(TEST_DECK_ID, deck.getId());
        assertEquals(TEST_USER_ID, deck.getUserId());
        assertEquals(TEST_DECK_NAME, deck.getName());
        assertEquals(flashcards, deck.getFlashcards());
        assertEquals(2, deck.getFlashcards().size());
    }

    @Test
    void gettersReturnCorrectValues() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        deck.setId(TEST_DECK_ID);
        Flashcard flashcard = new Flashcard("Front", "Back", deck);
        List<Flashcard> flashcards = Collections.singletonList(flashcard);
        deck.setFlashcards(flashcards);

        // Act & Assert
        assertEquals(TEST_DECK_ID, deck.getId());
        assertEquals(TEST_USER_ID, deck.getUserId());
        assertEquals(TEST_DECK_NAME, deck.getName());
        assertEquals(flashcards, deck.getFlashcards());
        assertEquals(1, deck.getFlashcards().size());
    }

    @Test
    void flashcardDeckHandlesNullUserId() {
        // Arrange & Act
        FlashcardDeck deck = new FlashcardDeck(null, TEST_DECK_NAME);

        // Assert
        assertNull(deck.getUserId());
        assertEquals(TEST_DECK_NAME, deck.getName());
    }

    @Test
    void flashcardDeckHandlesNullName() {
        // Arrange & Act
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, null);

        // Assert
        assertEquals(TEST_USER_ID, deck.getUserId());
        assertNull(deck.getName());
    }

    @Test
    void flashcardDeckHandlesEmptyName() {
        // Arrange & Act
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, "");

        // Assert
        assertEquals(TEST_USER_ID, deck.getUserId());
        assertEquals("", deck.getName());
    }

    @Test
    void flashcardDeckHandlesEmptyFlashcardsList() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        List<Flashcard> emptyFlashcards = Collections.emptyList();

        // Act
        deck.setFlashcards(emptyFlashcards);

        // Assert
        assertEquals(TEST_USER_ID, deck.getUserId());
        assertEquals(TEST_DECK_NAME, deck.getName());
        assertEquals(emptyFlashcards, deck.getFlashcards());
        assertTrue(deck.getFlashcards().isEmpty());
    }

    @Test
    void flashcardDeckHandlesNullFlashcardsList() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);

        // Act
        deck.setFlashcards(null);

        // Assert
        assertEquals(TEST_USER_ID, deck.getUserId());
        assertEquals(TEST_DECK_NAME, deck.getName());
        assertNull(deck.getFlashcards());
    }

    @Test
    void flashcardDeckHandlesBothNullParameters() {
        // Arrange & Act
        FlashcardDeck deck = new FlashcardDeck(null, null);

        // Assert
        assertNull(deck.getUserId());
        assertNull(deck.getName());
        assertNull(deck.getFlashcards());
    }

    @Test
    void flashcardDeckWithMultipleFlashcards() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        Flashcard flashcard1 = new Flashcard("What is CI/CD?", "Continuous Integration/Continuous Deployment", deck);
        Flashcard flashcard2 = new Flashcard("What is Docker?", "A containerization platform", deck);
        Flashcard flashcard3 = new Flashcard("What is Kubernetes?", "A container orchestration platform", deck);
        List<Flashcard> flashcards = Arrays.asList(flashcard1, flashcard2, flashcard3);

        // Act
        deck.setFlashcards(flashcards);

        // Assert
        assertEquals(3, deck.getFlashcards().size());
        assertEquals("What is CI/CD?", deck.getFlashcards().get(0).getFront());
        assertEquals("What is Docker?", deck.getFlashcards().get(1).getFront());
        assertEquals("What is Kubernetes?", deck.getFlashcards().get(2).getFront());
    }
}
