# 📌 Problem Statement

**Study Assistant App**

**Main Functionality:**

A web-based educational platform designed to help students deepen their understanding of DevOps by practicing open-ended quiz questions and receiving AI-powered feedback. This application offers a curated set of quizzes, organized by course chapters. Students answer text-based questions and receive immediate feedback comparing their response to a model solution, helping them identify areas for improvement. To support long-term retention, quizzes can also be exported as flashcards compatible with Anki, enabling spaced repetition.

**Intended Users:**

- University students enrolled in DevOps or software engineering courses
- Self-learners preparing for DevOps certifications or job interviews

**GenAI Integration:**

The AI engine (e.g., based on LangChain) powers the feedback system by:

- Comparing user-submitted text answers against sample answers using semantic similarity metrics and large language model inference
- Generating constructive, targeted feedback that helps users understand what they got right, what was missing, and how to improve

**Example Scenarios:**

1. **Quiz Generation:**

   - Lisa answers a question about the purpose of Continuous Integration (CI). Her response is close but misses a key point about automated testing. The AI feedback points this out and suggests how to refine her explanation.

2. **Answer Explanation:**

   - Alex completes the “Chapter 2: Infrastructure as Code” quiz and wants to export the quiz to flashcards. He imports this into [Anki](https://apps.ankiweb.net) to reinforce the material with spaced repetition.

3. **Review Mode:**
   - After finishing all quizzes in “Chapter 3: Virtualization,” Priya reviews her weakest answers and reads the AI's personalized feedback to solidity her understanding.
