package com.aet.studyassistant.flashcard_service.service;

import com.aet.studyassistant.flashcard_service.dto.FlashcardDeckDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TemplateService {

    @Autowired
    private FlashcardService flashcardService;

    /**
     * Creates default flashcard decks for new users
     * This includes starter decks with essential DevOps, Testing, and CI/CD content
     */
    public void createDefaultDecksForNewUser(UUID userId) {
        try {
            // Create DevOps Fundamentals Deck
            createDevOpsFundamentalsDeck(userId);
            
            // Create Testing Strategies Deck
            createTestingStrategiesDeck(userId);
            
            // Create CI/CD Pipeline Essentials Deck
            createCiCdPipelineDeck(userId);
            
            System.out.println("Successfully created default flashcard decks for user: " + userId);
        } catch (Exception e) {
            System.err.println("Error creating default decks for user " + userId + ": " + e.getMessage());
            // Don't throw the exception to avoid failing the registration process
        }
    }

    private void createDevOpsFundamentalsDeck(UUID userId) {
        FlashcardDeckDTO deck = flashcardService.createDeck(userId, "DevOps Fundamentals");
        
        // Create flashcards for this deck
        flashcardService.createFlashcard(deck.getId(), 
            "What does CI/CD stand for?",
            "Continuous Integration / Continuous Deployment (or Continuous Delivery)");
        
        flashcardService.createFlashcard(deck.getId(),
            "What is Infrastructure as Code (IaC)?",
            "Managing and provisioning computing infrastructure through machine-readable definition files, rather than physical hardware configuration or interactive configuration tools");
        
        flashcardService.createFlashcard(deck.getId(),
            "Name the three pillars of observability",
            "Metrics, Logs, and Traces");
        
        flashcardService.createFlashcard(deck.getId(),
            "What is the difference between monitoring and observability?",
            "Monitoring tells you WHAT is happening (predefined metrics), while observability helps you understand WHY it is happening (ability to debug unknown issues)");
    }

    private void createTestingStrategiesDeck(UUID userId) {
        FlashcardDeckDTO deck = flashcardService.createDeck(userId, "Testing Strategies");
        
        flashcardService.createFlashcard(deck.getId(),
            "What is the difference between verification and validation?",
            "Verification: Are we building the product right? (according to specifications)\nValidation: Are we building the right product? (according to user requirements)");
        
        flashcardService.createFlashcard(deck.getId(),
            "Name 3 fundamental principles of software testing",
            "1. Testing shows presence of defects, not their absence\n2. Exhaustive testing is impossible\n3. Early testing saves time and money");
        
        flashcardService.createFlashcard(deck.getId(),
            "What is mutation testing?",
            "A testing technique that evaluates test suite quality by introducing small changes (mutations) to the code and checking if tests can detect these changes");
    }

    private void createCiCdPipelineDeck(UUID userId) {
        FlashcardDeckDTO deck = flashcardService.createDeck(userId, "CI/CD Pipeline Essentials");
        
        flashcardService.createFlashcard(deck.getId(),
            "What are the key stages of a CI/CD pipeline?",
            "1. Source Control\n2. Build Stage\n3. Test Stage\n4. Code Analysis\n5. Deployment to Staging\n6. Production Deployment\n7. Monitoring");
        
        flashcardService.createFlashcard(deck.getId(),
            "What is the main benefit of automated testing in CI/CD?",
            "Early detection of integration issues and improved code quality through consistent, repeatable testing");
        
        flashcardService.createFlashcard(deck.getId(),
            "What is infrastructure drift?",
            "When actual infrastructure configuration deviates from the defined specification over time due to manual changes, patches, or updates");
        
        flashcardService.createFlashcard(deck.getId(),
            "How does IaC prevent infrastructure drift?",
            "By maintaining a single source of truth in code, regularly comparing actual vs desired state, and enabling automated remediation");
    }
}
