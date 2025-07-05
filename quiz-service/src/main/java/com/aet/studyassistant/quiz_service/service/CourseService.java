package com.aet.studyassistant.quiz_service.service;

import com.aet.studyassistant.quiz_service.model.Course;
import com.aet.studyassistant.quiz_service.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {
    
    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(UUID courseId) {
        return courseRepository.findById(courseId);
    }
}
