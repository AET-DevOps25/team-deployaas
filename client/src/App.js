import React, { useEffect, useState } from "react";

function App() {
  // read from build-time env vars
  const AUTH_HOST = process.env.REACT_APP_AUTH_HOST;
  const QUIZ_HOST = process.env.REACT_APP_QUIZ_HOST;
  const FLASHCARD_HOST = process.env.REACT_APP_FLASHCARD_HOST;

  const [authMessage, setAuthMessage] = useState("");
  const [quizMessage, setQuizMessage] = useState("");
  const [flashcardMessage, setFlashcardMessage] = useState("");
  const [chapters, setChapters] = useState([]);
  const [selectedChapterId, setSelectedChapterId] = useState(null);

  useEffect(() => {
    fetch(`https://${QUIZ_HOST}/api/quiz/test`)
      .then((res) => res.text())
      .then(setQuizMessage)
      .catch((err) =>
        console.error("Error fetching from quiz service:", err)
      );

    fetch(`https://${FLASHCARD_HOST}/api/flashcard/test`)
      .then((res) => res.text())
      .then(setFlashcardMessage)
      .catch((err) =>
        console.error("Error fetching from flashcard service:", err)
      );

    fetch(`https://${AUTH_HOST}/api/auth/test`)
      .then((res) => res.text())
      .then(setAuthMessage)
      .catch((err) => console.error("Error fetching from auth service:", err));

    fetch(`https://${QUIZ_HOST}/api/quiz/chapters`)
      .then((res) => res.json())
      .then(setChapters)
      .catch((err) => console.error("Error fetching chapters:", err));
  }, [AUTH_HOST, QUIZ_HOST, FLASHCARD_HOST]);

  const handleChapterSelect = (chapterId) => {
    setSelectedChapterId(chapterId);
  };

  const selectedChapter = chapters.find((c) => c.id === selectedChapterId);

  return (
    <div style={{ padding: "2rem" }}>
      <h3>{authMessage || "Loading auth service message..."}</h3>
      <h3>{quizMessage || "Loading quiz service message..."}</h3>
      <h3>{flashcardMessage || "Loading flashcard service message..."}</h3>
      <hr />
      <h2>Select a Chapter:</h2>
      <ul>
        {chapters.map((chapter) => (
          <li key={chapter.id}>
            <button onClick={() => handleChapterSelect(chapter.id)}>
              {chapter.name}
            </button>
          </li>
        ))}
      </ul>
      {selectedChapter && (
        <div>
          <h3>Quizzes in "{selectedChapter.name}"</h3>
          <ul>
            {selectedChapter.quizzes && selectedChapter.quizzes.length > 0 ? (
              selectedChapter.quizzes.map((quiz) => (
                <li key={quiz.id}>{quiz.title}</li>
              ))
            ) : (
              <li>No quizzes available for this chapter.</li>
            )}
          </ul>
        </div>
      )}
    </div>
  );
}

export default App;
