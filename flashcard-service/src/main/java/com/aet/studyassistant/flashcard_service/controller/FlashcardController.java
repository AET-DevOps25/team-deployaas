package com.aet.studyassistant.flashcard_service.controller;

import com.aet.studyassistant.flashcard_service.dto.FlashcardDTO;
import com.aet.studyassistant.flashcard_service.dto.FlashcardDeckDTO;
import com.aet.studyassistant.flashcard_service.service.FlashcardService;
import com.aet.studyassistant.flashcard_service.service.TemplateService;
import com.aet.studyassistant.flashcard_service.service.QuizServiceClient;
import com.aet.studyassistant.flashcard_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/flashcard")
public class FlashcardController {

    private final FlashcardService flashcardService;
    private final TemplateService templateService;
    private final QuizServiceClient quizServiceClient;
    private final JwtUtil jwtUtil;

    @Autowired
    public FlashcardController(FlashcardService flashcardService, TemplateService templateService, QuizServiceClient quizServiceClient, JwtUtil jwtUtil) {
        this.flashcardService = flashcardService;
        this.templateService = templateService;
        this.quizServiceClient = quizServiceClient;
        this.jwtUtil = jwtUtil;
    }

    private UUID extractUserIdFromRequest(HttpServletRequest request) {
        String token = (String) request.getAttribute("jwt_token");
        if (token != null) {
            return jwtUtil.extractUserId(token);
        }
        return null;
    }

    @GetMapping("/test")
    public String testConnection() {
        return "Flashcard Service is connected successfully!";
    }

    @PostMapping("/setup-defaults/{userId}")
    public ResponseEntity<String> setupDefaultDecks(@PathVariable UUID userId) {
        try {
            templateService.createDefaultDecksForNewUser(userId);
            return ResponseEntity.ok("Default flashcard decks created successfully for user: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating default decks: " + e.getMessage());
        }
    }

    // Deck endpoints
    @GetMapping("/decks/user/{userId}")
    public ResponseEntity<List<FlashcardDeckDTO>> getUserDecks(@PathVariable UUID userId, HttpServletRequest request) {
        // Extract user ID from JWT token for security
        UUID authenticatedUserId = extractUserIdFromRequest(request);
        if (authenticatedUserId == null) {
            return ResponseEntity.status(401).build();
        }
        
        // For security, only allow users to access their own decks
        if (!authenticatedUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        List<FlashcardDeckDTO> decks = flashcardService.getUserDecks(userId);
        return ResponseEntity.ok(decks);
    }

    @GetMapping("/decks/user")
    public ResponseEntity<List<FlashcardDeckDTO>> getMyDecks(HttpServletRequest request) {
        UUID userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        List<FlashcardDeckDTO> decks = flashcardService.getUserDecks(userId);
        return ResponseEntity.ok(decks);
    }

    @GetMapping("/decks/{deckId}")
    public ResponseEntity<FlashcardDeckDTO> getDeck(@PathVariable UUID deckId, HttpServletRequest request) {
        UUID userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        Optional<FlashcardDeckDTO> deck = flashcardService.getDeckById(deckId);
        if (deck.isPresent()) {
            // Verify that the deck belongs to the authenticated user
            if (!deck.get().getUserId().equals(userId)) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok(deck.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/decks")
    public ResponseEntity<FlashcardDeckDTO> createDeck(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        UUID userId = extractUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            String name = request.get("name").toString();
            FlashcardDeckDTO deck = flashcardService.createDeck(userId, name);
            return ResponseEntity.ok(deck);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/decks/{deckId}")
    public ResponseEntity<FlashcardDeckDTO> updateDeck(@PathVariable UUID deckId, 
                                                       @RequestBody Map<String, String> request,
                                                       HttpServletRequest httpRequest) {
        UUID userId = extractUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Verify ownership before updating
        Optional<FlashcardDeckDTO> existingDeck = flashcardService.getDeckById(deckId);
        if (existingDeck.isEmpty() || !existingDeck.get().getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        String name = request.get("name");
        Optional<FlashcardDeckDTO> updatedDeck = flashcardService.updateDeck(deckId, name);
        return updatedDeck.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/decks/{deckId}")
    public ResponseEntity<Void> deleteDeck(@PathVariable UUID deckId, HttpServletRequest request) {
        UUID userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Verify ownership before deleting
        Optional<FlashcardDeckDTO> existingDeck = flashcardService.getDeckById(deckId);
        if (existingDeck.isEmpty() || !existingDeck.get().getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        boolean deleted = flashcardService.deleteDeck(deckId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Flashcard endpoints
    @GetMapping("/decks/{deckId}/flashcards")
    public ResponseEntity<List<FlashcardDTO>> getDeckFlashcards(@PathVariable UUID deckId, HttpServletRequest request) {
        UUID userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Verify deck ownership
        Optional<FlashcardDeckDTO> deck = flashcardService.getDeckById(deckId);
        if (deck.isEmpty() || !deck.get().getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        List<FlashcardDTO> flashcards = flashcardService.getDeckFlashcards(deckId);
        return ResponseEntity.ok(flashcards);
    }

    @PostMapping("/decks/{deckId}/flashcards")
    public ResponseEntity<FlashcardDTO> createFlashcard(@PathVariable UUID deckId,
                                                        @RequestBody Map<String, String> request,
                                                        HttpServletRequest httpRequest) {
        UUID userId = extractUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Verify deck ownership
        Optional<FlashcardDeckDTO> deck = flashcardService.getDeckById(deckId);
        if (deck.isEmpty() || !deck.get().getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        String front = request.get("front");
        String back = request.get("back");
        Optional<FlashcardDTO> flashcard = flashcardService.createFlashcard(deckId, front, back);
        return flashcard.map(ResponseEntity::ok).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/flashcards/{flashcardId}")
    public ResponseEntity<FlashcardDTO> updateFlashcard(@PathVariable UUID flashcardId,
                                                        @RequestBody Map<String, String> request,
                                                        HttpServletRequest httpRequest) {
        UUID userId = extractUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Get the flashcard to verify deck ownership
        Optional<FlashcardDTO> existingFlashcard = flashcardService.getFlashcardById(flashcardId);
        if (existingFlashcard.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify deck ownership
        Optional<FlashcardDeckDTO> deck = flashcardService.getDeckById(existingFlashcard.get().getDeckId());
        if (deck.isEmpty() || !deck.get().getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        String front = request.get("front");
        String back = request.get("back");
        Optional<FlashcardDTO> flashcard = flashcardService.updateFlashcard(flashcardId, front, back);
        return flashcard.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/flashcards/{flashcardId}")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable UUID flashcardId, HttpServletRequest request) {
        UUID userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Get the flashcard to verify deck ownership
        Optional<FlashcardDTO> existingFlashcard = flashcardService.getFlashcardById(flashcardId);
        if (existingFlashcard.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify deck ownership
        Optional<FlashcardDeckDTO> deck = flashcardService.getDeckById(existingFlashcard.get().getDeckId());
        if (deck.isEmpty() || !deck.get().getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        boolean deleted = flashcardService.deleteFlashcard(flashcardId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Generate flashcards from quiz content
    @PostMapping("/generate/chapter/{chapterId}")
    public ResponseEntity<FlashcardDeckDTO> generateFromChapter(@PathVariable UUID chapterId,
                                                                @RequestBody Map<String, Object> request,
                                                                HttpServletRequest httpRequest) {
        UUID userId = extractUserIdFromRequest(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
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
