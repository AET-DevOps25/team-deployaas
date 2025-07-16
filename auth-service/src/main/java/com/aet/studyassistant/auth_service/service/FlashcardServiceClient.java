package com.aet.studyassistant.auth_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
public class FlashcardServiceClient {

    private final RestTemplate restTemplate;
    private final String flashcardServiceUrl;

    public FlashcardServiceClient(@Value("${flashcard.service.url:http://flashcard-service:8082}") String flashcardServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.flashcardServiceUrl = flashcardServiceUrl;
    }

    /**
     * Call the flashcard service to set up default decks for a new user
     */
    public boolean setupDefaultDecksForUser(UUID userId) {
        try {
            String url = flashcardServiceUrl + "/api/flashcard/setup-defaults/" + userId;
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Successfully set up default flashcard decks for user: " + userId);
                return true;
            } else {
                System.err.println("Failed to set up flashcard decks. Status: " + response.getStatusCode());
                return false;
            }
        } catch (RestClientException e) {
            System.err.println("Error calling flashcard service for user " + userId + ": " + e.getMessage());
            // Don't throw exception - we don't want registration to fail if flashcard setup fails
            return false;
        }
    }
}
