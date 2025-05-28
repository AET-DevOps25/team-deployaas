package com.aet.studyassistant.quiz_service.model;

import java.util.UUID;

public class Quiz {
    private UUID id;
    private String title;

    public Quiz(UUID id, String title) {
        this.id = id;
        this.title = title;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }

    public void setId(UUID id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
}
