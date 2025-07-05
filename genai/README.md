# GenAI Integration Documentation

## Overview

The GenAI (Generative AI) service provides intelligent feedback for quiz answers by comparing user responses to sample solutions using both cloud-based and local Large Language Models (LLMs).

## Features

- **Semantic Similarity Analysis**: Compares user answers with sample solutions using advanced NLP techniques
- **AI-Powered Feedback Generation**: Provides constructive feedback with strengths, weaknesses, and suggestions
- **Dual Model Support**: Supports both OpenAI API (cloud) and GPT4All (local) models
- **Advanced Analysis**: Offers detailed semantic analysis with concept coverage tracking
- **RESTful API**: Easy integration with the quiz service through HTTP endpoints
- **Containerized Deployment**: Fully containerized microservice with health checks

## Architecture

```
Frontend (Vue.js)
    ↓ HTTP
Quiz Service (Spring Boot)
    ↓ HTTP
GenAI Service (FastAPI + Python)
    ↓
[OpenAI API] / [Local GPT4All Model]
```

## API Endpoints

### GenAI Service (Port 8084)

#### Health Check

- `GET /health` - Service health and model availability
- `GET /` - Basic service info

#### Feedback Generation

- `POST /api/feedback` - Generate feedback using specified model
- `POST /api/feedback/advanced` - Generate advanced semantic feedback
- `GET /api/models` - Get available AI models

#### Request Format

```json
{
  "question_text": "What is the purpose of Continuous Integration?",
  "user_answer": "CI automatically builds and tests code changes",
  "sample_solution": "Continuous Integration is a practice where...",
  "model_type": "local" // or "openai"
}
```

#### Response Format

```json
{
  "score": 0.85,
  "feedback": "Good understanding of CI concepts...",
  "suggestions": ["Include discussion of automated testing", "..."],
  "strengths": ["Correct identification of automation", "..."],
  "weaknesses": ["Missing integration benefits", "..."],
  "model_used": "local-gpt4all",
  "timestamp": "2025-07-05T10:30:00"
}
```

### Quiz Service Integration (Port 8081)

#### Answer Submission

- `POST /api/quiz/questions/{questionId}/submit` - Submit answer for feedback
- `POST /api/quiz/questions/{questionId}/submit/advanced` - Submit for advanced analysis
- `GET /api/quiz/genai/health` - Check GenAI service availability

## Configuration

### Environment Variables

#### GenAI Service

- `OPENAI_API_KEY` - OpenAI API key (optional, for cloud models)
- `SERVICE_HOST` - Service host (default: 0.0.0.0)
- `SERVICE_PORT` - Service port (default: 8084)
- `LOG_LEVEL` - Logging level (default: INFO)

#### Quiz Service

- `GENAI_SERVICE_URL` - GenAI service URL (default: http://genai-service:8084)

### Model Configuration

The service automatically initializes available models:

1. **OpenAI Models** (if API key provided):

   - GPT-3.5-turbo for high-quality feedback
   - Requires internet connection and API credits

2. **Local Models**:
   - GPT4All with Orca Mini 3B model
   - Runs entirely offline
   - Smaller model size for faster inference

## Deployment

### Docker Compose

The GenAI service is included in the main docker-compose.yml:

```yaml
genai-service:
  build: ./genai
  container_name: studyapp_genai_service
  restart: always
  ports:
    - "8084:8084"
  environment:
    - OPENAI_API_KEY=${OPENAI_API_KEY:-}
  volumes:
    - genai_models:/app/models
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:8084/health"]
    interval: 30s
    timeout: 10s
    retries: 3
```

### Running the Services

1. **Start all services**:

   ```bash
   docker-compose up -d
   ```

2. **Check service health**:

   ```bash
   curl http://localhost:8084/health
   curl http://localhost:8081/api/quiz/genai/health
   ```

3. **Test integration**:
   ```bash
   python test_genai_integration.py
   ```

## Usage in Frontend

The Vue.js frontend integrates with the GenAI feedback through the quiz interface:

1. **Answer Submission**: Users type their answers and click "Submit Answer"
2. **AI Analysis**: The system sends the answer to the GenAI service
3. **Feedback Display**: Users see:
   - Numerical score (0-100%)
   - Overall feedback text
   - Strengths identified
   - Areas for improvement
   - Specific suggestions

### Frontend Features

- **Real-time feedback**: Immediate AI analysis after submission
- **Advanced analysis**: Optional deeper semantic analysis
- **Visual indicators**: Color-coded scores and progress bars
- **Model transparency**: Shows which AI model provided the feedback

## Development

### Adding New Models

1. **Local Models**: Add to `initialize_models()` in `main.py`
2. **Cloud Models**: Add API integration in `generate_*_feedback()` functions

### Extending Feedback Analysis

The `AdvancedFeedbackAnalyzer` class can be extended with:

- Named Entity Recognition (NER)
- Topic modeling
- Concept graph analysis
- Multi-language support

### Testing

Run the integration test suite:

```bash
python test_genai_integration.py
```

## Troubleshooting

### Common Issues

1. **GenAI Service Not Available**:

   - Check if service is running: `docker-compose ps`
   - Check logs: `docker-compose logs genai-service`
   - Verify network connectivity between services

2. **Model Loading Failures**:

   - Check available disk space for model downloads
   - Verify internet connection for initial model download
   - Check logs for specific error messages

3. **OpenAI API Issues**:

   - Verify API key is correctly set
   - Check API quota and billing
   - Ensure API key has appropriate permissions

4. **Slow Response Times**:
   - Local models: Check available RAM and CPU
   - OpenAI models: Check internet connection
   - Consider adjusting timeout values

### Performance Optimization

1. **Model Caching**: Models are cached in persistent volumes
2. **Request Timeouts**: Configured for reasonable response times
3. **Fallback Responses**: Service provides fallback when AI is unavailable
4. **Health Checks**: Automatic service monitoring and restart

## Security Considerations

1. **API Keys**: Store OpenAI API keys securely in environment variables
2. **Network Security**: Services communicate through internal Docker network
3. **Input Validation**: All inputs are validated before processing
4. **Rate Limiting**: Consider adding rate limiting for production use

## Future Enhancements

1. **Additional Models**: Support for more local and cloud models
2. **Caching**: Response caching for identical questions/answers
3. **Analytics**: Feedback analytics and learning insights
4. **Personalization**: Adaptive feedback based on user learning patterns
5. **Multi-language**: Support for multiple languages
