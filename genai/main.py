#!/usr/bin/env python3
"""
GenAI Feedback Service

A microservice that provides intelligent feedback for quiz answers by comparing
user responses to sample solutions using both cloud-based and local LLMs.

Features:
- Semantic similarity analysis
- AI-powered feedback generation
- Support for OpenAI API and lightweight local models
- RESTful API for integration with quiz service
"""

import os
import logging
import openai
import uvicorn
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, Dict, Any
from datetime import datetime
from feedback_analyzer import AdvancedFeedbackAnalyzer
from lightweight_ai import LightweightAI

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="GenAI Feedback Service",
    description="AI-powered quiz answer feedback system",
    version="1.0.0"
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, specify allowed origins
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Pydantic models
class FeedbackRequest(BaseModel):
    question_text: str
    user_answer: str
    sample_solution: str
    model_type: Optional[str] = "local"  # "openai" or "local"

class FeedbackResponse(BaseModel):
    feedback: str
    suggestions: list[str]
    strengths: list[str]
    weaknesses: list[str]
    model_used: str
    timestamp: str

# Global variables for models
openai_client = None
lightweight_ai = None  # Replaced local_model with lightweight_ai
feedback_analyzer = None

@app.on_event("startup")
async def startup_event():
    """Initialize AI models on startup"""
    logger.info("Starting GenAI Feedback Service...")
    await initialize_models()

async def initialize_models():
    """Initialize both OpenAI and lightweight local models"""
    global openai_client, lightweight_ai, feedback_analyzer
    
    # Initialize advanced feedback analyzer
    feedback_analyzer = AdvancedFeedbackAnalyzer()
    logger.info("Advanced feedback analyzer initialized")
    
    # Initialize OpenAI client if API key is available
    openai_api_key = os.getenv("OPENAI_API_KEY")
    if openai_api_key:
        try:
            openai_client = openai.OpenAI(api_key=openai_api_key)
            logger.info("OpenAI client initialized successfully")
        except Exception as e:
            logger.warning(f"Failed to initialize OpenAI client: {e}")
    else:
        logger.warning("OPENAI_API_KEY not found, OpenAI features will be disabled")
    
    # Initialize lightweight AI model (replaces GPT4All)
    try:
        lightweight_ai = LightweightAI()
        if lightweight_ai.initialize():
            logger.info("Lightweight AI model initialized successfully")
        else:
            logger.warning("Failed to initialize lightweight AI model")
            lightweight_ai = None
    except Exception as e:
        logger.warning(f"Failed to initialize lightweight AI: {e}")
        lightweight_ai = None

@app.get("/")
async def root():
    """Health check endpoint"""
    return {
        "service": "GenAI Feedback Service",
        "status": "running",
        "version": "1.0.0",
        "openai_available": openai_client is not None,
        "local_model_available": lightweight_ai is not None and lightweight_ai.is_available() if lightweight_ai else False
    }

@app.get("/health")
async def health_check():
    """Detailed health check"""
    return {
        "status": "healthy",
        "timestamp": datetime.now().isoformat(),
        "models": {
            "openai": openai_client is not None,
            "local": lightweight_ai is not None and lightweight_ai.is_available() if lightweight_ai else False
        }
    }

@app.post("/api/feedback", response_model=FeedbackResponse)
async def generate_feedback(request: FeedbackRequest):
    """
    Generate AI-powered feedback for a quiz answer
    """
    try:
        logger.info(f"Generating feedback using {request.model_type} model")
        
        if request.model_type == "openai" and openai_client:
            return await generate_openai_feedback(request)
        elif request.model_type == "local" and lightweight_ai and lightweight_ai.is_available():
            return await generate_lightweight_feedback(request)
        else:
            # Fallback to advanced analyzer
            return await generate_analyzer_feedback(request)
    
    except Exception as e:
        logger.error(f"Error generating feedback: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/feedback/advanced", response_model=FeedbackResponse)
async def generate_advanced_feedback(request: FeedbackRequest):
    """
    Generate advanced feedback using semantic similarity analysis
    """
    try:
        return await generate_analyzer_feedback(request)
        
    except Exception as e:
        logger.error(f"Error generating advanced feedback: {e}")
        raise HTTPException(status_code=500, detail=str(e))

async def generate_openai_feedback(request: FeedbackRequest) -> FeedbackResponse:
    """Generate feedback using OpenAI API"""
    
    prompt = create_feedback_prompt(
        request.question_text, 
        request.user_answer, 
        request.sample_solution
    )
    
    try:
        response = openai_client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {
                    "role": "system", 
                    "content": "You are an expert educational assistant that provides constructive feedback on student answers. Analyze the student's response compared to the sample solution and provide detailed, helpful feedback."
                },
                {"role": "user", "content": prompt}
            ],
            temperature=0.7,
            max_tokens=1000
        )
        
        feedback_text = response.choices[0].message.content
        feedback, suggestions, strengths, weaknesses = parse_feedback_response(feedback_text)
        
        return FeedbackResponse(
            feedback=feedback,
            suggestions=suggestions,
            strengths=strengths,
            weaknesses=weaknesses,
            model_used="openai-gpt-3.5-turbo",
            timestamp=datetime.now().isoformat()
        )
        
    except Exception as e:
        logger.error(f"OpenAI API error: {e}")
        raise HTTPException(status_code=500, detail=f"OpenAI API error: {str(e)}")

