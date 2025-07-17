package com.aet.studyassistant.quiz_service.controller;

import com.aet.studyassistant.quiz_service.dto.CourseDTO;
import com.aet.studyassistant.quiz_service.model.Chapter;
import com.aet.studyassistant.quiz_service.model.Course;
import com.aet.studyassistant.quiz_service.model.Question;
import com.aet.studyassistant.quiz_service.service.ChapterService;
import com.aet.studyassistant.quiz_service.service.CourseService;
import com.aet.studyassistant.quiz_service.service.QuestionService;
import com.aet.studyassistant.quiz_service.service.GenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final ChapterService chapterService;
    private final QuestionService questionService;
    private final CourseService courseService;
    private final GenAIService genAIService;

    @Autowired
    public QuizController(ChapterService chapterService, QuestionService questionService, 
                         CourseService courseService, GenAIService genAIService) {
        this.chapterService = chapterService;
        this.questionService = questionService;
        this.courseService = courseService;
        this.genAIService = genAIService;
    }

    @GetMapping("/test")
    public String testConnection() {
        return "Quiz Service is connected successfully!";
    }

    @GetMapping("/courses")
    public List<Course> getCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/courses/detailed")
    public List<CourseDTO> getCoursesDetailed() {
        List<Course> courses = courseService.getAllCourses();
        List<CourseDTO> courseDTOs = new ArrayList<>();
        
        for (Course course : courses) {
            List<Chapter> chapters = chapterService.getChaptersByCourseId(course.getId());
            
            CourseDTO courseDTO = new CourseDTO(
                course.getId(),
                course.getTitle(),
                getDescriptionForCourse(course.getTitle()),
                getDifficultyForCourse(course.getTitle()),
                getEstimatedTimeForCourse(course.getTitle()),
                getIconKeyForCourse(course.getTitle()),
                getTagsForCourse(course.getTitle()),
                chapters.size()
            );
            courseDTOs.add(courseDTO);
        }
        
        return courseDTOs;
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Course> getCourse(@PathVariable UUID courseId) {
        Optional<Course> course = courseService.getCourseById(courseId);
        return course.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/chapters")
    public List<Chapter> getChapters() {
        return chapterService.getAllChapters();
    }

    @GetMapping("/courses/{courseId}/chapters")
    public List<Chapter> getChaptersByCourse(@PathVariable UUID courseId) {
        return chapterService.getChaptersByCourseId(courseId);
    }

    @GetMapping("/chapters/{chapterId}")
    public ResponseEntity<Chapter> getChapter(@PathVariable UUID chapterId) {
        Optional<Chapter> chapter = chapterService.getChapterById(chapterId);
        return chapter.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/chapters/{chapterId}/questions")
    public List<Question> getQuestionsByChapter(@PathVariable UUID chapterId) {
        return questionService.getQuestionsByChapterId(chapterId);
    }

    @GetMapping("/questions/{questionId}")
    public ResponseEntity<Question> getQuestion(@PathVariable UUID questionId) {
        Optional<Question> question = questionService.getQuestionById(questionId);
        return question.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/questions/{questionId}/submit")
    public ResponseEntity<?> submitAnswer(@PathVariable UUID questionId, @RequestBody Map<String, String> request) {
        try {
            String userAnswer = request.get("answer");
            if (userAnswer == null || userAnswer.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Answer cannot be empty");
            }

            Optional<Question> questionOpt = questionService.getQuestionById(questionId);
            if (questionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Question question = questionOpt.get();
            String modelType = request.getOrDefault("model_type", "local");

            // Call GenAI service for feedback
            Map<String, Object> feedbackResponse = genAIService.generateFeedback(
                question.getText(),
                userAnswer,
                question.getSampleSolution(),
                modelType
            );

            // Add additional context to response
            Map<String, Object> response = new HashMap<>(feedbackResponse);
            response.put("questionId", questionId);
            response.put("userAnswer", userAnswer);
            response.put("questionText", question.getText());
            response.put("chapterId", question.getChapter().getId());
            response.put("chapterTitle", question.getChapter().getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing answer submission: " + e.getMessage());
        }
    }

    @PostMapping("/questions/{questionId}/submit/advanced")
    public ResponseEntity<?> submitAnswerAdvanced(@PathVariable UUID questionId, @RequestBody Map<String, String> request) {
        try {
            Optional<Question> questionOpt = questionService.getQuestionById(questionId);
            if (questionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Question question = questionOpt.get();
            String userAnswer = request.get("answer");
            
            if (userAnswer == null || userAnswer.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Answer cannot be empty");
            }

            // Call GenAI service for advanced feedback
            Map<String, Object> feedbackResponse = genAIService.generateAdvancedFeedback(
                question.getText(),
                userAnswer,
                question.getSampleSolution()
            );
            
            // Add additional context to response
            Map<String, Object> response = new HashMap<>(feedbackResponse);
            response.put("questionId", questionId);
            response.put("userAnswer", userAnswer);
            response.put("questionText", question.getText());
            response.put("chapterId", question.getChapter().getId());
            response.put("chapterTitle", question.getChapter().getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing advanced answer submission: " + e.getMessage());
        }
    }

    @PostMapping("/questions/{questionId}/submit/semantic")
    public ResponseEntity<?> submitAnswerSemantic(@PathVariable UUID questionId, @RequestBody Map<String, String> request) {
        try {
            Optional<Question> questionOpt = questionService.getQuestionById(questionId);
            if (questionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Question question = questionOpt.get();
            String userAnswer = request.get("answer");
            
            if (userAnswer == null || userAnswer.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Answer cannot be empty");
            }

            // Call GenAI service for semantic feedback
            Map<String, Object> feedbackResponse = genAIService.generateSemanticFeedback(
                question.getText(),
                userAnswer,
                question.getSampleSolution()
            );
            
            // Add additional context to response
            Map<String, Object> response = new HashMap<>(feedbackResponse);
            response.put("questionId", questionId);
            response.put("userAnswer", userAnswer);
            response.put("questionText", question.getText());
            response.put("chapterId", question.getChapter().getId());
            response.put("chapterTitle", question.getChapter().getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing semantic answer submission: " + e.getMessage());
        }
    }
    
    @GetMapping("/genai/health")
    public ResponseEntity<?> getGenAIHealth() {
        try {
            boolean isAvailable = genAIService.isGenAIServiceAvailable();
            Map<String, Object> healthResponse = new HashMap<>();
            healthResponse.put("genai_service_available", isAvailable);
            healthResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            
            if (isAvailable) {
                Map<String, Object> models = genAIService.getAvailableModels();
                healthResponse.put("available_models", models);
            }
            
            return ResponseEntity.ok(healthResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error checking GenAI service health: " + e.getMessage());
        }
    }

    // Helper methods to provide course metadata
    private String getDescriptionForCourse(String title) {
        if (title.contains("DevOps")) {
            return "Master DevOps practices including CI/CD, Infrastructure as Code, and Monitoring & Observability";
        } else if (title.contains("Placeholder Course 1")) {
            return "Comprehensive testing fundamentals and best practices for software quality assurance";
        } else if (title.contains("Placeholder Course 2")) {
            return "Advanced course covering sophisticated concepts and real-world applications";
        }
        return "Comprehensive technology course with hands-on practice";
    }

    private String getDifficultyForCourse(String title) {
        if (title.contains("DevOps")) {
            return "Intermediate";
        } else if (title.contains("Placeholder Course 1")) {
            return "Beginner";
        } else if (title.contains("Placeholder Course 2")) {
            return "Advanced";
        }
        return "Beginner";
    }

    private String getEstimatedTimeForCourse(String title) {
        if (title.contains("DevOps")) {
            return "8-12 hours";
        } else if (title.contains("Placeholder Course 1")) {
            return "4-6 hours";
        } else if (title.contains("Placeholder Course 2")) {
            return "6-8 hours";
        }
        return "4-6 hours";
    }

    private String getIconKeyForCourse(String title) {
        if (title.contains("DevOps")) {
            return "cloud";
        } else if (title.contains("Placeholder Course 1")) {
            return "brain";
        } else if (title.contains("Placeholder Course 2")) {
            return "cpu";
        }
        return "code";
    }

    private List<String> getTagsForCourse(String title) {
        if (title.contains("DevOps")) {
            return Arrays.asList("CI/CD", "Infrastructure", "Monitoring", "Automation");
        } else if (title.contains("Placeholder Course 1")) {
            return Arrays.asList("Testing", "Quality Assurance", "Fundamentals");
        } else if (title.contains("Placeholder Course 2")) {
            return Arrays.asList("Advanced", "Methodologies", "Best Practices");
        }
        return Arrays.asList("Technology", "Programming");
    }
}
