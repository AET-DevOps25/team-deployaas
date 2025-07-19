package com.aet.studyassistant.flashcard_service.service;

import com.aet.studyassistant.flashcard_service.dto.FlashcardDTO;
import com.aet.studyassistant.flashcard_service.dto.FlashcardDeckDTO;
import com.aet.studyassistant.flashcard_service.model.Flashcard;
import com.aet.studyassistant.flashcard_service.model.FlashcardDeck;
import com.aet.studyassistant.flashcard_service.repository.FlashcardDeckRepository;
import com.aet.studyassistant.flashcard_service.repository.FlashcardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashcardServiceTest {

    @Mock
    private FlashcardDeckRepository deckRepository;

    @Mock
    private FlashcardRepository flashcardRepository;

    @InjectMocks
    private FlashcardService flashcardService;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final UUID TEST_DECK_ID = UUID.randomUUID();
    private static final UUID TEST_FLASHCARD_ID = UUID.randomUUID();
    private static final UUID TEST_CHAPTER_ID = UUID.randomUUID();
    private static final String TEST_DECK_NAME = "DevOps Fundamentals";
    private static final String TEST_FRONT = "What is DevOps?";
    private static final String TEST_BACK = "DevOps combines software development and IT operations";

    @Test
    void getUserDecksReturnsUserDecks() {
        // Arrange
        FlashcardDeck deck1 = new FlashcardDeck(TEST_USER_ID, "Deck 1");
        deck1.setId(UUID.randomUUID());
        deck1.setFlashcards(Collections.emptyList());

        FlashcardDeck deck2 = new FlashcardDeck(TEST_USER_ID, "Deck 2");
        deck2.setId(UUID.randomUUID());
        deck2.setFlashcards(Collections.emptyList());

        List<FlashcardDeck> decks = Arrays.asList(deck1, deck2);
        when(deckRepository.findByUserId(TEST_USER_ID)).thenReturn(decks);

        // Act
        List<FlashcardDeckDTO> result = flashcardService.getUserDecks(TEST_USER_ID);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Deck 1", result.get(0).getName());
        assertEquals("Deck 2", result.get(1).getName());
        assertEquals(TEST_USER_ID, result.get(0).getUserId());
        assertEquals(TEST_USER_ID, result.get(1).getUserId());
        verify(deckRepository).findByUserId(TEST_USER_ID);
    }

    @Test
    void getUserDecksWhenNoDecksReturnsEmptyList() {
        // Arrange
        when(deckRepository.findByUserId(TEST_USER_ID)).thenReturn(Collections.emptyList());

        // Act
        List<FlashcardDeckDTO> result = flashcardService.getUserDecks(TEST_USER_ID);

        // Assert
        assertTrue(result.isEmpty());
        verify(deckRepository).findByUserId(TEST_USER_ID);
    }

    @Test
    void getDeckByIdWhenDeckExistsReturnsDeck() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        deck.setId(TEST_DECK_ID);
        
        Flashcard flashcard = new Flashcard(TEST_FRONT, TEST_BACK, deck);
        flashcard.setId(TEST_FLASHCARD_ID);
        deck.setFlashcards(Collections.singletonList(flashcard));

        when(deckRepository.findById(TEST_DECK_ID)).thenReturn(Optional.of(deck));

        // Act
        Optional<FlashcardDeckDTO> result = flashcardService.getDeckById(TEST_DECK_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_DECK_ID, result.get().getId());
        assertEquals(TEST_DECK_NAME, result.get().getName());
        assertEquals(1, result.get().getFlashcards().size());
        assertEquals(TEST_FRONT, result.get().getFlashcards().get(0).getFront());
        verify(deckRepository).findById(TEST_DECK_ID);
    }

    @Test
    void getDeckByIdWhenDeckDoesNotExistReturnsEmpty() {
        // Arrange
        when(deckRepository.findById(TEST_DECK_ID)).thenReturn(Optional.empty());

        // Act
        Optional<FlashcardDeckDTO> result = flashcardService.getDeckById(TEST_DECK_ID);

        // Assert
        assertFalse(result.isPresent());
        verify(deckRepository).findById(TEST_DECK_ID);
    }

    @Test
    void createDeckCreatesAndReturnsNewDeck() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        FlashcardDeck savedDeck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        savedDeck.setId(TEST_DECK_ID);
        savedDeck.setFlashcards(Collections.emptyList());

        when(deckRepository.save(any(FlashcardDeck.class))).thenReturn(savedDeck);

        // Act
        FlashcardDeckDTO result = flashcardService.createDeck(TEST_USER_ID, TEST_DECK_NAME);

        // Assert
        assertEquals(TEST_DECK_ID, result.getId());
        assertEquals(TEST_USER_ID, result.getUserId());
        assertEquals(TEST_DECK_NAME, result.getName());
        assertEquals(0, result.getFlashcardCount());
        verify(deckRepository).save(any(FlashcardDeck.class));
    }

    @Test
    void updateDeckWhenDeckExistsUpdatesAndReturnsUpdatedDeck() {
        // Arrange
        String newName = "Updated Deck Name";
        FlashcardDeck existingDeck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        existingDeck.setId(TEST_DECK_ID);
        existingDeck.setFlashcards(Collections.emptyList());

        FlashcardDeck updatedDeck = new FlashcardDeck(TEST_USER_ID, newName);
        updatedDeck.setId(TEST_DECK_ID);
        updatedDeck.setFlashcards(Collections.emptyList());

        when(deckRepository.findById(TEST_DECK_ID)).thenReturn(Optional.of(existingDeck));
        when(deckRepository.save(any(FlashcardDeck.class))).thenReturn(updatedDeck);

        // Act
        Optional<FlashcardDeckDTO> result = flashcardService.updateDeck(TEST_DECK_ID, newName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_DECK_ID, result.get().getId());
        assertEquals(newName, result.get().getName());
        verify(deckRepository).findById(TEST_DECK_ID);
        verify(deckRepository).save(any(FlashcardDeck.class));
    }

    @Test
    void updateDeckWhenDeckDoesNotExistReturnsEmpty() {
        // Arrange
        when(deckRepository.findById(TEST_DECK_ID)).thenReturn(Optional.empty());

        // Act
        Optional<FlashcardDeckDTO> result = flashcardService.updateDeck(TEST_DECK_ID, "New Name");

        // Assert
        assertFalse(result.isPresent());
        verify(deckRepository).findById(TEST_DECK_ID);
        verify(deckRepository, never()).save(any());
    }

    @Test
    void deleteDeckWhenDeckExistsReturnsTrue() {
        // Arrange
        when(deckRepository.existsById(TEST_DECK_ID)).thenReturn(true);

        // Act
        boolean result = flashcardService.deleteDeck(TEST_DECK_ID);

        // Assert
        assertTrue(result);
        verify(deckRepository).existsById(TEST_DECK_ID);
        verify(deckRepository).deleteById(TEST_DECK_ID);
    }

    @Test
    void deleteDeckWhenDeckDoesNotExistReturnsFalse() {
        // Arrange
        when(deckRepository.existsById(TEST_DECK_ID)).thenReturn(false);

        // Act
        boolean result = flashcardService.deleteDeck(TEST_DECK_ID);

        // Assert
        assertFalse(result);
        verify(deckRepository).existsById(TEST_DECK_ID);
        verify(deckRepository, never()).deleteById(any());
    }

    @Test
    void getDeckFlashcardsReturnsFlashcardsForDeck() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        deck.setId(TEST_DECK_ID);

        Flashcard flashcard1 = new Flashcard("Front 1", "Back 1", deck);
        flashcard1.setId(UUID.randomUUID());
        Flashcard flashcard2 = new Flashcard("Front 2", "Back 2", deck);
        flashcard2.setId(UUID.randomUUID());

        List<Flashcard> flashcards = Arrays.asList(flashcard1, flashcard2);
        when(flashcardRepository.findByDeckId(TEST_DECK_ID)).thenReturn(flashcards);

        // Act
        List<FlashcardDTO> result = flashcardService.getDeckFlashcards(TEST_DECK_ID);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Front 1", result.get(0).getFront());
        assertEquals("Back 1", result.get(0).getBack());
        assertEquals("Front 2", result.get(1).getFront());
        assertEquals("Back 2", result.get(1).getBack());
        verify(flashcardRepository).findByDeckId(TEST_DECK_ID);
    }

    @Test
    void createFlashcardWhenDeckExistsCreatesAndReturnsFlashcard() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        deck.setId(TEST_DECK_ID);

        Flashcard savedFlashcard = new Flashcard(TEST_FRONT, TEST_BACK, deck);
        savedFlashcard.setId(TEST_FLASHCARD_ID);

        when(deckRepository.findById(TEST_DECK_ID)).thenReturn(Optional.of(deck));
        when(flashcardRepository.save(any(Flashcard.class))).thenReturn(savedFlashcard);

        // Act
        Optional<FlashcardDTO> result = flashcardService.createFlashcard(TEST_DECK_ID, TEST_FRONT, TEST_BACK);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_FLASHCARD_ID, result.get().getId());
        assertEquals(TEST_DECK_ID, result.get().getDeckId());
        assertEquals(TEST_FRONT, result.get().getFront());
        assertEquals(TEST_BACK, result.get().getBack());
        verify(deckRepository).findById(TEST_DECK_ID);
        verify(flashcardRepository).save(any(Flashcard.class));
    }

    @Test
    void createFlashcardWhenDeckDoesNotExistReturnsEmpty() {
        // Arrange
        when(deckRepository.findById(TEST_DECK_ID)).thenReturn(Optional.empty());

        // Act
        Optional<FlashcardDTO> result = flashcardService.createFlashcard(TEST_DECK_ID, TEST_FRONT, TEST_BACK);

        // Assert
        assertFalse(result.isPresent());
        verify(deckRepository).findById(TEST_DECK_ID);
        verify(flashcardRepository, never()).save(any());
    }

    @Test
    void updateFlashcardWhenFlashcardExistsUpdatesAndReturnsFlashcard() {
        // Arrange
        String newFront = "Updated Front";
        String newBack = "Updated Back";
        
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        deck.setId(TEST_DECK_ID);

        Flashcard existingFlashcard = new Flashcard(TEST_FRONT, TEST_BACK, deck);
        existingFlashcard.setId(TEST_FLASHCARD_ID);

        Flashcard updatedFlashcard = new Flashcard(newFront, newBack, deck);
        updatedFlashcard.setId(TEST_FLASHCARD_ID);

        when(flashcardRepository.findById(TEST_FLASHCARD_ID)).thenReturn(Optional.of(existingFlashcard));
        when(flashcardRepository.save(any(Flashcard.class))).thenReturn(updatedFlashcard);

        // Act
        Optional<FlashcardDTO> result = flashcardService.updateFlashcard(TEST_FLASHCARD_ID, newFront, newBack);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_FLASHCARD_ID, result.get().getId());
        assertEquals(newFront, result.get().getFront());
        assertEquals(newBack, result.get().getBack());
        verify(flashcardRepository).findById(TEST_FLASHCARD_ID);
        verify(flashcardRepository).save(any(Flashcard.class));
    }

    @Test
    void updateFlashcardWhenFlashcardDoesNotExistReturnsEmpty() {
        // Arrange
        when(flashcardRepository.findById(TEST_FLASHCARD_ID)).thenReturn(Optional.empty());

        // Act
        Optional<FlashcardDTO> result = flashcardService.updateFlashcard(TEST_FLASHCARD_ID, "New Front", "New Back");

        // Assert
        assertFalse(result.isPresent());
        verify(flashcardRepository).findById(TEST_FLASHCARD_ID);
        verify(flashcardRepository, never()).save(any());
    }

    @Test
    void getFlashcardByIdWhenFlashcardExistsReturnsFlashcard() {
        // Arrange
        FlashcardDeck deck = new FlashcardDeck(TEST_USER_ID, TEST_DECK_NAME);
        deck.setId(TEST_DECK_ID);

        Flashcard flashcard = new Flashcard(TEST_FRONT, TEST_BACK, deck);
        flashcard.setId(TEST_FLASHCARD_ID);

        when(flashcardRepository.findById(TEST_FLASHCARD_ID)).thenReturn(Optional.of(flashcard));

        // Act
        Optional<FlashcardDTO> result = flashcardService.getFlashcardById(TEST_FLASHCARD_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_FLASHCARD_ID, result.get().getId());
        assertEquals(TEST_FRONT, result.get().getFront());
        assertEquals(TEST_BACK, result.get().getBack());
        verify(flashcardRepository).findById(TEST_FLASHCARD_ID);
    }

    @Test
    void getFlashcardByIdWhenFlashcardDoesNotExistReturnsEmpty() {
        // Arrange
        when(flashcardRepository.findById(TEST_FLASHCARD_ID)).thenReturn(Optional.empty());

        // Act
        Optional<FlashcardDTO> result = flashcardService.getFlashcardById(TEST_FLASHCARD_ID);

        // Assert
        assertFalse(result.isPresent());
        verify(flashcardRepository).findById(TEST_FLASHCARD_ID);
    }

    @Test
    void deleteFlashcardWhenFlashcardExistsReturnsTrue() {
        // Arrange
        when(flashcardRepository.existsById(TEST_FLASHCARD_ID)).thenReturn(true);

        // Act
        boolean result = flashcardService.deleteFlashcard(TEST_FLASHCARD_ID);

        // Assert
        assertTrue(result);
        verify(flashcardRepository).existsById(TEST_FLASHCARD_ID);
        verify(flashcardRepository).deleteById(TEST_FLASHCARD_ID);
    }

    @Test
    void deleteFlashcardWhenFlashcardDoesNotExistReturnsFalse() {
        // Arrange
        when(flashcardRepository.existsById(TEST_FLASHCARD_ID)).thenReturn(false);

        // Act
        boolean result = flashcardService.deleteFlashcard(TEST_FLASHCARD_ID);

        // Assert
        assertFalse(result);
        verify(flashcardRepository).existsById(TEST_FLASHCARD_ID);
        verify(flashcardRepository, never()).deleteById(any());
    }
}
