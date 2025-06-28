package com.aet.studyassistant.quiz_service.controller;

import com.aet.studyassistant.quiz_service.model.Chapter;
import com.aet.studyassistant.quiz_service.model.Question;
import com.aet.studyassistant.quiz_service.service.ChapterService;
import com.aet.studyassistant.quiz_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final ChapterService chapterService;
    private final QuestionService questionService;

    @Autowired
    public QuizController(ChapterService chapterService, QuestionService questionService) {
        this.chapterService = chapterService;
        this.questionService = questionService;
    }

    @GetMapping("/test")
    public String testConnection() {
        return "Quiz Service is connected successfully!";
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
}
