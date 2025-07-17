package com.aet.studyassistant.flashcard_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "flashcard_decks")
public class FlashcardDeck {
    @Id
    @Column(name = "deck_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Flashcard> flashcards;

    // Default constructor for JPA
    public FlashcardDeck() {}

    public FlashcardDeck(UUID userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public List<Flashcard> getFlashcards() { return flashcards; }

    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setFlashcards(List<Flashcard> flashcards) { this.flashcards = flashcards; }
}