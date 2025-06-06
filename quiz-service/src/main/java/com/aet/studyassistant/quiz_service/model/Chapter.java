package com.aet.studyassistant.quiz_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chapters")
public class Chapter {
    @Id
    @Column(name = "chapter_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("ordering ASC")
    @JsonManagedReference
    private List<Question> questions;

    // Default constructor for JPA
    public Chapter() {}

    public Chapter(String name, UUID courseId) {
        this.name = name;
        this.courseId = courseId;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public UUID getCourseId() { return courseId; }
    public List<Question> getQuestions() { return questions; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
