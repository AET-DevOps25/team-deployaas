package com.aet.studyassistant.quiz_service.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChapterTest {

    private static final String TEST_CHAPTER_NAME = "CI/CD Fundamentals";
    private static final UUID TEST_COURSE_ID = UUID.randomUUID();
    private static final UUID TEST_CHAPTER_ID = UUID.randomUUID();

    @Test
    void defaultConstructorCreatesValidChapter() {
        // Arrange & Act
        Chapter chapter = new Chapter();

        // Assert
        assertNull(chapter.getId());
        assertNull(chapter.getName());
        assertNull(chapter.getCourseId());
        assertNull(chapter.getQuestions());
    }

    @Test
    void parameterizedConstructorCreatesCorrectChapter() {
        // Arrange & Act
        Chapter chapter = new Chapter(TEST_CHAPTER_NAME, TEST_COURSE_ID);

        // Assert
        assertNull(chapter.getId());
        assertEquals(TEST_CHAPTER_NAME, chapter.getName());
        assertEquals(TEST_COURSE_ID, chapter.getCourseId());
        assertNull(chapter.getQuestions());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        // Arrange
        Chapter chapter = new Chapter();
        Question question1 = new Question("Question 1", "Solution 1", 1, chapter);
        Question question2 = new Question("Question 2", "Solution 2", 2, chapter);
        List<Question> questions = Arrays.asList(question1, question2);

        // Act
        chapter.setId(TEST_CHAPTER_ID);
        chapter.setName(TEST_CHAPTER_NAME);
        chapter.setCourseId(TEST_COURSE_ID);
        chapter.setQuestions(questions);

        // Assert
        assertEquals(TEST_CHAPTER_ID, chapter.getId());
        assertEquals(TEST_CHAPTER_NAME, chapter.getName());
        assertEquals(TEST_COURSE_ID, chapter.getCourseId());
        assertEquals(questions, chapter.getQuestions());
        assertEquals(2, chapter.getQuestions().size());
    }

    @Test
    void gettersReturnCorrectValues() {
        // Arrange
        Chapter chapter = new Chapter(TEST_CHAPTER_NAME, TEST_COURSE_ID);
        chapter.setId(TEST_CHAPTER_ID);

        // Act & Assert
        assertEquals(TEST_CHAPTER_ID, chapter.getId());
        assertEquals(TEST_CHAPTER_NAME, chapter.getName());
        assertEquals(TEST_COURSE_ID, chapter.getCourseId());
    }

    @Test
    void chapterHandlesNullName() {
        // Arrange & Act
        Chapter chapter = new Chapter(null, TEST_COURSE_ID);

        // Assert
        assertNull(chapter.getName());
        assertEquals(TEST_COURSE_ID, chapter.getCourseId());
    }

    @Test
    void chapterHandlesNullCourseId() {
        // Arrange & Act
        Chapter chapter = new Chapter(TEST_CHAPTER_NAME, null);

        // Assert
        assertEquals(TEST_CHAPTER_NAME, chapter.getName());
        assertNull(chapter.getCourseId());
    }

    @Test
    void chapterHandlesEmptyName() {
        // Arrange
        String emptyName = "";

        // Act
        Chapter chapter = new Chapter(emptyName, TEST_COURSE_ID);

        // Assert
        assertEquals(emptyName, chapter.getName());
        assertEquals(TEST_COURSE_ID, chapter.getCourseId());
    }

    @Test
    void chapterHandlesEmptyQuestionsList() {
        // Arrange
        Chapter chapter = new Chapter(TEST_CHAPTER_NAME, TEST_COURSE_ID);
        List<Question> emptyQuestions = Arrays.asList();

        // Act
        chapter.setQuestions(emptyQuestions);

        // Assert
        assertEquals(emptyQuestions, chapter.getQuestions());
        assertTrue(chapter.getQuestions().isEmpty());
    }

    @Test
    void chapterHandlesNullQuestionsList() {
        // Arrange
        Chapter chapter = new Chapter(TEST_CHAPTER_NAME, TEST_COURSE_ID);

        // Act
        chapter.setQuestions(null);

        // Assert
        assertNull(chapter.getQuestions());
    }

    @Test
    void chapterHandlesBothNullParameters() {
        // Arrange & Act
        Chapter chapter = new Chapter(null, null);

        // Assert
        assertNull(chapter.getName());
        assertNull(chapter.getCourseId());
    }
}
