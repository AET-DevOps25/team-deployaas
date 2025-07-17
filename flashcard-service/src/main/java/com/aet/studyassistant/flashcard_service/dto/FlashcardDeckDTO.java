package com.aet.studyassistant.flashcard_service.dto;

import java.util.List;
import java.util.UUID;

public class FlashcardDeckDTO {
    private UUID id;
    private UUID userId;
    private String name;
    private List<FlashcardDTO> flashcards;
    private int flashcardCount;

    // Default constructor
    public FlashcardDeckDTO() {}

    public FlashcardDeckDTO(UUID id, UUID userId, String name, List<FlashcardDTO> flashcards) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.flashcards = flashcards;
        this.flashcardCount = flashcards != null ? flashcards.size() : 0;
    }

    public FlashcardDeckDTO(UUID id, UUID userId, String name, int flashcardCount) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.flashcardCount = flashcardCount;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public List<FlashcardDTO> getFlashcards() { return flashcards; }
    public int getFlashcardCount() { return flashcardCount; }

    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setFlashcards(List<FlashcardDTO> flashcards) { 
        this.flashcards = flashcards;
        this.flashcardCount = flashcards != null ? flashcards.size() : 0;
    }
    public void setFlashcardCount(int flashcardCount) { this.flashcardCount = flashcardCount; }
}
