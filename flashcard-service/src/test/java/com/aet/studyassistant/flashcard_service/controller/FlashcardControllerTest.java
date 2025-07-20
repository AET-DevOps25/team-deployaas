package com.aet.studyassistant.flashcard_service.controller;

import com.aet.studyassistant.flashcard_service.dto.FlashcardDTO;
import com.aet.studyassistant.flashcard_service.dto.FlashcardDeckDTO;
import com.aet.studyassistant.flashcard_service.security.JwtUtil;
import com.aet.studyassistant.flashcard_service.service.FlashcardService;
import com.aet.studyassistant.flashcard_service.service.TemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlashcardController.class)
class FlashcardControllerTest {

    private static final String TEST_DECK_NAME = "DevOps Fundamentals";
    private static final String TEST_FLASHCARD_FRONT = "What is DevOps?";
    private static final String TEST_FLASHCARD_BACK = "DevOps combines development and operations";
    private static final String API_FLASHCARD_BASE = "/api/flashcard";
    private static final String LENGTH_JSON_PATH = "$.length()";
    private static final String DECKS_ENDPOINT = "/decks/";
    private static final String FLASHCARDS_ENDPOINT = "/flashcards/";
    private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID TEST_DECK_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private static final UUID TEST_FLASHCARD_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FlashcardService flashcardService;

    @MockBean
    private TemplateService templateService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser
    void testConnectionReturnsSuccessMessage() throws Exception {
        // Arrange - No specific setup needed for this simple endpoint

        // Act & Assert
        mockMvc.perform(get(API_FLASHCARD_BASE + "/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Flashcard Service is connected successfully!"));
    }

    @Test
    @WithMockUser
    void setupDefaultDecksReturnsSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(templateService).createDefaultDecksForNewUser(TEST_USER_ID);

