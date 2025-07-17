package com.aet.studyassistant.quiz_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @Column(name = "course_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(mappedBy = "courseId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Chapter> chapters;

    // Default constructor for JPA
    public Course() {}

    public Course(String title) {
        this.title = title;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public List<Chapter> getChapters() { return chapters; }

    public void setId(UUID id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setChapters(List<Chapter> chapters) { this.chapters = chapters; }
}
