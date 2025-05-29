package com.aet.studyassistant.quiz_service.service;

import com.aet.studyassistant.quiz_service.model.Question;
import com.aet.studyassistant.quiz_service.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuestionService {
    
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getQuestionsByChapterId(UUID chapterId) {
        return questionRepository.findByChapterIdOrderByOrdering(chapterId);
    }

    public Optional<Question> getQuestionById(UUID questionId) {
        return questionRepository.findById(questionId);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
}
