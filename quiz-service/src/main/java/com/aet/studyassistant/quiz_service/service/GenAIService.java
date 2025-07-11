package com.aet.studyassistant.quiz_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class GenAIService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${genai.service.url:http://genai-service:5001}")
    private String genaiServiceUrl;
    
    @Autowired
    public GenAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generate basic AI feedback for a student's answer
     */
    public Map<String, Object> generateFeedback(String questionText, String userAnswer, String sampleSolution, String modelType) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("question_text", questionText);
            requestBody.put("user_answer", userAnswer);
            requestBody.put("sample_solution", sampleSolution);
            requestBody.put("model_type", modelType);
            
            String response = webClient.post()
                    .uri(genaiServiceUrl + "/feedback")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
            
            return objectMapper.readValue(response, Map.class);
            
        } catch (Exception e) {
            // Fallback response in case of error
            Map<String, Object> fallbackResponse = new HashMap<>();
            fallbackResponse.put("feedback", "AI feedback service is currently unavailable. Please try again later.");
            fallbackResponse.put("strengths", new String[]{});
            fallbackResponse.put("weaknesses", new String[]{});
            fallbackResponse.put("suggestions", new String[]{});
            fallbackResponse.put("score", 0.0);
            fallbackResponse.put("model_used", "fallback");
            fallbackResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            fallbackResponse.put("error", "GenAI service error: " + e.getMessage());
            
            return fallbackResponse;
        }
    }
    
    /**
     * Generate advanced AI feedback with detailed analysis
     */
    public Map<String, Object> generateAdvancedFeedback(String questionText, String userAnswer, String sampleSolution) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("question_text", questionText);
            requestBody.put("user_answer", userAnswer);
            requestBody.put("sample_solution", sampleSolution);
            
            String response = webClient.post()
                    .uri(genaiServiceUrl + "/feedback/advanced")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
            
            return objectMapper.readValue(response, Map.class);
            
        } catch (Exception e) {
            // Fallback response in case of error
            Map<String, Object> fallbackResponse = new HashMap<>();
            fallbackResponse.put("feedback", "Advanced AI feedback service is currently unavailable. Please try again later.");
            fallbackResponse.put("strengths", new String[]{});
            fallbackResponse.put("weaknesses", new String[]{});
            fallbackResponse.put("suggestions", new String[]{});
            fallbackResponse.put("score", 0.0);
            fallbackResponse.put("model_used", "fallback");
            fallbackResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            fallbackResponse.put("error", "GenAI service error: " + e.getMessage());
            
            return fallbackResponse;
        }
    }
    
    /**
     * Check if the GenAI service is available
     */
    public boolean isGenAIServiceAvailable() {
        try {
            String response = webClient.get()
                    .uri(genaiServiceUrl + "/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            Map<String, Object> healthResponse = objectMapper.readValue(response, Map.class);
            return "healthy".equals(healthResponse.get("status"));
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get available models from the GenAI service
     */
    public Map<String, Object> getAvailableModels() {
        try {
            String response = webClient.get()
                    .uri(genaiServiceUrl + "/")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            return objectMapper.readValue(response, Map.class);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Could not retrieve models: " + e.getMessage());
            return errorResponse;
        }
    }
}
