package com.aet.studyassistant.quiz_service.controller;

import com.aet.studyassistant.quiz_service.dto.CourseDTO;
import com.aet.studyassistant.quiz_service.model.Chapter;
import com.aet.studyassistant.quiz_service.model.Course;
import com.aet.studyassistant.quiz_service.model.Question;
import com.aet.studyassistant.quiz_service.service.ChapterService;
import com.aet.studyassistant.quiz_service.service.CourseService;
import com.aet.studyassistant.quiz_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final ChapterService chapterService;
    private final QuestionService questionService;
    private final CourseService courseService;

    @Autowired
    public QuizController(ChapterService chapterService, QuestionService questionService, CourseService courseService) {
        this.chapterService = chapterService;
        this.questionService = questionService;
        this.courseService = courseService;
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

    // Helper methods to provide course metadata
    private String getDescriptionForCourse(String title) {
        if (title.contains("DevOps")) {
            return "Master DevOps practices including CI/CD, Infrastructure as Code, and Monitoring & Observability";
        }
        return "Comprehensive technology course with hands-on practice";
    }

    private String getDifficultyForCourse(String title) {
        if (title.contains("DevOps")) {
            return "Intermediate";
        }
        return "Beginner";
    }

    private String getEstimatedTimeForCourse(String title) {
        if (title.contains("DevOps")) {
            return "8-12 hours";
        }
        return "4-6 hours";
    }

    private String getIconKeyForCourse(String title) {
        if (title.contains("DevOps")) {
            return "cloud";
        }
        return "code";
    }

    private List<String> getTagsForCourse(String title) {
        if (title.contains("DevOps")) {
            return Arrays.asList("CI/CD", "Infrastructure", "Monitoring", "Automation");
        }
        return Arrays.asList("Technology", "Programming");
    }
}
