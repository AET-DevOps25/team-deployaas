package com.aet.studyassistant.quiz_service.service;

import com.aet.studyassistant.quiz_service.model.Chapter;
import com.aet.studyassistant.quiz_service.repository.ChapterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private ChapterService chapterService;

    @Test
    void getAllChaptersReturnsAllChapters() {
        // Arrange
        Chapter chapter1 = new Chapter("Introduction to DevOps", UUID.randomUUID());
        Chapter chapter2 = new Chapter("CI/CD Fundamentals", UUID.randomUUID());
        List<Chapter> expectedChapters = Arrays.asList(chapter1, chapter2);
        when(chapterRepository.findAll()).thenReturn(expectedChapters);

        // Act
        List<Chapter> actualChapters = chapterService.getAllChapters();

        // Assert
        assertEquals(expectedChapters.size(), actualChapters.size());
        assertEquals(expectedChapters, actualChapters);
        verify(chapterRepository, times(1)).findAll();
    }

    @Test
    void getAllChaptersWhenNoChaptersReturnsEmptyList() {
        // Arrange
        when(chapterRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Chapter> actualChapters = chapterService.getAllChapters();

        // Assert
        assertTrue(actualChapters.isEmpty());
        verify(chapterRepository, times(1)).findAll();
    }

    @Test
    void getChaptersByCourseIdReturnsCorrectChapters() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        Chapter chapter1 = new Chapter("Chapter 1", courseId);
        Chapter chapter2 = new Chapter("Chapter 2", courseId);
        List<Chapter> expectedChapters = Arrays.asList(chapter1, chapter2);
        when(chapterRepository.findByCourseId(courseId)).thenReturn(expectedChapters);

        // Act
        List<Chapter> actualChapters = chapterService.getChaptersByCourseId(courseId);

        // Assert
        assertEquals(expectedChapters.size(), actualChapters.size());
        assertEquals(expectedChapters, actualChapters);
        assertEquals(courseId, actualChapters.get(0).getCourseId());
        assertEquals(courseId, actualChapters.get(1).getCourseId());
        verify(chapterRepository, times(1)).findByCourseId(courseId);
    }

    @Test
    void getChaptersByCourseIdWhenNoChaptersReturnsEmptyList() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        when(chapterRepository.findByCourseId(courseId)).thenReturn(Arrays.asList());

        // Act
        List<Chapter> actualChapters = chapterService.getChaptersByCourseId(courseId);

        // Assert
        assertTrue(actualChapters.isEmpty());
        verify(chapterRepository, times(1)).findByCourseId(courseId);
    }

    @Test
    void getChapterByIdWhenChapterExistsReturnsCorrectChapter() {
        // Arrange
        UUID chapterId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Chapter expectedChapter = new Chapter("Test Chapter", courseId);
        expectedChapter.setId(chapterId);
        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(expectedChapter));

        // Act
        Optional<Chapter> actualChapter = chapterService.getChapterById(chapterId);

        // Assert
        assertTrue(actualChapter.isPresent());
        assertEquals(expectedChapter, actualChapter.get());
        assertEquals(chapterId, actualChapter.get().getId());
        assertEquals("Test Chapter", actualChapter.get().getName());
        verify(chapterRepository, times(1)).findById(chapterId);
    }

    @Test
    void getChapterByIdWhenChapterDoesNotExistReturnsEmpty() {
        // Arrange
        UUID chapterId = UUID.randomUUID();
        when(chapterRepository.findById(chapterId)).thenReturn(Optional.empty());

        // Act
        Optional<Chapter> actualChapter = chapterService.getChapterById(chapterId);

        // Assert
        assertFalse(actualChapter.isPresent());
        verify(chapterRepository, times(1)).findById(chapterId);
    }
}
