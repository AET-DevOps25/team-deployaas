package com.aet.studyassistant.quiz_service.service;

import com.aet.studyassistant.quiz_service.model.Chapter;
import com.aet.studyassistant.quiz_service.model.Question;
import com.aet.studyassistant.quiz_service.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    private static final String TEST_CHAPTER_NAME = "Test Chapter";

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    @Test
    void getQuestionsByChapterIdReturnsOrderedQuestions() {
        // Arrange
        UUID chapterId = UUID.randomUUID();
        Chapter chapter = new Chapter(TEST_CHAPTER_NAME, UUID.randomUUID());
        chapter.setId(chapterId);
        
        Question question1 = new Question("Question 1", "Solution 1", 1, chapter);
        Question question2 = new Question("Question 2", "Solution 2", 2, chapter);
        List<Question> expectedQuestions = Arrays.asList(question1, question2);
        
        when(questionRepository.findByChapterIdOrderByOrdering(chapterId)).thenReturn(expectedQuestions);

        // Act
        List<Question> actualQuestions = questionService.getQuestionsByChapterId(chapterId);

        // Assert
        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertEquals(expectedQuestions, actualQuestions);
        assertEquals(Integer.valueOf(1), actualQuestions.get(0).getOrdering());
        assertEquals(Integer.valueOf(2), actualQuestions.get(1).getOrdering());
        verify(questionRepository, times(1)).findByChapterIdOrderByOrdering(chapterId);
    }

    @Test
    void getQuestionsByChapterIdWhenNoQuestionsReturnsEmptyList() {
        // Arrange
        UUID chapterId = UUID.randomUUID();
        when(questionRepository.findByChapterIdOrderByOrdering(chapterId)).thenReturn(Arrays.asList());

        // Act
        List<Question> actualQuestions = questionService.getQuestionsByChapterId(chapterId);

        // Assert
        assertTrue(actualQuestions.isEmpty());
        verify(questionRepository, times(1)).findByChapterIdOrderByOrdering(chapterId);
    }

    @Test
    void getQuestionByIdWhenQuestionExistsReturnsCorrectQuestion() {
        // Arrange
        UUID questionId = UUID.randomUUID();
        Chapter chapter = new Chapter(TEST_CHAPTER_NAME, UUID.randomUUID());
        Question expectedQuestion = new Question("Test Question", "Test Solution", 1, chapter);
        expectedQuestion.setId(questionId);
        
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(expectedQuestion));

        // Act
        Optional<Question> actualQuestion = questionService.getQuestionById(questionId);

        // Assert
        assertTrue(actualQuestion.isPresent());
        assertEquals(expectedQuestion, actualQuestion.get());
        assertEquals(questionId, actualQuestion.get().getId());
        assertEquals("Test Question", actualQuestion.get().getText());
        assertEquals("Test Solution", actualQuestion.get().getSampleSolution());
        verify(questionRepository, times(1)).findById(questionId);
    }

    @Test
    void getQuestionByIdWhenQuestionDoesNotExistReturnsEmpty() {
        // Arrange
        UUID questionId = UUID.randomUUID();
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        // Act
        Optional<Question> actualQuestion = questionService.getQuestionById(questionId);

        // Assert
        assertFalse(actualQuestion.isPresent());
        verify(questionRepository, times(1)).findById(questionId);
    }

    @Test
    void getAllQuestionsReturnsAllQuestions() {
        // Arrange
        Chapter chapter = new Chapter(TEST_CHAPTER_NAME, UUID.randomUUID());
        Question question1 = new Question("Question 1", "Solution 1", 1, chapter);
        Question question2 = new Question("Question 2", "Solution 2", 2, chapter);
        List<Question> expectedQuestions = Arrays.asList(question1, question2);
        
        when(questionRepository.findAll()).thenReturn(expectedQuestions);

        // Act
        List<Question> actualQuestions = questionService.getAllQuestions();

        // Assert
        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertEquals(expectedQuestions, actualQuestions);
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    void getAllQuestionsWhenNoQuestionsReturnsEmptyList() {
        // Arrange
        when(questionRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Question> actualQuestions = questionService.getAllQuestions();

        // Assert
        assertTrue(actualQuestions.isEmpty());
        verify(questionRepository, times(1)).findAll();
    }
}
