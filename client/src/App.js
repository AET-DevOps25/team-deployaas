import React, { useEffect, useState } from "react";

function App() {
  const [message, setMessage] = useState("");
  const [quizMessage, setQuizMessage] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/api/hello")
      .then((res) => res.text())
      .then(setMessage)
      .catch((err) => console.error("Error fetching:", err));

    fetch("http://localhost:8081/api/quiz/test")
      .then((res) => res.text())
      .then(setQuizMessage)
      .catch((err) => console.error("Error fetching from quiz service:", err));
  }, []);

  return (
    <div style={{ padding: "2rem" }}>
      <h1>{message || "Loading..."}</h1>
      <h2>{quizMessage || "Loading quiz service message..."}</h2>
    </div>
  );
}

export default App;
