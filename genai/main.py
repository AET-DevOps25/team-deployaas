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
from feedback_analyzer import SemanticAnalyzer
from local_llm_real import RealLocalLLM

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
local_llm = None  # Real Local LLM using transformers for generative AI
semantic_analyzer = None

@app.on_event("startup")
async def startup_event():
    """Initialize AI models on startup"""
    logger.info("Starting GenAI Feedback Service...")
    await initialize_models()

async def initialize_models():
    """Initialize OpenAI, Local LLM, and semantic analyzer models"""
    global openai_client, local_llm, semantic_analyzer
    
    # Initialize semantic analyzer
    semantic_analyzer = SemanticAnalyzer()
    logger.info("Semantic analyzer initialized")
    
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
    
    # Initialize Real Local LLM (using transformers for actual text generation)
    try:
        local_llm = RealLocalLLM()
        if local_llm.initialize():
            logger.info("Real Local LLM initialized successfully")
        else:
            logger.warning("Failed to initialize Real Local LLM")
            local_llm = None
    except Exception as e:
        logger.warning(f"Failed to initialize Real Local LLM: {e}")
        local_llm = None

@app.get("/")
async def root():
    """Health check endpoint"""
    return {
        "service": "GenAI Feedback Service",
        "status": "running",
        "version": "1.0.0",
        "openai_available": openai_client is not None,
        "local_llm_available": local_llm is not None and local_llm.is_available() if local_llm else False,
        "semantic_analyzer_available": semantic_analyzer is not None
    }

@app.get("/health")
async def health_check():
    """Detailed health check"""
    return {
        "status": "healthy",
        "timestamp": datetime.now().isoformat(),
        "models": {
            "openai": openai_client is not None,
            "local_llm": local_llm is not None and local_llm.is_available() if local_llm else False,
            "semantic_analyzer": semantic_analyzer is not None
        }
    }

@app.post("/api/feedback", response_model=FeedbackResponse)
async def generate_feedback(request: FeedbackRequest):
    """
    Generate AI-powered feedback for a quiz answer
    Priority: OpenAI → Real Local LLM → "Local LLM unavailable" 
    """
    try:
        logger.info(f"Generating feedback using {request.model_type} model")
        
        if request.model_type == "openai" and openai_client:
            return await generate_openai_feedback(request)
        elif request.model_type == "local":
            # Always try real LLM for local model type (even if answers are nonsensical)
            if local_llm and local_llm.is_available():
                return await generate_local_llm_feedback(request)
            else:
                # Return simple unavailable message if LLM is not available
                return FeedbackResponse(
                    feedback="Local LLM unavailable.",
                    suggestions=["Try again later", "Use semantic analysis for basic comparison"],
                    strengths=[],
                    weaknesses=[],
                    model_used="unavailable",
                    timestamp=datetime.now().isoformat()
                )
        else:
            # Default fallback
            return FeedbackResponse(
                feedback="Invalid model type specified.",
                suggestions=["Use 'local' or 'openai' model type"],
                strengths=[],
                weaknesses=[],
                model_used="unavailable",
                timestamp=datetime.now().isoformat()
            )
    
    except Exception as e:
        logger.error(f"Error generating feedback: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/feedback/advanced", response_model=FeedbackResponse)
async def generate_advanced_feedback(request: FeedbackRequest):
    """
    Legacy endpoint - redirects to semantic analysis for backward compatibility
    """
    logger.info("Legacy /api/feedback/advanced called, redirecting to semantic analysis")
    return await generate_semantic_analysis(request)

@app.post("/api/semantic", response_model=FeedbackResponse)
async def generate_semantic_analysis(request: FeedbackRequest):
    """
    Generate semantic similarity analysis (pure similarity comparison)
    """
    try:
        return await generate_semantic_feedback(request)
        
    except Exception as e:
        logger.error(f"Error generating semantic analysis: {e}")
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

async def generate_local_llm_feedback(request: FeedbackRequest) -> FeedbackResponse:
    """Generate feedback using Real Local LLM (transformers-based generative AI)"""
    
    try:
        result = local_llm.generate_feedback(
            user_answer=request.user_answer,
            sample_solution=request.sample_solution,
            question_text=request.question_text
        )
        
        return FeedbackResponse(
            feedback=result["feedback"],
            suggestions=result["suggestions"],
            strengths=result["strengths"],
            weaknesses=result["weaknesses"],
            model_used=result["model_used"],
            timestamp=datetime.now().isoformat()
        )
        
    except Exception as e:
        logger.error(f"Local LLM error: {e}")
        raise HTTPException(status_code=500, detail=f"Local LLM error: {str(e)}")

async def generate_semantic_feedback(request: FeedbackRequest) -> FeedbackResponse:
    """Generate feedback using semantic similarity analysis"""
    
    if not semantic_analyzer:
        raise HTTPException(status_code=503, detail="Semantic analyzer not available")
    
    try:
        result = semantic_analyzer.generate_semantic_feedback(
            request.question_text,
            request.user_answer,
            request.sample_solution
        )
        
        return FeedbackResponse(
            feedback=result["feedback"],
            suggestions=result["suggestions"],
            strengths=result["strengths"],
            weaknesses=result["weaknesses"],
            model_used="semantic-analyzer",
            timestamp=datetime.now().isoformat()
        )
        
    except Exception as e:
        logger.error(f"Semantic analyzer error: {e}")
        raise HTTPException(status_code=500, detail=f"Semantic analyzer error: {str(e)}")

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
                "speed": "2-5 seconds",
                "endpoint": "/api/feedback"
            },
            "local": {
                "available": local_llm is not None and local_llm.is_available() if local_llm else False,
                "description": "Real Local LLM using HuggingFace transformers for generative AI feedback",
                "size": "~500MB", 
                "speed": "5-10 seconds",
                "endpoint": "/api/feedback"
            },
            "semantic": {
                "available": semantic_analyzer is not None,
                "description": "Semantic similarity analyzer for concept comparison",
                "size": "~80MB",
                "speed": "<1 second",
                "endpoint": "/api/semantic"
            }
        },
        "default_model": "local" if local_llm and local_llm.is_available() else "semantic",
        "endpoints": {
            "/api/feedback": "AI-powered feedback (OpenAI → Local LLM → unavailable message)",
            "/api/semantic": "Pure semantic similarity analysis"
        }
    }

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8084,
        reload=True,
        log_level="info"
    )
