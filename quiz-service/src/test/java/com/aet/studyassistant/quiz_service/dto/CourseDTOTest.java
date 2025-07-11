package com.aet.studyassistant.quiz_service.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CourseDTOTest {

    private static final UUID TEST_COURSE_ID = UUID.randomUUID();
    private static final String TEST_TITLE = "DevOps Fundamentals";
    private static final String TEST_DESCRIPTION = "Master DevOps practices including CI/CD, Infrastructure as Code, and Monitoring & Observability";
    private static final String TEST_DIFFICULTY = "Intermediate";
    private static final String TEST_ESTIMATED_TIME = "8-12 hours";
    private static final String TEST_ICON_KEY = "cloud";
    private static final List<String> TEST_TAGS = Arrays.asList("CI/CD", "Infrastructure", "Monitoring", "Automation");
    private static final int TEST_CHAPTERS = 3;

    @Test
    void defaultConstructorCreatesEmptyObject() {
        // Arrange & Act
        CourseDTO courseDTO = new CourseDTO();

        // Assert
        assertNull(courseDTO.getId());
        assertNull(courseDTO.getTitle());
        assertNull(courseDTO.getDescription());
        assertNull(courseDTO.getDifficulty());
        assertNull(courseDTO.getEstimatedTime());
        assertNull(courseDTO.getIconKey());
        assertNull(courseDTO.getTags());
        assertEquals(0, courseDTO.getChapters());
    }

    @Test
    void parameterizedConstructorCreatesCorrectObject() {
        // Arrange & Act
        CourseDTO courseDTO = new CourseDTO(
            TEST_COURSE_ID, TEST_TITLE, TEST_DESCRIPTION, TEST_DIFFICULTY,
            TEST_ESTIMATED_TIME, TEST_ICON_KEY, TEST_TAGS, TEST_CHAPTERS
        );

        // Assert
        assertEquals(TEST_COURSE_ID, courseDTO.getId());
        assertEquals(TEST_TITLE, courseDTO.getTitle());
        assertEquals(TEST_DESCRIPTION, courseDTO.getDescription());
        assertEquals(TEST_DIFFICULTY, courseDTO.getDifficulty());
        assertEquals(TEST_ESTIMATED_TIME, courseDTO.getEstimatedTime());
        assertEquals(TEST_ICON_KEY, courseDTO.getIconKey());
        assertEquals(TEST_TAGS, courseDTO.getTags());
        assertEquals(TEST_CHAPTERS, courseDTO.getChapters());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        // Arrange
        CourseDTO courseDTO = new CourseDTO();

        // Act
        courseDTO.setId(TEST_COURSE_ID);
        courseDTO.setTitle(TEST_TITLE);
        courseDTO.setDescription(TEST_DESCRIPTION);
        courseDTO.setDifficulty(TEST_DIFFICULTY);
        courseDTO.setEstimatedTime(TEST_ESTIMATED_TIME);
        courseDTO.setIconKey(TEST_ICON_KEY);
        courseDTO.setTags(TEST_TAGS);
        courseDTO.setChapters(TEST_CHAPTERS);

        // Assert
        assertEquals(TEST_COURSE_ID, courseDTO.getId());
        assertEquals(TEST_TITLE, courseDTO.getTitle());
        assertEquals(TEST_DESCRIPTION, courseDTO.getDescription());
        assertEquals(TEST_DIFFICULTY, courseDTO.getDifficulty());
        assertEquals(TEST_ESTIMATED_TIME, courseDTO.getEstimatedTime());
        assertEquals(TEST_ICON_KEY, courseDTO.getIconKey());
        assertEquals(TEST_TAGS, courseDTO.getTags());
        assertEquals(TEST_CHAPTERS, courseDTO.getChapters());
    }

    @Test
    void gettersReturnCorrectValues() {
        // Arrange
        CourseDTO courseDTO = new CourseDTO(
            TEST_COURSE_ID, TEST_TITLE, TEST_DESCRIPTION, TEST_DIFFICULTY,
            TEST_ESTIMATED_TIME, TEST_ICON_KEY, TEST_TAGS, TEST_CHAPTERS
        );

        // Act & Assert
        assertEquals(TEST_COURSE_ID, courseDTO.getId());
        assertEquals(TEST_TITLE, courseDTO.getTitle());
        assertEquals(TEST_DESCRIPTION, courseDTO.getDescription());
        assertEquals(TEST_DIFFICULTY, courseDTO.getDifficulty());
        assertEquals(TEST_ESTIMATED_TIME, courseDTO.getEstimatedTime());
        assertEquals(TEST_ICON_KEY, courseDTO.getIconKey());
        assertEquals(TEST_TAGS, courseDTO.getTags());
        assertEquals(TEST_CHAPTERS, courseDTO.getChapters());
    }

    @Test
    void courseDTOHandlesNullValues() {
        // Arrange & Act
        CourseDTO courseDTO = new CourseDTO(
            null, null, null, null, null, null, null, 0
        );

        // Assert
        assertNull(courseDTO.getId());
        assertNull(courseDTO.getTitle());
        assertNull(courseDTO.getDescription());
        assertNull(courseDTO.getDifficulty());
        assertNull(courseDTO.getEstimatedTime());
        assertNull(courseDTO.getIconKey());
        assertNull(courseDTO.getTags());
        assertEquals(0, courseDTO.getChapters());
    }

    @Test
    void courseDTOHandlesEmptyTags() {
        // Arrange
        List<String> emptyTags = Arrays.asList();

        // Act
        CourseDTO courseDTO = new CourseDTO(
            TEST_COURSE_ID, TEST_TITLE, TEST_DESCRIPTION, TEST_DIFFICULTY,
            TEST_ESTIMATED_TIME, TEST_ICON_KEY, emptyTags, TEST_CHAPTERS
        );

        // Assert
        assertEquals(emptyTags, courseDTO.getTags());
        assertTrue(courseDTO.getTags().isEmpty());
    }

    @Test
    void courseDTOHandlesNegativeChapters() {
        // Arrange
        int negativeChapters = -1;

        // Act
        CourseDTO courseDTO = new CourseDTO(
            TEST_COURSE_ID, TEST_TITLE, TEST_DESCRIPTION, TEST_DIFFICULTY,
            TEST_ESTIMATED_TIME, TEST_ICON_KEY, TEST_TAGS, negativeChapters
        );

        // Assert
        assertEquals(negativeChapters, courseDTO.getChapters());
    }
}
