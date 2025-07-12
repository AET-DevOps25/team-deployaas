package com.aet.studyassistant.quiz_service.dto;

import java.util.List;
import java.util.UUID;

public class CourseDTO {
    private UUID id;
    private String title;
    private String description;
    private String difficulty;
    private String estimatedTime;
    private String iconKey;
    private List<String> tags;
    private int chapters;

    // Default constructor
    public CourseDTO() {}

    public CourseDTO(UUID id, String title, String description, String difficulty, 
                    String estimatedTime, String iconKey, List<String> tags, int chapters) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.estimatedTime = estimatedTime;
        this.iconKey = iconKey;
        this.tags = tags;
        this.chapters = chapters;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public String getEstimatedTime() { return estimatedTime; }
    public String getIconKey() { return iconKey; }
    public List<String> getTags() { return tags; }
    public int getChapters() { return chapters; }

    public void setId(UUID id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }
    public void setIconKey(String iconKey) { this.iconKey = iconKey; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setChapters(int chapters) { this.chapters = chapters; }
}
