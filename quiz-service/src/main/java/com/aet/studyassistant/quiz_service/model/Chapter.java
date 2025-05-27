package com.aet.studyassistant.quiz_service.model;

import java.util.List;
import java.util.UUID;

public class Chapter {
    private UUID id;
    private String name;
    private List<Quiz> quizzes;

    public Chapter(UUID id, String name, List<Quiz> quizzes) {
        this.id = id;
        this.name = name;
        this.quizzes = quizzes;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public List<Quiz> getQuizzes() { return quizzes; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setQuizzes(List<Quiz> quizzes) { this.quizzes = quizzes; }
}