        // Act & Assert
        mockMvc.perform(post(API_FLASHCARD_BASE + "/setup-defaults/" + TEST_USER_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Default flashcard decks created successfully for user: " + TEST_USER_ID));

        verify(templateService, times(1)).createDefaultDecksForNewUser(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void setupDefaultDecksWhenExceptionOccursReturnsInternalServerError() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Service error")).when(templateService).createDefaultDecksForNewUser(TEST_USER_ID);

        // Act & Assert
        mockMvc.perform(post(API_FLASHCARD_BASE + "/setup-defaults/" + TEST_USER_ID)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error creating default decks: Service error"));

        verify(templateService, times(1)).createDefaultDecksForNewUser(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getUserDecksWhenAuthorizedReturnsUserDecks() throws Exception {
        // Arrange
        List<FlashcardDeckDTO> mockDecks = Arrays.asList(
                createMockDeck(TEST_DECK_NAME, TEST_USER_ID),
                createMockDeck("Java Programming", TEST_USER_ID)
        );
        
        // Mock JWT extraction
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getUserDecks(TEST_USER_ID)).thenReturn(mockDecks);

        // Act & Assert
        mockMvc.perform(get(API_FLASHCARD_BASE + "/decks/user/" + TEST_USER_ID)
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath(LENGTH_JSON_PATH).value(2))
                .andExpect(jsonPath("$[0].name").value(TEST_DECK_NAME));

        verify(flashcardService, times(1)).getUserDecks(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getUserDecksWhenUnauthorizedReturnsUnauthorized() throws Exception {
        // Arrange - No JWT token provided
        when(jwtUtil.extractUserId(anyString())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get(API_FLASHCARD_BASE + "/decks/user/" + TEST_USER_ID))
                .andExpect(status().isUnauthorized());

        verify(flashcardService, never()).getUserDecks(any());
    }

    @Test
    @WithMockUser
    void getUserDecksWhenAccessingOtherUserDecksReturnsForbidden() throws Exception {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        mockJwtExtraction(TEST_USER_ID);

        // Act & Assert
        mockMvc.perform(get(API_FLASHCARD_BASE + "/decks/user/" + otherUserId)
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isForbidden());

        verify(flashcardService, never()).getUserDecks(any());
    }

    @Test
    @WithMockUser
    void getMyDecksReturnsCurrentUserDecks() throws Exception {
        // Arrange
        List<FlashcardDeckDTO> mockDecks = Arrays.asList(
                createMockDeck(TEST_DECK_NAME, TEST_USER_ID)
        );
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getUserDecks(TEST_USER_ID)).thenReturn(mockDecks);

        // Act & Assert
        mockMvc.perform(get(API_FLASHCARD_BASE + "/decks/user")
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath(LENGTH_JSON_PATH).value(1))
                .andExpect(jsonPath("$[0].name").value(TEST_DECK_NAME));

        verify(flashcardService, times(1)).getUserDecks(TEST_USER_ID);
    }

    @Test
    @WithMockUser
    void getDeckWhenAuthorizedAndDeckExistsReturnsDeck() throws Exception {
        // Arrange
        FlashcardDeckDTO mockDeck = createMockDeck(TEST_DECK_NAME, TEST_USER_ID);
        mockDeck.setId(TEST_DECK_ID);
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getDeckById(TEST_DECK_ID)).thenReturn(Optional.of(mockDeck));

        // Act & Assert
        mockMvc.perform(get(API_FLASHCARD_BASE + DECKS_ENDPOINT + TEST_DECK_ID)
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TEST_DECK_NAME))
                .andExpect(jsonPath("$.id").value(TEST_DECK_ID.toString()));

        verify(flashcardService, times(1)).getDeckById(TEST_DECK_ID);
    }

    @Test
    @WithMockUser
    void getDeckWhenDeckDoesNotExistReturnsNotFound() throws Exception {
        // Arrange
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getDeckById(TEST_DECK_ID)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get(API_FLASHCARD_BASE + DECKS_ENDPOINT + TEST_DECK_ID)
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isNotFound());

        verify(flashcardService, times(1)).getDeckById(TEST_DECK_ID);
    }

    @Test
    @WithMockUser
    void getDeckWhenUserDoesNotOwnDeckReturnsForbidden() throws Exception {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        FlashcardDeckDTO mockDeck = createMockDeck(TEST_DECK_NAME, otherUserId);
        mockDeck.setId(TEST_DECK_ID);
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getDeckById(TEST_DECK_ID)).thenReturn(Optional.of(mockDeck));

        // Act & Assert
        mockMvc.perform(get(API_FLASHCARD_BASE + DECKS_ENDPOINT + TEST_DECK_ID)
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isForbidden());

        verify(flashcardService, times(1)).getDeckById(TEST_DECK_ID);
    }

    @Test
    @WithMockUser
    void createDeckWithValidInputReturnsDeck() throws Exception {
        // Arrange
        FlashcardDeckDTO mockDeck = createMockDeck(TEST_DECK_NAME, TEST_USER_ID);
        Map<String, Object> requestBody = Map.of("name", TEST_DECK_NAME);
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.createDeck(TEST_USER_ID, TEST_DECK_NAME)).thenReturn(mockDeck);

        // Act & Assert
        mockMvc.perform(post(API_FLASHCARD_BASE + "/decks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TEST_DECK_NAME));

        verify(flashcardService, times(1)).createDeck(TEST_USER_ID, TEST_DECK_NAME);
    }

    @Test
    @WithMockUser
    void createDeckWhenUnauthorizedReturnsUnauthorized() throws Exception {
        // Arrange
        Map<String, Object> requestBody = Map.of("name", TEST_DECK_NAME);
        when(jwtUtil.extractUserId(anyString())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post(API_FLASHCARD_BASE + "/decks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized());

        verify(flashcardService, never()).createDeck(any(), any());
    }

    @Test
    @WithMockUser
    void createDeckWithInvalidDataReturnsBadRequest() throws Exception {
        // Arrange
        Map<String, Object> requestBody = Map.of("invalid_field", "value");
        mockJwtExtraction(TEST_USER_ID);

        // Act & Assert
        mockMvc.perform(post(API_FLASHCARD_BASE + "/decks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isBadRequest());

        verify(flashcardService, never()).createDeck(any(), any());
    }

    @Test
    @WithMockUser
    void updateDeckWhenAuthorizedAndDeckExistsReturnsUpdatedDeck() throws Exception {
        // Arrange
        String newName = "Updated Deck Name";
        FlashcardDeckDTO existingDeck = createMockDeck(TEST_DECK_NAME, TEST_USER_ID);
        existingDeck.setId(TEST_DECK_ID);
        FlashcardDeckDTO updatedDeck = createMockDeck(newName, TEST_USER_ID);
        updatedDeck.setId(TEST_DECK_ID);
        
        Map<String, String> requestBody = Map.of("name", newName);
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getDeckById(TEST_DECK_ID)).thenReturn(Optional.of(existingDeck));
        when(flashcardService.updateDeck(TEST_DECK_ID, newName)).thenReturn(Optional.of(updatedDeck));

        // Act & Assert
        mockMvc.perform(put(API_FLASHCARD_BASE + DECKS_ENDPOINT + TEST_DECK_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));

        verify(flashcardService, times(1)).getDeckById(TEST_DECK_ID);
        verify(flashcardService, times(1)).updateDeck(TEST_DECK_ID, newName);
    }

    @Test
    @WithMockUser
    void updateDeckWhenUserDoesNotOwnDeckReturnsForbidden() throws Exception {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        FlashcardDeckDTO existingDeck = createMockDeck(TEST_DECK_NAME, otherUserId);
        existingDeck.setId(TEST_DECK_ID);
        
        Map<String, String> requestBody = Map.of("name", "New Name");
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getDeckById(TEST_DECK_ID)).thenReturn(Optional.of(existingDeck));

        // Act & Assert
        mockMvc.perform(put(API_FLASHCARD_BASE + DECKS_ENDPOINT + TEST_DECK_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isForbidden());

        verify(flashcardService, times(1)).getDeckById(TEST_DECK_ID);
        verify(flashcardService, never()).updateDeck(any(), any());
    }

    @Test
    @WithMockUser
    void deleteDeckWhenAuthorizedAndDeckExistsReturnsOk() throws Exception {
        // Arrange
        FlashcardDeckDTO existingDeck = createMockDeck(TEST_DECK_NAME, TEST_USER_ID);
        existingDeck.setId(TEST_DECK_ID);
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getDeckById(TEST_DECK_ID)).thenReturn(Optional.of(existingDeck));
        when(flashcardService.deleteDeck(TEST_DECK_ID)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete(API_FLASHCARD_BASE + DECKS_ENDPOINT + TEST_DECK_ID)
                        .with(csrf())
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isOk());

        verify(flashcardService, times(1)).getDeckById(TEST_DECK_ID);
        verify(flashcardService, times(1)).deleteDeck(TEST_DECK_ID);
    }

    @Test
    @WithMockUser
    void getDeckFlashcardsWhenAuthorizedReturnsFlashcards() throws Exception {
        // Arrange
        FlashcardDeckDTO mockDeck = createMockDeck(TEST_DECK_NAME, TEST_USER_ID);
        mockDeck.setId(TEST_DECK_ID);
        List<FlashcardDTO> mockFlashcards = Arrays.asList(
                createMockFlashcard(TEST_FLASHCARD_FRONT, TEST_FLASHCARD_BACK, TEST_DECK_ID)
        );
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getDeckById(TEST_DECK_ID)).thenReturn(Optional.of(mockDeck));
        when(flashcardService.getDeckFlashcards(TEST_DECK_ID)).thenReturn(mockFlashcards);

        // Act & Assert
        mockMvc.perform(get(API_FLASHCARD_BASE + DECKS_ENDPOINT + TEST_DECK_ID + "/flashcards")
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath(LENGTH_JSON_PATH).value(1))
                .andExpect(jsonPath("$[0].front").value(TEST_FLASHCARD_FRONT));

        verify(flashcardService, times(1)).getDeckById(TEST_DECK_ID);
        verify(flashcardService, times(1)).getDeckFlashcards(TEST_DECK_ID);
    }

    @Test
    @WithMockUser
    void createFlashcardWithValidInputReturnsFlashcard() throws Exception {
        // Arrange
        FlashcardDeckDTO mockDeck = createMockDeck(TEST_DECK_NAME, TEST_USER_ID);
        mockDeck.setId(TEST_DECK_ID);
        FlashcardDTO mockFlashcard = createMockFlashcard(TEST_FLASHCARD_FRONT, TEST_FLASHCARD_BACK, TEST_DECK_ID);
        
        Map<String, String> requestBody = Map.of(
                "front", TEST_FLASHCARD_FRONT,
                "back", TEST_FLASHCARD_BACK
        );
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getDeckById(TEST_DECK_ID)).thenReturn(Optional.of(mockDeck));
        when(flashcardService.createFlashcard(TEST_DECK_ID, TEST_FLASHCARD_FRONT, TEST_FLASHCARD_BACK))
                .thenReturn(Optional.of(mockFlashcard));

        // Act & Assert
        mockMvc.perform(post(API_FLASHCARD_BASE + DECKS_ENDPOINT + TEST_DECK_ID + "/flashcards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.front").value(TEST_FLASHCARD_FRONT))
                .andExpect(jsonPath("$.back").value(TEST_FLASHCARD_BACK));

        verify(flashcardService, times(1)).getDeckById(TEST_DECK_ID);
        verify(flashcardService, times(1)).createFlashcard(TEST_DECK_ID, TEST_FLASHCARD_FRONT, TEST_FLASHCARD_BACK);
    }

    @Test
    @WithMockUser
    void updateFlashcardWhenAuthorizedAndFlashcardExistsReturnsUpdatedFlashcard() throws Exception {
        // Arrange
        String newFront = "Updated front";
        String newBack = "Updated back";
        FlashcardDeckDTO mockDeck = createMockDeck(TEST_DECK_NAME, TEST_USER_ID);
        mockDeck.setId(TEST_DECK_ID);
        FlashcardDTO existingFlashcard = createMockFlashcard(TEST_FLASHCARD_FRONT, TEST_FLASHCARD_BACK, TEST_DECK_ID);
        existingFlashcard.setId(TEST_FLASHCARD_ID);
        FlashcardDTO updatedFlashcard = createMockFlashcard(newFront, newBack, TEST_DECK_ID);
        updatedFlashcard.setId(TEST_FLASHCARD_ID);
        
        Map<String, String> requestBody = Map.of(
                "front", newFront,
                "back", newBack
        );
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getFlashcardById(TEST_FLASHCARD_ID)).thenReturn(Optional.of(existingFlashcard));
        when(flashcardService.getDeckById(TEST_DECK_ID)).thenReturn(Optional.of(mockDeck));
        when(flashcardService.updateFlashcard(TEST_FLASHCARD_ID, newFront, newBack))
                .thenReturn(Optional.of(updatedFlashcard));

        // Act & Assert
        mockMvc.perform(put(API_FLASHCARD_BASE + FLASHCARDS_ENDPOINT + TEST_FLASHCARD_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.front").value(newFront))
                .andExpect(jsonPath("$.back").value(newBack));

        verify(flashcardService, times(1)).getFlashcardById(TEST_FLASHCARD_ID);
        verify(flashcardService, times(1)).getDeckById(TEST_DECK_ID);
        verify(flashcardService, times(1)).updateFlashcard(TEST_FLASHCARD_ID, newFront, newBack);
    }

    @Test
    @WithMockUser
    void updateFlashcardWhenFlashcardDoesNotExistReturnsNotFound() throws Exception {
        // Arrange
        Map<String, String> requestBody = Map.of(
                "front", "New front",
                "back", "New back"
        );
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getFlashcardById(TEST_FLASHCARD_ID)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put(API_FLASHCARD_BASE + FLASHCARDS_ENDPOINT + TEST_FLASHCARD_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isNotFound());

        verify(flashcardService, times(1)).getFlashcardById(TEST_FLASHCARD_ID);
        verify(flashcardService, never()).updateFlashcard(any(), any(), any());
    }

    @Test
    @WithMockUser
    void deleteFlashcardWhenAuthorizedAndFlashcardExistsReturnsOk() throws Exception {
        // Arrange
        FlashcardDeckDTO mockDeck = createMockDeck(TEST_DECK_NAME, TEST_USER_ID);
        mockDeck.setId(TEST_DECK_ID);
        FlashcardDTO existingFlashcard = createMockFlashcard(TEST_FLASHCARD_FRONT, TEST_FLASHCARD_BACK, TEST_DECK_ID);
        existingFlashcard.setId(TEST_FLASHCARD_ID);
        
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getFlashcardById(TEST_FLASHCARD_ID)).thenReturn(Optional.of(existingFlashcard));
        when(flashcardService.getDeckById(TEST_DECK_ID)).thenReturn(Optional.of(mockDeck));
        when(flashcardService.deleteFlashcard(TEST_FLASHCARD_ID)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete(API_FLASHCARD_BASE + FLASHCARDS_ENDPOINT + TEST_FLASHCARD_ID)
                        .with(csrf())
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isOk());

        verify(flashcardService, times(1)).getFlashcardById(TEST_FLASHCARD_ID);
        verify(flashcardService, times(1)).getDeckById(TEST_DECK_ID);
        verify(flashcardService, times(1)).deleteFlashcard(TEST_FLASHCARD_ID);
    }

    @Test
    @WithMockUser
    void deleteFlashcardWhenFlashcardDoesNotExistReturnsNotFound() throws Exception {
        // Arrange
        mockJwtExtraction(TEST_USER_ID);
        when(flashcardService.getFlashcardById(TEST_FLASHCARD_ID)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete(API_FLASHCARD_BASE + FLASHCARDS_ENDPOINT + TEST_FLASHCARD_ID)
                        .with(csrf())
                        .requestAttr("jwt_token", "valid_token"))
                .andExpect(status().isNotFound());

        verify(flashcardService, times(1)).getFlashcardById(TEST_FLASHCARD_ID);
        verify(flashcardService, never()).deleteFlashcard(any());
    }

    // Helper methods for creating mock objects
    private FlashcardDeckDTO createMockDeck(String name, UUID userId) {
        FlashcardDeckDTO deck = new FlashcardDeckDTO();
        deck.setId(UUID.randomUUID());
        deck.setName(name);
        deck.setUserId(userId);
        deck.setFlashcardCount(0);
        return deck;
    }

    private FlashcardDTO createMockFlashcard(String front, String back, UUID deckId) {
        FlashcardDTO flashcard = new FlashcardDTO();
        flashcard.setId(UUID.randomUUID());
        flashcard.setFront(front);
        flashcard.setBack(back);
        flashcard.setDeckId(deckId);
        return flashcard;
    }

    private void mockJwtExtraction(UUID userId) {
        when(jwtUtil.extractUserId("valid_token")).thenReturn(userId);
    }
}
