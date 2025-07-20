package com.aet.studyassistant.flashcard_service.service;

import com.aet.studyassistant.flashcard_service.dto.FlashcardDTO;
import com.aet.studyassistant.flashcard_service.dto.FlashcardDeckDTO;
import com.aet.studyassistant.flashcard_service.model.Flashcard;
import com.aet.studyassistant.flashcard_service.model.FlashcardDeck;
import com.aet.studyassistant.flashcard_service.repository.FlashcardDeckRepository;
import com.aet.studyassistant.flashcard_service.repository.FlashcardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FlashcardService {
    
    private final FlashcardDeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;

    @Autowired
    public FlashcardService(FlashcardDeckRepository deckRepository, 
                           FlashcardRepository flashcardRepository) {
        this.deckRepository = deckRepository;
        this.flashcardRepository = flashcardRepository;
    }

    // Deck operations
    public List<FlashcardDeckDTO> getUserDecks(UUID userId) {
        List<FlashcardDeck> decks = deckRepository.findByUserId(userId);
        return decks.stream()
                .map(this::convertDeckToDTO)
                .collect(Collectors.toList());
    }

    public Optional<FlashcardDeckDTO> getDeckById(UUID deckId) {
        Optional<FlashcardDeck> deck = deckRepository.findById(deckId);
        return deck.map(this::convertDeckToDTOWithFlashcards);
    }

    public FlashcardDeckDTO createDeck(UUID userId, String name) {
        FlashcardDeck deck = new FlashcardDeck(userId, name);
        FlashcardDeck savedDeck = deckRepository.save(deck);
        return convertDeckToDTO(savedDeck);
    }

    public Optional<FlashcardDeckDTO> updateDeck(UUID deckId, String name) {
        Optional<FlashcardDeck> deckOpt = deckRepository.findById(deckId);
        if (deckOpt.isPresent()) {
            FlashcardDeck deck = deckOpt.get();
            deck.setName(name);
            FlashcardDeck savedDeck = deckRepository.save(deck);
            return Optional.of(convertDeckToDTO(savedDeck));
        }
        return Optional.empty();
    }

    public boolean deleteDeck(UUID deckId) {
        if (deckRepository.existsById(deckId)) {
            deckRepository.deleteById(deckId);
            return true;
        }
        return false;
    }

    // Flashcard operations
    public List<FlashcardDTO> getDeckFlashcards(UUID deckId) {
        List<Flashcard> flashcards = flashcardRepository.findByDeckId(deckId);
        return flashcards.stream()
                .map(this::convertFlashcardToDTO)
                .collect(Collectors.toList());
    }

    public Optional<FlashcardDTO> createFlashcard(UUID deckId, String front, String back) {
        Optional<FlashcardDeck> deckOpt = deckRepository.findById(deckId);
        if (deckOpt.isPresent()) {
            FlashcardDeck deck = deckOpt.get();
            Flashcard flashcard = new Flashcard(front, back, deck);
            Flashcard savedFlashcard = flashcardRepository.save(flashcard);
            return Optional.of(convertFlashcardToDTO(savedFlashcard));
        }
        return Optional.empty();
    }

    public Optional<FlashcardDTO> updateFlashcard(UUID flashcardId, String front, String back) {
        Optional<Flashcard> flashcardOpt = flashcardRepository.findById(flashcardId);
        if (flashcardOpt.isPresent()) {
            Flashcard flashcard = flashcardOpt.get();
            flashcard.setFront(front);
            flashcard.setBack(back);
            Flashcard savedFlashcard = flashcardRepository.save(flashcard);
            return Optional.of(convertFlashcardToDTO(savedFlashcard));
        }
        return Optional.empty();
    }

    public Optional<FlashcardDTO> getFlashcardById(UUID flashcardId) {
        Optional<Flashcard> flashcard = flashcardRepository.findById(flashcardId);
        return flashcard.map(this::convertFlashcardToDTO);
    }

    public boolean deleteFlashcard(UUID flashcardId) {
        if (flashcardRepository.existsById(flashcardId)) {
            flashcardRepository.deleteById(flashcardId);
            return true;
        }
        return false;
    }

    // Helper methods
    private FlashcardDeckDTO convertDeckToDTO(FlashcardDeck deck) {
        int flashcardCount = deck.getFlashcards() != null ? deck.getFlashcards().size() : 0;
        return new FlashcardDeckDTO(deck.getId(), deck.getUserId(), deck.getName(), flashcardCount);
    }

    private FlashcardDeckDTO convertDeckToDTOWithFlashcards(FlashcardDeck deck) {
        List<FlashcardDTO> flashcardDTOs = deck.getFlashcards() != null ? 
                deck.getFlashcards().stream()
                        .map(this::convertFlashcardToDTO)
                        .collect(Collectors.toList()) : 
                List.of();
        
        return new FlashcardDeckDTO(deck.getId(), deck.getUserId(), deck.getName(), flashcardDTOs);
    }

    private FlashcardDTO convertFlashcardToDTO(Flashcard flashcard) {
        return new FlashcardDTO(
                flashcard.getId(),
                flashcard.getDeck().getId(),
                flashcard.getFront(),
                flashcard.getBack()
        );
    }
}