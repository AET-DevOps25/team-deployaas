package com.aet.studyassistant.quiz_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "sample_solution", nullable = false, columnDefinition = "TEXT")
    private String sampleSolution;

    @Column(name = "ordering", nullable = false)
    private Integer ordering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    @JsonBackReference
    private Chapter chapter;

    // Default constructor for JPA
    public Question() {}

    public Question(String text, String sampleSolution, Integer ordering, Chapter chapter) {
        this.text = text;
        this.sampleSolution = sampleSolution;
        this.ordering = ordering;
        this.chapter = chapter;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getText() { return text; }
    public String getSampleSolution() { return sampleSolution; }
    public Integer getOrdering() { return ordering; }
    public Chapter getChapter() { return chapter; }

    public void setId(UUID id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setSampleSolution(String sampleSolution) { this.sampleSolution = sampleSolution; }
    public void setOrdering(Integer ordering) { this.ordering = ordering; }
    public void setChapter(Chapter chapter) { this.chapter = chapter; }
}
