package com.aet.studyassistant.flashcard_service.controller;

import com.aet.studyassistant.flashcard_service.dto.FlashcardDTO;
import com.aet.studyassistant.flashcard_service.dto.FlashcardDeckDTO;
import com.aet.studyassistant.flashcard_service.service.FlashcardService;
import com.aet.studyassistant.flashcard_service.service.QuizServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/flashcard")
public class FlashcardController {

    private final FlashcardService flashcardService;
    private final QuizServiceClient quizServiceClient;

    @Autowired
    public FlashcardController(FlashcardService flashcardService, QuizServiceClient quizServiceClient) {
        this.flashcardService = flashcardService;
        this.quizServiceClient = quizServiceClient;
    }

    @GetMapping("/test")
    public String testConnection() {
        return "Flashcard Service is connected successfully!";
    }

    // Deck endpoints
    @GetMapping("/decks/user/{userId}")
    public List<FlashcardDeckDTO> getUserDecks(@PathVariable UUID userId) {
        return flashcardService.getUserDecks(userId);
    }

    @GetMapping("/decks/{deckId}")
    public ResponseEntity<FlashcardDeckDTO> getDeck(@PathVariable UUID deckId) {
        Optional<FlashcardDeckDTO> deck = flashcardService.getDeckById(deckId);
        return deck.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/decks")
    public ResponseEntity<FlashcardDeckDTO> createDeck(@RequestBody Map<String, Object> request) {
        try {
            UUID userId = UUID.fromString(request.get("userId").toString());
            String name = request.get("name").toString();
            FlashcardDeckDTO deck = flashcardService.createDeck(userId, name);
            return ResponseEntity.ok(deck);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/decks/{deckId}")
    public ResponseEntity<FlashcardDeckDTO> updateDeck(@PathVariable UUID deckId, 
                                                       @RequestBody Map<String, String> request) {
        String name = request.get("name");
        Optional<FlashcardDeckDTO> updatedDeck = flashcardService.updateDeck(deckId, name);
        return updatedDeck.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/decks/{deckId}")
    public ResponseEntity<Void> deleteDeck(@PathVariable UUID deckId) {
        boolean deleted = flashcardService.deleteDeck(deckId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Flashcard endpoints
    @GetMapping("/decks/{deckId}/flashcards")
    public List<FlashcardDTO> getDeckFlashcards(@PathVariable UUID deckId) {
        return flashcardService.getDeckFlashcards(deckId);
    }

    @PostMapping("/decks/{deckId}/flashcards")
    public ResponseEntity<FlashcardDTO> createFlashcard(@PathVariable UUID deckId,
                                                        @RequestBody Map<String, String> request) {
        String front = request.get("front");
        String back = request.get("back");
        Optional<FlashcardDTO> flashcard = flashcardService.createFlashcard(deckId, front, back);
        return flashcard.map(ResponseEntity::ok).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/flashcards/{flashcardId}")
    public ResponseEntity<FlashcardDTO> updateFlashcard(@PathVariable UUID flashcardId,
                                                        @RequestBody Map<String, String> request) {
        String front = request.get("front");
        String back = request.get("back");
        Optional<FlashcardDTO> flashcard = flashcardService.updateFlashcard(flashcardId, front, back);
        return flashcard.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/flashcards/{flashcardId}")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable UUID flashcardId) {
        boolean deleted = flashcardService.deleteFlashcard(flashcardId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Generate flashcards from quiz content
    @PostMapping("/generate/chapter/{chapterId}")
    public ResponseEntity<FlashcardDeckDTO> generateFromChapter(@PathVariable UUID chapterId,
                                                                @RequestBody Map<String, Object> request) {
        try {
            UUID userId = UUID.fromString(request.get("userId").toString());
            String deckName = request.getOrDefault("deckName", "Chapter Flashcards").toString();
            
            Optional<FlashcardDeckDTO> deck = flashcardService.generateFlashcardsFromChapter(userId, chapterId, deckName);
            return deck.map(ResponseEntity::ok).orElse(ResponseEntity.badRequest().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Check quiz service connectivity
    @GetMapping("/quiz/health")
    public ResponseEntity<Map<String, Boolean>> checkQuizServiceHealth() {
        boolean isAvailable = quizServiceClient.isQuizServiceAvailable();
        return ResponseEntity.ok(Map.of("quizServiceAvailable", isAvailable));
    }
}
