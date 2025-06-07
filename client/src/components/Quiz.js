import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./Quiz.css";

function Quiz() {
  const { chapterId } = useParams();
  const navigate = useNavigate();
  const [chapter, setChapter] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Fetch chapter details
    fetch(`http://localhost:8081/api/quiz/chapters/${chapterId}`)
      .then((res) => res.json())
      .then((data) => {
        setChapter(data);
      })
      .catch((err) => console.error("Error fetching chapter:", err));

    // Fetch questions for the selected chapter
    fetch(`http://localhost:8081/api/quiz/chapters/${chapterId}/questions`)
      .then((res) => res.json())
      .then((data) => {
        setQuestions(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error fetching questions:", err);
        setLoading(false);
      });
  }, [chapterId]);

  const handleAnswerChange = (value) => {
    setAnswers({
      ...answers,
      [currentQuestionIndex]: value,
    });
  };

  const handleNextQuestion = () => {
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    }
  };

  const handlePreviousQuestion = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(currentQuestionIndex - 1);
    }
  };

  const handleFinishQuiz = () => {
    // TODO: Implement quiz submission logic here
    alert("Quiz completed! Returning to homepage.");
    navigate("/");
  };

  const goToHomepage = () => {
    navigate("/");
  };

  if (loading) {
    return (
      <div className="loading-container">
        <h2>Loading quiz...</h2>
      </div>
    );
  }

  if (questions.length === 0) {
    return (
      <div className="no-questions-container">
        <h2>No questions found for this chapter</h2>
        <button onClick={goToHomepage} className="error-back-button">
          Back to Homepage
        </button>
      </div>
    );
  }

  const currentQuestion = questions[currentQuestionIndex];
  const currentAnswer = answers[currentQuestionIndex] || "";

  return (
    <div className="quiz-container">
      <div className="quiz-header">
        <button onClick={goToHomepage} className="back-button">
          ← Back to Chapters
        </button>
      </div>

      <div className="quiz-title-section">
        <h1 className="quiz-title">{chapter?.name || "Quiz"}</h1>
        <div className="progress-indicator">
          Question {currentQuestionIndex + 1} of {questions.length}
        </div>
      </div>

      <div className="question-card">
        <h3 className="question-text">{currentQuestion.text}</h3>

        <textarea
          value={currentAnswer}
          onChange={(e) => handleAnswerChange(e.target.value)}
          rows="6"
          className="answer-textarea"
          placeholder="Type your answer here..."
        />
      </div>

      <div className="navigation-controls">
        <button
          onClick={handlePreviousQuestion}
          disabled={currentQuestionIndex === 0}
          className="nav-button previous-button"
        >
          ← Previous
        </button>

        <div
          className={`answer-status ${currentAnswer.trim() ? "answered" : ""}`}
        >
          {currentAnswer.trim() ? "✓ Answered" : "Not answered yet"}
        </div>

        {currentQuestionIndex < questions.length - 1 ? (
          <button
            onClick={handleNextQuestion}
            className="nav-button next-button"
          >
            Next →
          </button>
        ) : (
          <button
            onClick={handleFinishQuiz}
            className="nav-button finish-button"
          >
            Finish Quiz
          </button>
        )}
      </div>

      {questions.length > 1 && (
        <div className="question-navigator">
          <div className="question-dots">
            {questions.map((question, index) => {
              const isCurrentQuestion = index === currentQuestionIndex;
              const isAnswered = answers[index]?.trim();

              let buttonClass = "question-dot";
              if (isCurrentQuestion) {
                buttonClass += " current";
              } else if (isAnswered) {
                buttonClass += " answered";
              }

              return (
                <button
                  key={`question-${question.id || index}`}
                  onClick={() => setCurrentQuestionIndex(index)}
                  className={buttonClass}
                >
                  {index + 1}
                </button>
              );
            })}
          </div>
          <p className="navigator-help">
            Click a number to jump to that question
          </p>
        </div>
      )}
    </div>
  );
}

export default Quiz;
