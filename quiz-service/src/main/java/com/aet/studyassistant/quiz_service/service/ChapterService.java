package com.aet.studyassistant.quiz_service.service;

import com.aet.studyassistant.quiz_service.model.Chapter;
import com.aet.studyassistant.quiz_service.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChapterService {
    
    private final ChapterRepository chapterRepository;

    @Autowired
    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    public List<Chapter> getChaptersByCourseId(UUID courseId) {
        return chapterRepository.findByCourseId(courseId);
    }

    public Optional<Chapter> getChapterById(UUID chapterId) {
        return chapterRepository.findById(chapterId);
    }
}
