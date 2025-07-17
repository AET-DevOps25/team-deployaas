package com.aet.studyassistant.flashcard_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class QuizServiceClient {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${quiz.service.url:http://quiz-service:8081}")
    private String quizServiceUrl;
    
    @Autowired
    public QuizServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get questions from a chapter to convert to flashcards
     */
    public List<Map<String, Object>> getChapterQuestions(UUID chapterId) {
        try {
            String response = webClient.get()
                    .uri(quizServiceUrl + "/api/quiz/chapters/{chapterId}/questions", chapterId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            if (response != null) {
                return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
            }
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
    
    /**
     * Get chapter information including name
     */
    public Map<String, Object> getChapterInfo(UUID chapterId) {
        try {
            String response = webClient.get()
                    .uri(quizServiceUrl + "/api/quiz/chapters/{chapterId}", chapterId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            if (response != null) {
                return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            }
            return Map.of();
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    /**
     * Get course information
     */
    public Map<String, Object> getCourseInfo(UUID courseId) {
        try {
            String response = webClient.get()
                    .uri(quizServiceUrl + "/api/quiz/courses/{courseId}", courseId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            if (response != null) {
                return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            }
            return Map.of();
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    /**
     * Check if the Quiz service is available
     */
    public boolean isQuizServiceAvailable() {
        try {
            String response = webClient.get()
                    .uri(quizServiceUrl + "/api/quiz/test")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            return response != null && response.contains("connected");
        } catch (Exception e) {
            return false;
        }
    }
}