import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Homepage from "./components/Homepage";
import Quiz from "./components/Quiz";

function App() {
  return (
    <Router>
      <div>
        <Routes>
          <Route path="/" element={<Homepage />} />
          <Route path="/quiz/:chapterId" element={<Quiz />} />
        </Routes>
      </div>
    </Router>
  );
}
export default App;
