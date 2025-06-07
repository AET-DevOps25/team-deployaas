import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Homepage.css";

function Homepage() {
  const [chapters, setChapters] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetch("http://localhost:8081/api/quiz/chapters")
      .then((res) => res.json())
      .then((data) => {
        setChapters(data);
      })
      .catch((err) => console.error("Error fetching chapters:", err));
  }, []);

  const handleChapterSelect = (chapterId) => {
    navigate(`/quiz/${chapterId}`);
  };

  return (
    <div className="homepage-container">
      <h1 className="homepage-title">Study Assistant</h1>
      <h2 className="homepage-subtitle">
        Select a Chapter to Start Your Quiz:
      </h2>

      {chapters.length === 0 ? (
        <p className="loading-message">Loading chapters...</p>
      ) : (
        <div className="chapters-grid">
          {chapters.map((chapter) => (
            <button
              key={chapter.id}
              onClick={() => handleChapterSelect(chapter.id)}
              className="chapter-button"
            >
              {chapter.name}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default Homepage;
