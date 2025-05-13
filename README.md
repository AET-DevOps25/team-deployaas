# 📅 Weekly Project Report Template

**Week:** 1 (05/05/2025 - 05/11/2025)

**Participants:** Manuel Tamayo Moreno, Yuyang Cao, Marcel Skalski

| Student Name | Weekly Progress | Impediments | Promises (Next Week) | Comments |
| :----------- | :-------------- | :---------- | :------------------- | :------- |
| Manuel Tamayo Moreno | Come up with Problem Statement |                  | Create System Architecture |             |
| Yuyang Cao | Come up with Problem Statement |                  | Create System Architecture |             |
| Marcel Skalski | Come up with Problem Statement |                  | Create System Architecture |             |

---

**Week:** 2 (05/12/2025 - 05/18/2025)

**Participants:** Manuel Tamayo Moreno, Yuyang Cao, Marcel Skalski

| Student Name | Weekly Progress | Impediments | Promises (Next Week) | Comments |
| :----------- | :-------------- | :---------- | :------------------- | :------- |
| Manuel Tamayo Moreno |  |  |  |  |
| Yuyang Cao |  |  |  |  |
| Marcel Skalski |  |  |  |  |

---

## Discussion

Use this space for any team-wide questions, decisions or side-conversations that emerged from your individual updates:

* **Requester:** 
  * **Topic:** 
  * **Decision:**
 
---

# 📌 Problem Statement

**Study Assistant App**

**Main Functionality:**

A web application that helps students actively test their understanding by automatically generating quizzes from their study materials. Users upload documents (e.g., lecture slides, notes, textbook excerpts), and the system creates multiple-choice or short-answer quizzes tailored to the content. The core feature is a GenAI-powered quiz engine that builds personalized, context-aware questions and explanations.

**Intended Users:**

- University and high school students studying for exams
- Self-learners using online materials
- Tutors or educators looking for quick quiz generation based on content

**GenAI Integration:**

The GenAI module (LangChain-based Python service) will:

- Parse uploaded study material (PDF, Markdown, plain text) and generate structured quiz questions
- Provide detailed explanations for answers when requested
- Use a vector database like Weaviate to store and retrieve relevant content for Retrieval-Augmented Generation (RAG) during follow-up queries (e.g., “Why is option C correct?”)

**Example Scenarios:**

1. **Quiz Generation:**
    - Tom, a university student, wants to test his understanding of “Computer Networks – TCP/IP.” He uploads a chapter on the topic to the platform. The system automatically generates a 10-question multiple-choice quiz that covers key concepts from the material. Tom takes the quiz directly within the app to assess his knowledge.
2. **Answer Explanation:**
    - After completing a quiz, Sara is unsure why one of her answers was incorrect. She clicks on the question and asks, “Why is this the correct answer?” The GenAI service analyzes the source material and provides a clear, contextual explanation that helps her understand the concept better.
3. **Review Mode:**
    - David wants to review a quiz he took earlier in the week. The app allows him to revisit each question, see which ones he got right or wrong, and access AI-generated clarifications. In some cases, it even suggests specific sections of the original material for him to review.
