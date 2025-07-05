package com.aet.studyassistant.quiz_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class GenAIService {
    
    private static final Logger logger = LoggerFactory.getLogger(GenAIService.class);
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${genai.service.url:http://genai-service:8084}")
    private String genaiServiceUrl;
    
    public GenAIService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.objectMapper = objectMapper;
    }
    
    /**
     * Generate feedback for a quiz answer using the GenAI service
     */
    public Map<String, Object> generateFeedback(String questionText, String userAnswer, 
                                              String sampleSolution, String modelType) {
        try {
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("question_text", questionText);
            requestBody.put("user_answer", userAnswer);
            requestBody.put("sample_solution", sampleSolution);
            requestBody.put("model_type", modelType != null ? modelType : "local");
            
            logger.info("Sending feedback request to GenAI service at: {}", genaiServiceUrl);
            
            // Make HTTP call to GenAI service
            Mono<Map> responseMono = webClient
                    .post()
                    .uri(genaiServiceUrl + "/api/feedback")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(30)); // 30 second timeout
            
            Map<String, Object> response = responseMono.block();
            
            if (response != null) {
                logger.info("Successfully received feedback from GenAI service");
                return response;
            } else {
                logger.warn("Received null response from GenAI service");
                return createFallbackResponse();
            }
            
        } catch (Exception e) {
            logger.error("Error calling GenAI service: {}", e.getMessage(), e);
            return createFallbackResponse();
        }
    }
    
    /**
     * Generate advanced feedback using semantic analysis
     */
    public Map<String, Object> generateAdvancedFeedback(String questionText, String userAnswer, 
                                                       String sampleSolution) {
        try {
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("question_text", questionText);
            requestBody.put("user_answer", userAnswer);
            requestBody.put("sample_solution", sampleSolution);
            
            logger.info("Sending advanced feedback request to GenAI service");
            
            // Make HTTP call to GenAI service
            Mono<Map> responseMono = webClient
                    .post()
                    .uri(genaiServiceUrl + "/api/feedback/advanced")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(45)); // Longer timeout for advanced analysis
            
            Map<String, Object> response = responseMono.block();
            
            if (response != null) {
                logger.info("Successfully received advanced feedback from GenAI service");
                return response;
            } else {
                logger.warn("Received null response from GenAI service for advanced feedback");
                return createFallbackResponse();
            }
            
        } catch (Exception e) {
            logger.error("Error calling GenAI service for advanced feedback: {}", e.getMessage(), e);
            return createFallbackResponse();
        }
    }
    
    /**
     * Check if GenAI service is available
     */
    public boolean isGenAIServiceAvailable() {
        try {
            Mono<Map> healthMono = webClient
                    .get()
                    .uri(genaiServiceUrl + "/health")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(5));
            
            Map<String, Object> healthResponse = healthMono.block();
            return healthResponse != null && "healthy".equals(healthResponse.get("status"));
            
        } catch (Exception e) {
            logger.warn("GenAI service health check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get available models from GenAI service
     */
    public Map<String, Object> getAvailableModels() {
        try {
            Mono<Map> modelsMono = webClient
                    .get()
                    .uri(genaiServiceUrl + "/api/models")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10));
            
            return modelsMono.block();
            
        } catch (Exception e) {
            logger.error("Error getting available models from GenAI service: {}", e.getMessage());
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("models", new HashMap<>());
            return fallback;
        }
    }
    
    private Map<String, Object> createFallbackResponse() {
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("score", 0.7);
        fallbackResponse.put("feedback", "Your answer has been submitted. AI feedback is temporarily unavailable, but your response shows good understanding of the topic.");
        fallbackResponse.put("suggestions", new String[]{"Review the sample solution", "Practice explaining concepts clearly"});
        fallbackResponse.put("strengths", new String[]{"Answer provided", "Shows effort and engagement"});
        fallbackResponse.put("weaknesses", new String[]{"AI analysis unavailable"});
        fallbackResponse.put("model_used", "fallback-response");
        fallbackResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        return fallbackResponse;
    }
}
