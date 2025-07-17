package com.aet.studyassistant.flashcard_service.dto;

import java.util.UUID;

public class FlashcardDTO {
    private UUID id;
    private UUID deckId;
    private String front;
    private String back;

    // Default constructor
    public FlashcardDTO() {}

    public FlashcardDTO(UUID id, UUID deckId, String front, String back) {
        this.id = id;
        this.deckId = deckId;
        this.front = front;
        this.back = back;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public UUID getDeckId() { return deckId; }
    public String getFront() { return front; }
    public String getBack() { return back; }

    public void setId(UUID id) { this.id = id; }
    public void setDeckId(UUID deckId) { this.deckId = deckId; }
    public void setFront(String front) { this.front = front; }
    public void setBack(String back) { this.back = back; }
}