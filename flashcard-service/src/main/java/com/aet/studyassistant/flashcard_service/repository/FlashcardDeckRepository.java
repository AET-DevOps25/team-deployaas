package com.aet.studyassistant.flashcard_service.repository;

import com.aet.studyassistant.flashcard_service.model.FlashcardDeck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlashcardDeckRepository extends JpaRepository<FlashcardDeck, UUID> {
    List<FlashcardDeck> findByUserId(UUID userId);
}