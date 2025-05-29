package com.aet.studyassistant.quiz_service.repository;

import com.aet.studyassistant.quiz_service.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
    List<Chapter> findByCourseId(UUID courseId);
}
