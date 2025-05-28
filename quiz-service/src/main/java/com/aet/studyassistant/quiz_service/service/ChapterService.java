package com.aet.studyassistant.quiz_service.service;

import com.aet.studyassistant.quiz_service.model.Chapter;
import com.aet.studyassistant.quiz_service.model.Quiz;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChapterService {
    private final List<Chapter> chapters;

    public ChapterService() {
        // Demo data
        chapters = new ArrayList<>();
        Chapter chapter1 = new Chapter(UUID.randomUUID(), "Continuous Integration", Arrays.asList(
                new Quiz(UUID.randomUUID(), "CI Basics"),
                new Quiz(UUID.randomUUID(), "CI Tools")
        ));
        Chapter chapter2 = new Chapter(UUID.randomUUID(), "Infrastructure as Code", Arrays.asList(
                new Quiz(UUID.randomUUID(), "IaC Concepts"),
                new Quiz(UUID.randomUUID(), "Terraform Basics")
        ));
        chapters.add(chapter1);
        chapters.add(chapter2);
    }

    public List<Chapter> getAllChapters() {
        return chapters;
    }
}
