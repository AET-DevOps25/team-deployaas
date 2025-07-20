package com.aet.studyassistant.quiz_service.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    private static final String TEST_TITLE = "DevOps Fundamentals";
    private static final UUID TEST_COURSE_ID = UUID.randomUUID();

    @Test
    void defaultConstructorCreatesValidCourse() {
        // Arrange & Act
        Course course = new Course();

        // Assert
        assertNull(course.getId());
        assertNull(course.getTitle());
        assertNull(course.getChapters());
    }

    @Test
    void parameterizedConstructorCreatesCorrectCourse() {
        // Arrange & Act
        Course course = new Course(TEST_TITLE);

        // Assert
        assertNull(course.getId());
        assertEquals(TEST_TITLE, course.getTitle());
        assertNull(course.getChapters());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        // Arrange
        Course course = new Course();
        Chapter chapter1 = new Chapter("Chapter 1", TEST_COURSE_ID);
        Chapter chapter2 = new Chapter("Chapter 2", TEST_COURSE_ID);
        List<Chapter> chapters = Arrays.asList(chapter1, chapter2);

        // Act
        course.setId(TEST_COURSE_ID);
        course.setTitle(TEST_TITLE);
        course.setChapters(chapters);

        // Assert
        assertEquals(TEST_COURSE_ID, course.getId());
        assertEquals(TEST_TITLE, course.getTitle());
        assertEquals(chapters, course.getChapters());
        assertEquals(2, course.getChapters().size());
    }

    @Test
    void gettersReturnCorrectValues() {
        // Arrange
        Course course = new Course(TEST_TITLE);
        course.setId(TEST_COURSE_ID);

        // Act & Assert
        assertEquals(TEST_COURSE_ID, course.getId());
        assertEquals(TEST_TITLE, course.getTitle());
    }

    @Test
    void courseHandlesNullTitle() {
        // Arrange & Act
        Course course = new Course(null);

        // Assert
        assertNull(course.getTitle());
    }

    @Test
    void courseHandlesEmptyTitle() {
        // Arrange
        String emptyTitle = "";

        // Act
        Course course = new Course(emptyTitle);

        // Assert
        assertEquals(emptyTitle, course.getTitle());
    }

    @Test
    void courseHandlesEmptyChaptersList() {
        // Arrange
        Course course = new Course(TEST_TITLE);
        List<Chapter> emptyChapters = Arrays.asList();

        // Act
        course.setChapters(emptyChapters);

        // Assert
        assertEquals(emptyChapters, course.getChapters());
        assertTrue(course.getChapters().isEmpty());
    }

    @Test
    void courseHandlesNullChaptersList() {
        // Arrange
        Course course = new Course(TEST_TITLE);

        // Act
        course.setChapters(null);

        // Assert
        assertNull(course.getChapters());
    }
}
