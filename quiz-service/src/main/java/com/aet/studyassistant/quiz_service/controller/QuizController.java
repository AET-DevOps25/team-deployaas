package com.aet.studyassistant.quiz_service.controller;

import com.aet.studyassistant.quiz_service.model.Chapter;
import com.aet.studyassistant.quiz_service.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "http://localhost:3000")
public class QuizController {

    private final ChapterService chapterService;

    @Autowired
    public QuizController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/test")
    public String testConnection() {
        return "Quiz Service is connected successfully!";
    }

    @GetMapping("/chapters")
    public List<Chapter> getChapters() {
        return chapterService.getAllChapters();
    }
}
