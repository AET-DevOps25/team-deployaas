package com.aet.studyassistant.flashcard_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "flashcards")
public class Flashcard {
    @Id
    @Column(name = "flashcard_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "front", nullable = false, columnDefinition = "TEXT")
    private String front;

    @Column(name = "back", nullable = false, columnDefinition = "TEXT")
    private String back;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    @JsonBackReference
    private FlashcardDeck deck;

    // Default constructor for JPA
    public Flashcard() {}

    public Flashcard(String front, String back, FlashcardDeck deck) {
        this.front = front;
        this.back = back;
        this.deck = deck;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getFront() { return front; }
    public String getBack() { return back; }
    public FlashcardDeck getDeck() { return deck; }

    public void setId(UUID id) { this.id = id; }
    public void setFront(String front) { this.front = front; }
    public void setBack(String back) { this.back = back; }
    public void setDeck(FlashcardDeck deck) { this.deck = deck; }
}