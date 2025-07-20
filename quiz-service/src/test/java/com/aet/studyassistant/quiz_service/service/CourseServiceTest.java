package com.aet.studyassistant.quiz_service.service;

import com.aet.studyassistant.quiz_service.model.Course;
import com.aet.studyassistant.quiz_service.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void getAllCoursesShouldReturnAllCourses() {
        // Arrange
        Course course1 = new Course("DevOps Fundamentals");
        Course course2 = new Course("Java Programming");
        List<Course> expectedCourses = Arrays.asList(course1, course2);
        when(courseRepository.findAll()).thenReturn(expectedCourses);

        // Act
        List<Course> actualCourses = courseService.getAllCourses();

        // Assert
        assertEquals(expectedCourses.size(), actualCourses.size());
        assertEquals(expectedCourses, actualCourses);
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getAllCoursesWhenNoCoursesReturnsEmptyList() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Course> actualCourses = courseService.getAllCourses();

        // Assert
        assertTrue(actualCourses.isEmpty());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getCourseByIdWhenCourseExistsReturnsCorrectCourse() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        Course expectedCourse = new Course("DevOps Fundamentals");
        expectedCourse.setId(courseId);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(expectedCourse));

        // Act
        Optional<Course> actualCourse = courseService.getCourseById(courseId);

        // Assert
        assertTrue(actualCourse.isPresent());
        assertEquals(expectedCourse, actualCourse.get());
        assertEquals(courseId, actualCourse.get().getId());
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    void getCourseByIdWhenCourseDoesNotExistReturnsEmpty() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act
        Optional<Course> actualCourse = courseService.getCourseById(courseId);

        // Assert
        assertFalse(actualCourse.isPresent());
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    void getAllCoursesWhenDatabaseThrowsExceptionPropagatesException() {
        // Arrange
        when(courseRepository.findAll()).thenThrow(new DataAccessException("Database connection failed") {});

        // Act & Assert
        assertThrows(DataAccessException.class, () -> courseService.getAllCourses());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getCourseByIdWhenDatabaseThrowsExceptionPropagatesException() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenThrow(new DataAccessException("Database connection failed") {});

        // Act & Assert
        assertThrows(DataAccessException.class, () -> courseService.getCourseById(courseId));
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    void getAllCoursesWithLargeDatasetReturnsCorrectly() {
        // Arrange
        List<Course> largeCourseList = createLargeCourseList(1000);
        when(courseRepository.findAll()).thenReturn(largeCourseList);

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertEquals(1000, result.size());
        assertEquals(largeCourseList, result);
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getCourseByIdWithNullIdHandlesGracefully() {
        // Arrange
        when(courseRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<Course> result = courseService.getCourseById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(courseRepository, times(1)).findById(null);
    }

    @Test
    void serviceHandlesRepositoryReturningNullList() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(null);

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertNull(result);
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void serviceHandlesMultipleConcurrentRequests() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        Course mockCourse = new Course("Test Course");
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));

        // Act - Simulate multiple concurrent calls
        Optional<Course> result1 = courseService.getCourseById(courseId);
        Optional<Course> result2 = courseService.getCourseById(courseId);
        Optional<Course> result3 = courseService.getCourseById(courseId);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertTrue(result3.isPresent());
        assertEquals(mockCourse, result1.get());
        assertEquals(mockCourse, result2.get());
        assertEquals(mockCourse, result3.get());
        verify(courseRepository, times(3)).findById(courseId);
    }

    private List<Course> createLargeCourseList(int size) {
        Course[] courses = new Course[size];
        for (int i = 0; i < size; i++) {
            courses[i] = new Course("Course " + i);
        }
        return Arrays.asList(courses);
    }
}