async def generate_lightweight_feedback(request: FeedbackRequest) -> FeedbackResponse:
    """Generate feedback using lightweight AI model"""
    
    try:
        result = lightweight_ai.generate_feedback(
            request.user_answer,
            request.sample_solution
        )
        
        return FeedbackResponse(
            feedback=result["feedback"],
            suggestions=result["suggestions"],
            strengths=result["strengths"],
            weaknesses=result["weaknesses"],
            model_used="lightweight-ai",
            timestamp=datetime.now().isoformat()
        )
        
    except Exception as e:
        logger.error(f"Lightweight AI error: {e}")
        raise HTTPException(status_code=500, detail=f"Lightweight AI error: {str(e)}")

async def generate_analyzer_feedback(request: FeedbackRequest) -> FeedbackResponse:
    """Generate feedback using the advanced analyzer"""
    
    if not feedback_analyzer:
        raise HTTPException(status_code=503, detail="Feedback analyzer not available")
    
    try:
        result = feedback_analyzer.generate_detailed_feedback(
            request.question_text,
            request.user_answer,
            request.sample_solution
        )
        
        return FeedbackResponse(
            feedback=result["feedback"],
            suggestions=result["suggestions"],
            strengths=result["strengths"],
            weaknesses=result["weaknesses"],
            model_used="advanced-analyzer",
            timestamp=datetime.now().isoformat()
        )
        
    except Exception as e:
        logger.error(f"Analyzer error: {e}")
        raise HTTPException(status_code=500, detail=f"Analyzer error: {str(e)}")

def create_feedback_prompt(question: str, user_answer: str, sample_solution: str) -> str:
    """Create a structured prompt for AI feedback generation"""
    
    return f"""
Please analyze this student's answer to a quiz question and provide detailed feedback.

**Question:** {question}

**Student's Answer:** {user_answer}

**Sample Solution:** {sample_solution}

Please provide your analysis in the following format:

**FEEDBACK:** [2-3 sentences of overall feedback]

**STRENGTHS:** [List 2-3 things the student did well, separated by |]

**WEAKNESSES:** [List 2-3 areas for improvement, separated by |]

**SUGGESTIONS:** [List 2-3 specific suggestions for improvement, separated by |]

Focus on:
1. Semantic similarity between the answers
2. Key concepts covered or missed
3. Clarity and completeness
4. Technical accuracy
5. Constructive guidance for improvement
"""

def parse_feedback_response(response_text: str) -> tuple:
    """Parse the AI response into structured components"""
    
    try:
        lines = response_text.strip().split('\n')
        
        feedback = "Feedback generated successfully."
        strengths = ["Answer provided"]
        weaknesses = ["Could be more detailed"]
        suggestions = ["Review the topic further"]
        
        for line in lines:
            line = line.strip()
            if line.startswith("**FEEDBACK:**"):
                feedback = line.replace("**FEEDBACK:**", "").strip()
            elif line.startswith("**STRENGTHS:**"):
                strengths_text = line.replace("**STRENGTHS:**", "").strip()
                strengths = [s.strip() for s in strengths_text.split("|") if s.strip()]
            elif line.startswith("**WEAKNESSES:**"):
                weaknesses_text = line.replace("**WEAKNESSES:**", "").strip()
                weaknesses = [w.strip() for w in weaknesses_text.split("|") if w.strip()]
            elif line.startswith("**SUGGESTIONS:**"):
                suggestions_text = line.replace("**SUGGESTIONS:**", "").strip()
                suggestions = [s.strip() for s in suggestions_text.split("|") if s.strip()]
        
        return feedback, suggestions, strengths, weaknesses
        
    except Exception as e:
        logger.error(f"Error parsing feedback response: {e}")
        # Return safe defaults
        return "Feedback generated successfully.", ["Answer provided"], ["Could be more detailed"], ["Review the topic further"]

@app.get("/api/models")
async def get_available_models():
    """Get information about available AI models"""
    return {
        "models": {
            "openai": {
                "available": openai_client is not None,
                "description": "OpenAI GPT-3.5-turbo model for high-quality feedback",
                "size": "Cloud-based",
                "speed": "2-5 seconds"
            },
            "local": {
                "available": lightweight_ai is not None and lightweight_ai.is_available() if lightweight_ai else False,
                "description": "Lightweight local AI with sentence transformers",
                "size": "~80MB", 
                "speed": "1-2 seconds"
            },
            "advanced": {
                "available": feedback_analyzer is not None,
                "description": "Advanced semantic similarity analyzer",
                "size": "~80MB",
                "speed": "<1 second"
            }
        },
        "default_model": "local" if lightweight_ai and lightweight_ai.is_available() else "advanced"
    }

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8084,
        reload=True,
        log_level="info"
    )
