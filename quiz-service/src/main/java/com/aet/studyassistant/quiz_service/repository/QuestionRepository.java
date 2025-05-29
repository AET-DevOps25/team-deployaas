package com.aet.studyassistant.quiz_service.repository;

import com.aet.studyassistant.quiz_service.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByChapterIdOrderByOrdering(UUID chapterId);
}
