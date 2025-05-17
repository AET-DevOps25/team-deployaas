package com.aet.studyassistant.flashcard_service.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flashcard")
@CrossOrigin(origins = "http://localhost:3000")
public class FlashcardController {

    @GetMapping("/test")
    public String testConnection() {
        return "Flashcard Service is connected successfully!";
    }
}
