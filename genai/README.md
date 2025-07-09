# GenAI Integration Documentation

## Overview

The GenAI (Generative AI) service provides intelligent feedback for quiz answers by comparing user responses to sample solutions using both cloud-based and local Large Language Models (LLMs).

## Features

- **Semantic Similarity Analysis**: Compares user answers with sample solutions using advanced NLP techniques
- **AI-Powered Feedback Generation**: Provides constructive feedback with strengths, weaknesses, and suggestions
- **Multiple AI Models**: Supports OpenAI API (cloud), Lightweight AI (local), and Advanced Analyzer (fallback)
- **Fast Local Processing**: Lightweight sentence transformers (~80MB) for sub-second response times
- **Smart Fallback System**: Automatic fallback between models for maximum reliability
- **Semantic Analysis**: Detailed semantic analysis with concept coverage tracking
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
┌─ OpenAI API (Cloud)
├─ Lightweight AI (Local: ~80MB, <1s)
└─ Advanced Analyzer (Fallback: Semantic similarity)
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
  "model_type": "local" // "openai", "local", or "advanced"
}
```

#### Response Format

```json
{
  "feedback": "Good understanding of CI concepts...",
  "suggestions": ["Include discussion of automated testing", "..."],
  "strengths": ["Correct identification of automation", "..."],
  "weaknesses": ["Missing integration benefits", "..."],
  "model_used": "lightweight-ai",
  "timestamp": "2025-07-05T10:30:00"
}
```

### Quiz Service Integration (Port 8081)

#### Answer Submission

- `POST /api/quiz/questions/{questionId}/submit` - Submit answer for feedback
- `POST /api/quiz/questions/{questionId}/submit/advanced` - Submit for semantic analysis
- `GET /api/quiz/genai/health` - Check GenAI service availability

## Model Architecture

### Lightweight AI (Primary Local Model)

The lightweight AI implementation uses optimized sentence transformers for fast, accurate feedback:

- **Model**: `all-MiniLM-L6-v2` sentence transformer
- **Size**: ~80MB (vs 3-7GB for traditional LLMs)
- **Speed**: 0.5-1 second response time
- **Memory**: ~200MB RAM usage
- **Accuracy**: High semantic similarity detection
- **Offline**: Fully functional without internet

### Processing Pipeline

1. **Semantic Analysis**: Calculate cosine similarity between embeddings
2. **Concept Extraction**: Identify key concepts using NLP techniques
3. **Feedback Generation**: Rule-based feedback with AI-enhanced scoring
4. **Structured Output**: Consistent format with feedback, strengths, weaknesses, and suggestions

### Fallback Strategy

```
Request → OpenAI API (if available & requested)
            ↓ (on failure)
         Lightweight AI (primary local)
            ↓ (on failure)
         Advanced Analyzer (always available)
```

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

The service automatically initializes available models in priority order:

1. **OpenAI Models** (if API key provided):

   - GPT-3.5-turbo for high-quality feedback
   - Requires internet connection and API credits
   - Response time: 2-5 seconds

2. **Lightweight AI** (primary local model):

   - Sentence transformers with semantic similarity analysis
   - ~80MB model size (vs 3-7GB for traditional models)
   - Response time: 0.5-1 seconds
   - Runs entirely offline

3. **Advanced Analyzer** (fallback):
   - Rule-based semantic similarity analysis
   - Always available as last resort
   - Response time: <0.5 seconds

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
   # Test the GenAI service directly
   curl -X POST http://localhost:8084/api/feedback \
     -H "Content-Type: application/json" \
     -d '{
       "question_text": "What is CI?",
       "user_answer": "Continuous Integration automatically builds and tests code",
       "sample_solution": "CI is a development practice...",
       "model_type": "local"
     }'

   # Or run the integration test script
   python test_genai_integration.py
   ```

## Usage in Frontend

The Vue.js frontend integrates with the GenAI feedback through the quiz interface:

1. **Answer Submission**: Users type their answers and click "Submit Answer"
2. **AI Analysis**: The system sends the answer to the GenAI service
3. **Feedback Display**: Users see:
   - Overall feedback text
   - Strengths identified
   - Areas for improvement
   - Specific suggestions

### Frontend Features

- **Real-time feedback**: Immediate AI analysis after submission
- **Semantic analysis**: Optional deeper semantic analysis
- **Comprehensive feedback**: Detailed feedback with strengths, weaknesses, and suggestions
- **Model selection**: Choose between OpenAI, local AI, or analyzer
- **Performance indicators**: Response time and model used displayed

## Development

### Adding New Models

1. **Local Models**:

   - Add new model class in `local_llm_real.py` or create new module
   - Register in `initialize_models()` in `main.py`
   - Add model availability check in health endpoints

2. **Cloud Models**:
   - Add API integration in new `generate_*_feedback()` function
   - Update model routing logic in main feedback endpoint

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

   - Check available disk space for model downloads (~80MB for sentence transformers)
   - Verify internet connection for initial model download
   - Check logs for specific error messages
   - Ensure models directory is writable: `docker-compose logs genai-service`

3. **OpenAI API Issues**:

   - Verify API key is correctly set
   - Check API quota and billing
   - Ensure API key has appropriate permissions

4. **Slow Response Times**:
   - **Lightweight AI**: Check available RAM (requires ~200MB) and CPU
   - **OpenAI models**: Check internet connection and API response times
   - **Advanced Analyzer**: Should be <0.5s, check system load
   - Consider adjusting timeout values in docker-compose.yml

### Performance Optimization

1. **Model Caching**: Sentence transformer models are cached in persistent volumes
2. **Smart Fallbacks**: Automatic fallback from OpenAI → Lightweight AI → Advanced Analyzer
3. **Request Timeouts**: Configured for reasonable response times (30s max)
4. **Health Checks**: Automatic service monitoring and restart
5. **Memory Efficiency**: Lightweight models use ~200MB RAM vs 4-8GB for traditional LLMs

## Security Considerations

1. **API Keys**: Store OpenAI API keys securely in environment variables
2. **Network Security**: Services communicate through internal Docker network
3. **Input Validation**: All inputs are validated before processing
4. **Rate Limiting**: Consider adding rate limiting for production use

## Future Enhancements

1. **Additional Models**: Support for more lightweight transformers and cloud models
2. **Response Caching**: Cache responses for identical questions/answers
3. **Analytics Dashboard**: Feedback analytics and learning insights
4. **Personalization**: Adaptive feedback based on user learning patterns
5. **Multi-language Support**: International language support for feedback
6. **Advanced NLP**: Named Entity Recognition and topic modeling
7. **Performance Monitoring**: Detailed metrics and monitoring dashboard
