package com.aet.studyassistant.quiz_service.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "http://localhost:3000")
public class QuizController {

    @GetMapping("/test")
    public String testConnection() {
        return "Quiz Service is connected successfully!";
    }
}
