# 🎓 Study Assistant

**An Educational Microservices Platform for DevOps Learning**

[![Docker Compose](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)](docker-compose.yml)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Ready-green?logo=kubernetes)](helm/study-assistant/)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-yellow?logo=github)](/.github/workflows/)
[![Monitoring](https://img.shields.io/badge/Monitoring-Prometheus%2FGrafana-orange?logo=prometheus)](prometheus/)

---

## 🌟 Overview

The Study Assistant Platform is an educational microservice system designed to help students through interactive quizzes, AI-powered feedback, and spaced repetition flashcards.

### 🎯 Main Functionality

A web-based educational platform that helps students deepen their understanding of different topics by:

- **Interactive Quizzes**: Practice open-ended questions organized by course chapters
- **AI-Powered Feedback**: Receive immediate, personalized feedback using advanced language models
- **Flashcard System**: Create and study flashcards with spaced repetition for long-term retention
- **Progress Tracking**: Monitor learning progress across courses and topics
- **Anki Integration**: Export flashcards to popular spaced repetition software

### 👥 Intended Users

- University students enrolled in software engineering courses
- Self-learners preparing for job interviews
- Professionals looking to enhance their knowledge

### 🤖 GenAI Integration

The AI engine powers the feedback system by:

- Comparing user-submitted text answers against sample solutions using semantic similarity
- Generating constructive, targeted feedback highlighting strengths and areas for improvement
- Providing different feedback types (basic, advanced, semantic analysis)
- Adapting responses based on question context and user performance

---

### Technology Stack

- **Frontend**: Vue.js 3 + Vite + Tailwind CSS
- **Backend**: Spring Boot 3.4.5 (Java 21) + FastAPI (Python)
- **Database**: PostgreSQL 14
- **Authentication**: JWT with Spring Security
- **AI Integration**: Open WebUI API with Llama 3.3
- **Monitoring**: Prometheus + Grafana + Alertmanager
- **Containerization**: Docker + Docker Compose
- **Orchestration**: Kubernetes with Helm charts
- **CI/CD**: GitHub Actions

---

## ✨ Features

### 🔐 User Management

- Secure user registration and authentication
- JWT-based session management

### 📚 Learning Content

- **Courses**: Browse courses
- **Chapters**: Navigate through structured learning content
- **Questions**: Practice with open-ended quiz questions
- **Sample Solutions**: Compare answers against solutions

### 🤖 AI-Powered Feedback

- **Basic Feedback**: Simple correctness assessment
- **Advanced Feedback**: Detailed analysis with strengths/weaknesses
- **Semantic Analysis**: Similarity scoring between answers

### 📇 Flashcard System

- **Deck Management**: Create, edit, and organize flashcard decks
- **Study Mode**: Interactive flashcard review sessions
- **Anki Export**: Compatible with popular spaced repetition tools

---

## 🚀 Quick Start

### Prerequisites

- **Docker** and **Docker Compose** installed
- **Git** for cloning the repository

### 1. Clone the Repository

```bash
git clone https://github.com/aet-devops25/team-deployaas.git
cd team-deployaas
```

### 2. Configure Environment

Create a `.env` file in the project root:

```bash
# Database Configuration
POSTGRES_USER=initexample
POSTGRES_PASSWORD=initexample
POSTGRES_DB=initexample

# JWT Configuration
JWT_SECRET=yourSecureJWTSecret256BitKey

# GenAI Service Configuration
WEBUI_API_KEY=your-openwebui-api-key
```

### 3. Start the Application

```bash
# Start all services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f
```

### 4. Access the Application

- **Main Application**: http://localhost:3000
- **API Documentation**:
  - Auth Service: http://localhost:8083/auth/swagger-ui/index.html
  - Quiz Service: http://localhost:8081/quiz/swagger-ui/index.html
  - Flashcard Service: http://localhost:8082/flashcard/swagger-ui/index.html
  - GenAI Service: http://localhost:5001/docs

### 5. Monitoring Dashboards

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (admin/admin)
- **Alertmanager**: http://localhost:9093
- **PgAdmin**: http://localhost:8080 (admin@you.com/AdminPass123!)

---

## 📖 Usage Guide

### Getting Started

1. **Register an Account**

   - Navigate to http://localhost:3000
   - Click "Register" and create your account

2. **Explore Courses**

   - Browse available courses
   - View course details and difficulty levels

3. **Take Your First Quiz**

   - Select a course and chapter
   - Answer open-ended questions
   - Submit your responses for AI feedback

4. **Review AI Feedback**

   - **Basic**: Simple correctness assessment
   - **Advanced**: Detailed analysis with improvement suggestions
   - **Semantic**: Similarity scoring and comparative analysis

5. **Study with Flashcards**
   - Access your flashcard decks
   - Create custom cards from quiz content
   - Study with spaced repetition techniques
   - Export to Anki for mobile studying

### Example Learning Scenarios

#### 🎯 Scenario 1: Quiz Taking with AI Feedback

**Lisa studies Continuous Integration**

1. Select "DevOps Fundamentals" → "Chapter 2: CI/CD"
2. Answer: "CI helps developers merge code regularly"
3. Receive feedback: "Good start! Also mention automated testing and early bug detection"
4. Learn from detailed suggestions and try again

#### 📚 Scenario 2: Flashcard Creation and Study

**Alex reinforces Infrastructure as Code concepts**

1. Complete "Chapter 3: Infrastructure as Code" quiz
2. Create custom flashcard deck from difficult questions
3. Add personal notes and examples
4. Export to Anki for daily review sessions

#### 📊 Scenario 3: Review Flashcards directly

**Priya want to study flashcards directly**

1. Select a created flashcard deck
2. Click Review
4. Answer whether the solution you came up with matches with the sample solution

### Advanced Features

## 📚 API Documentation

### Authentication Endpoints

```http
POST /api/auth/register    # User registration
POST /api/auth/login       # User login
GET  /api/auth/test        # Health check
```

### Quiz Management

```http
GET  /api/quiz/courses                      # List all courses
GET  /api/quiz/courses/{id}                 # Get course details
GET  /api/quiz/chapters/{id}/questions      # Get chapter questions
POST /api/quiz/questions/{id}/submit        # Submit basic answer
POST /api/quiz/questions/{id}/submit/advanced   # Submit for advanced feedback
POST /api/quiz/questions/{id}/submit/semantic   # Submit for semantic analysis
```

### Flashcard Management

```http
GET    /api/flashcard/decks/user/{userId}           # List user decks
POST   /api/flashcard/decks                         # Create deck
GET    /api/flashcard/decks/{deckId}/flashcards     # List flashcards
POST   /api/flashcard/decks/{deckId}/flashcards     # Create flashcard
PUT    /api/flashcard/flashcards/{id}               # Update flashcard
DELETE /api/flashcard/flashcards/{id}               # Delete flashcard
```

### AI Feedback Service

```http
POST /feedback            # Basic AI feedback
POST /feedback/advanced   # Advanced feedback analysis
POST /feedback/semantic   # Semantic similarity analysis
GET  /health             # Service health check
```

Interactive API documentation is available at:

- Swagger UI for each service (see Quick Start section)
- FastAPI auto-docs for GenAI service

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
