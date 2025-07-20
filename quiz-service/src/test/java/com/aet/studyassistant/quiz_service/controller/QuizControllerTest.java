package com.aet.studyassistant.quiz_service.controller;

import com.aet.studyassistant.quiz_service.dto.CourseDTO;
import com.aet.studyassistant.quiz_service.model.Chapter;
import com.aet.studyassistant.quiz_service.model.Course;
import com.aet.studyassistant.quiz_service.model.Question;
import com.aet.studyassistant.quiz_service.security.JwtUtil;
import com.aet.studyassistant.quiz_service.service.ChapterService;
import com.aet.studyassistant.quiz_service.service.CourseService;
import com.aet.studyassistant.quiz_service.service.GenAIService;
import com.aet.studyassistant.quiz_service.service.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

    private static final String TEST_COURSE_TITLE = "DevOps Fundamentals";
    private static final String TEST_CHAPTER_NAME = "CI/CD Basics";
    private static final String TEST_QUESTION_TEXT = "What is DevOps?";
    private static final String TEST_USER_ANSWER = "DevOps is a culture";
    private static final String API_QUIZ_BASE = "/api/quiz";
    private static final String LENGTH_JSON_PATH = "$.length()";
    private static final String COURSES_ENDPOINT = "/courses/";
    private static final String QUESTIONS_ENDPOINT = "/questions/";
    private static final String SUBMIT_ENDPOINT = "/submit";
    private static final String ANSWER_KEY = "answer";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @MockBean
    private ChapterService chapterService;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private GenAIService genAIService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser
    void testConnectionReturnsSuccessMessage() throws Exception {
        // Arrange - No specific setup needed for this simple endpoint

        // Act & Assert
        mockMvc.perform(get(API_QUIZ_BASE + "/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Quiz Service is connected successfully!"));
    }

    @Test
    @WithMockUser
    void getCoursesReturnsAllCourses() throws Exception {
        // Arrange
        List<Course> mockCourses = Arrays.asList(
                createMockCourse(TEST_COURSE_TITLE),
                createMockCourse("Java Programming")
        );
        when(courseService.getAllCourses()).thenReturn(mockCourses);

        // Act & Assert
        mockMvc.perform(get(API_QUIZ_BASE + "/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath(LENGTH_JSON_PATH).value(2))
                .andExpect(jsonPath("$[0].title").value(TEST_COURSE_TITLE));

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    @WithMockUser
    void getCoursesDetailedReturnsCoursesWithMetadata() throws Exception {
        // Arrange
        Course mockCourse = createMockCourse(TEST_COURSE_TITLE);
        List<Chapter> mockChapters = Arrays.asList(
                createMockChapter(TEST_CHAPTER_NAME, mockCourse.getId())
        );
        
        when(courseService.getAllCourses()).thenReturn(Arrays.asList(mockCourse));
        when(chapterService.getChaptersByCourseId(mockCourse.getId())).thenReturn(mockChapters);

        // Act & Assert
        mockMvc.perform(get(API_QUIZ_BASE + "/courses/detailed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value(TEST_COURSE_TITLE))
                .andExpect(jsonPath("$[0].chapters").value(1))
                .andExpect(jsonPath("$[0].difficulty").value("Intermediate"))
                .andExpect(jsonPath("$[0].description").exists());

        verify(courseService, times(1)).getAllCourses();
        verify(chapterService, times(1)).getChaptersByCourseId(mockCourse.getId());
    }

    @Test
    @WithMockUser
    void getCourseByIdWhenCourseExistsReturnsCorrectCourse() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        Course mockCourse = createMockCourse(TEST_COURSE_TITLE);
        mockCourse.setId(courseId);
        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(mockCourse));

        // Act & Assert
        mockMvc.perform(get(API_QUIZ_BASE + COURSES_ENDPOINT + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(TEST_COURSE_TITLE))
                .andExpect(jsonPath("$.id").value(courseId.toString()));

        verify(courseService, times(1)).getCourseById(courseId);
    }

    @Test
    @WithMockUser
    void getCourseByIdWhenCourseDoesNotExistReturnsNotFound() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        when(courseService.getCourseById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get(API_QUIZ_BASE + "/courses/" + courseId))
                .andExpect(status().isNotFound());

        verify(courseService, times(1)).getCourseById(courseId);
    }

    @Test
    @WithMockUser
    void getChaptersReturnsAllChapters() throws Exception {
        // Arrange
        List<Chapter> mockChapters = Arrays.asList(
                createMockChapter(TEST_CHAPTER_NAME, UUID.randomUUID()),
                createMockChapter("Advanced Topics", UUID.randomUUID())
        );
        when(chapterService.getAllChapters()).thenReturn(mockChapters);

        // Act & Assert
        mockMvc.perform(get(API_QUIZ_BASE + "/chapters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(TEST_CHAPTER_NAME));

        verify(chapterService, times(1)).getAllChapters();
    }

    @Test
    @WithMockUser
    void getChaptersByCourseReturnsCorrectChapters() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        List<Chapter> mockChapters = Arrays.asList(
                createMockChapter(TEST_CHAPTER_NAME, courseId)
        );
        when(chapterService.getChaptersByCourseId(courseId)).thenReturn(mockChapters);

        // Act & Assert
        mockMvc.perform(get(API_QUIZ_BASE + "/courses/" + courseId + "/chapters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(TEST_CHAPTER_NAME));

        verify(chapterService, times(1)).getChaptersByCourseId(courseId);
    }

    @Test
    @WithMockUser
    void getQuestionsByChapterReturnsCorrectQuestions() throws Exception {
        // Arrange
        UUID chapterId = UUID.randomUUID();
        Chapter mockChapter = createMockChapter(TEST_CHAPTER_NAME, UUID.randomUUID());
        mockChapter.setId(chapterId);
        
        List<Question> mockQuestions = Arrays.asList(
                createMockQuestion(TEST_QUESTION_TEXT, "Sample solution", mockChapter)
        );
        when(questionService.getQuestionsByChapterId(chapterId)).thenReturn(mockQuestions);

        // Act & Assert
        mockMvc.perform(get(API_QUIZ_BASE + "/chapters/" + chapterId + "/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].text").value(TEST_QUESTION_TEXT));

        verify(questionService, times(1)).getQuestionsByChapterId(chapterId);
    }

    @Test
    @WithMockUser
    void submitAnswerWithValidInputReturnsSuccessfulResponse() throws Exception {
        // Arrange
        UUID questionId = UUID.randomUUID();
        Chapter mockChapter = createMockChapter(TEST_CHAPTER_NAME, UUID.randomUUID());
        Question mockQuestion = createMockQuestion(TEST_QUESTION_TEXT, "Sample solution", mockChapter);
        mockQuestion.setId(questionId);

        Map<String, Object> mockFeedback = createMockFeedbackResponse();
        
        when(questionService.getQuestionById(questionId)).thenReturn(Optional.of(mockQuestion));
        when(genAIService.generateFeedback(anyString(), anyString(), anyString()))
                .thenReturn(mockFeedback);

        Map<String, String> requestBody = Map.of("answer", TEST_USER_ANSWER);

        // Act & Assert
        mockMvc.perform(post(API_QUIZ_BASE + "/questions/" + questionId + "/submit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback").value("Great response!"))
                .andExpect(jsonPath("$.questionId").value(questionId.toString()))
                .andExpect(jsonPath("$.userAnswer").value(TEST_USER_ANSWER));

        verify(questionService, times(1)).getQuestionById(questionId);
        verify(genAIService, times(1)).generateFeedback(anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser
    void submitAnswerWithEmptyAnswerReturnsBadRequest() throws Exception {
        // Arrange
        UUID questionId = UUID.randomUUID();
        Map<String, String> requestBody = Map.of("answer", "");

        // Act & Assert
        mockMvc.perform(post(API_QUIZ_BASE + "/questions/" + questionId + "/submit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Answer cannot be empty"));

        verify(questionService, never()).getQuestionById(any());
        verify(genAIService, never()).generateFeedback(anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser
    void submitAnswerWithNonExistentQuestionReturnsNotFound() throws Exception {
        // Arrange
        UUID questionId = UUID.randomUUID();
        when(questionService.getQuestionById(questionId)).thenReturn(Optional.empty());
        
        Map<String, String> requestBody = Map.of("answer", TEST_USER_ANSWER);

        // Act & Assert
        mockMvc.perform(post(API_QUIZ_BASE + "/questions/" + questionId + "/submit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());

        verify(questionService, times(1)).getQuestionById(questionId);
        verify(genAIService, never()).generateFeedback(anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser
    void getGenAIHealthWhenServiceIsAvailableReturnsHealthyStatus() throws Exception {
        // Arrange
        when(genAIService.isGenAIServiceAvailable()).thenReturn(true);
        when(genAIService.getAvailableModels()).thenReturn(Map.of("models", Arrays.asList("local", "cloud")));

        // Act & Assert
        mockMvc.perform(get(API_QUIZ_BASE + "/genai/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genai_service_available").value(true))
                .andExpect(jsonPath("$.available_models").exists());

        verify(genAIService, times(1)).isGenAIServiceAvailable();
        verify(genAIService, times(1)).getAvailableModels();
    }

    @Test
    @WithMockUser
    void getGenAIHealthWhenServiceIsUnavailableReturnsUnavailableStatus() throws Exception {
        // Arrange
        when(genAIService.isGenAIServiceAvailable()).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get(API_QUIZ_BASE + "/genai/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genai_service_available").value(false))
                .andExpect(jsonPath("$.available_models").doesNotExist());

        verify(genAIService, times(1)).isGenAIServiceAvailable();
        verify(genAIService, never()).getAvailableModels();
    }

    // Helper methods for creating mock objects
    private Course createMockCourse(String title) {
        Course course = new Course(title);
        course.setId(UUID.randomUUID());
        return course;
    }

    private Chapter createMockChapter(String name, UUID courseId) {
        Chapter chapter = new Chapter(name, courseId);
        chapter.setId(UUID.randomUUID());
        return chapter;
    }

    private Question createMockQuestion(String text, String solution, Chapter chapter) {
        Question question = new Question(text, solution, 1, chapter);
        question.setId(UUID.randomUUID());
        return question;
    }

    private Map<String, Object> createMockFeedbackResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("feedback", "Great response!");
        response.put("model_used", "local");
        return response;
    }
}
