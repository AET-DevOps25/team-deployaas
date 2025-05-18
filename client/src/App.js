import React, { useEffect, useState } from "react";

function App() {
  const [authMessage, setAuthMessage] = useState("");
  const [quizMessage, setQuizMessage] = useState("");
  const [flashcardMessage, setFlashcardMessage] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/api/auth/test")
      .then((res) => res.text())
      .then(setAuthMessage)
      .catch((err) => console.error("Error fetching from auth service:", err));

    fetch("http://localhost:8081/api/quiz/test")
      .then((res) => res.text())
      .then(setQuizMessage)
      .catch((err) => console.error("Error fetching from quiz service:", err));

    fetch("http://localhost:8082/api/flashcard/test")
      .then((res) => res.text())
      .then(setFlashcardMessage)
      .catch((err) =>
        console.error("Error fetching from flashcard service:", err)
      );
  }, []);

  return (
    <div style={{ padding: "2rem" }}>
      <h1>{authMessage || "Loading auth service message..."}</h1>
      <h1>{quizMessage || "Loading quiz service message..."}</h1>
      <h1>{flashcardMessage || "Loading flashcard service message..."}</h1>
    </div>
  );
}

export default App;
