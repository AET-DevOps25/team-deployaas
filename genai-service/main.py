import os
import json
import requests
from typing import Dict, Any, List, Optional
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
import datetime
import uvicorn
from semantic_analyzer import get_semantic_analyzer
from prometheus_fastapi_instrumentator import Instrumentator

# Environment configuration
WEBUI_API_KEY = os.getenv("WEBUI_API_KEY")
API_URL = "https://gpu.aet.cit.tum.de/api/chat/completions"

# Create FastAPI application instance
app = FastAPI(
    title="GenAI Feedback Service",
    description="""
    ## AI-Powered Learning Assistant
    
    This service provides intelligent feedback generation for educational content using state-of-the-art 
    language models. It supports multiple feedback types and semantic analysis.
    
    ### Features
    - **Basic Feedback**: Standard AI-generated feedback comparing answers
    - **Advanced Feedback**: Detailed analysis with strengths, weaknesses, and suggestions
    - **Semantic Analysis**: Similarity-based feedback using embeddings
    - **Multiple Models**: Support for local LLM and OpenAI models
    
    ### Authentication
    No authentication required for this service.
    
    ### Rate Limits
    Please be mindful of API usage to ensure fair access for all users.
    """,
    version="1.0.0",
    contact={
        "name": "Study Assistant Team",
        "email": "support@studyassistant.com",
    },
    license_info={
        "name": "MIT",
        "url": "https://opensource.org/licenses/MIT",
    },
)

Instrumentator().instrument(app).expose(app)


class FeedbackRequest(BaseModel):
    """
    Request schema for feedback endpoint.
    """
    user_answer: str = Field(
        ..., 
        description="Student's submitted answer",
        example="DevOps is a practice that combines development and operations teams to improve collaboration."
    )
    sample_solution: str = Field(
        ..., 
        description="Correct/sample solution for comparison",
        example="DevOps is a set of practices that combines software development (Dev) and IT operations (Ops) to shorten the development lifecycle and provide continuous delivery with high software quality."
    )
    question_text: str = Field(
        ..., 
        description="Original question text for context",
        example="What is DevOps and why is it important in modern software development?"
    )
    model_type: str = Field(
        default="local", 
        description="Model type to use for feedback generation",
        example="local",
        pattern="^(local|openai)$"
    )


class AdvancedFeedbackRequest(BaseModel):
    """
    Request schema for advanced feedback endpoint.

    Attributes:
        user_answer (str): The student's submitted answer
        sample_solution (str): The correct/sample solution
        question_text (str): The original question text for context
    """
    user_answer: str = Field(..., description="Student's submitted answer")
    sample_solution: str = Field(..., description="Correct/sample solution")
    question_text: str = Field(..., description="Original question text")


class SemanticFeedbackRequest(BaseModel):
    """
    Request schema for semantic feedback endpoint.

    Attributes:
        user_answer (str): The student's submitted answer
        sample_solution (str): The correct/sample solution
        question_text (str): The original question text for context
    """
    user_answer: str = Field(..., description="Student's submitted answer")
    sample_solution: str = Field(..., description="Correct/sample solution")
    question_text: str = Field(..., description="Original question text")


class FeedbackResponse(BaseModel):
    """
    Response schema for feedback endpoints.
    """
    feedback: str = Field(
        ..., 
        description="Main feedback text providing detailed analysis",
        example="Your answer demonstrates a good understanding of DevOps fundamentals. You correctly identified that DevOps combines development and operations teams..."
    )
    strengths: List[str] = Field(
        default_factory=list, 
        description="List of identified strengths in the answer",
        example=["Clear understanding of DevOps concept", "Mentioned team collaboration"]
    )
    weaknesses: List[str] = Field(
        default_factory=list, 
        description="List of identified weaknesses or areas for improvement",
        example=["Could mention continuous delivery", "Missing discussion of software quality benefits"]
    )
    suggestions: List[str] = Field(
        default_factory=list, 
        description="List of specific improvement suggestions",
        example=["Consider discussing the continuous integration/continuous deployment (CI/CD) pipeline", "Mention how DevOps improves software quality and deployment frequency"]
    )
    score: float = Field(
        ..., 
        description="Numerical score from 0-100 based on answer quality",
        example=78.5,
        ge=0,
        le=100
    )
    model_used: str = Field(
        ..., 
        description="Model that generated the feedback",
        example="llama3.3:latest"
    )
    timestamp: str = Field(
        ..., 
        description="ISO timestamp when feedback was generated",
        example="2024-01-15T10:30:00Z"
    )


def call_openwebui_api(prompt: str, model_name: str = "llama3.3:latest") -> str:
    """
    Direct API call to Open WebUI without LangChain.
    
    Args:
        prompt: The input prompt to send to the model
        model_name: The model to use
        
    Returns:
        The generated response text
        
    Raises:
        Exception: If API call fails
    """
    if not WEBUI_API_KEY:
        raise ValueError("WEBUI_API_KEY environment variable is required")
    
    headers = {
        "Authorization": f"Bearer {WEBUI_API_KEY}",
        "Content-Type": "application/json",
    }
    
    # Build messages for chat completion
    messages = [
        {"role": "user", "content": prompt}
    ]
    
    payload = {
        "model": model_name,
        "messages": messages,
    }
    
    try:
        print(f"DEBUG: About to make request")
        print(f"DEBUG: Request URL: {API_URL}")
        print(f"DEBUG: Model name: {model_name}")
        print(f"DEBUG: API key length: {len(WEBUI_API_KEY) if WEBUI_API_KEY else 0}")
        print(f"DEBUG: Prompt length: {len(prompt)}")
        
        response = requests.post(
            API_URL,
            headers=headers,
            json=payload,
            timeout=60
        )
        
        print(f"DEBUG: Response status: {response.status_code}")
        print(f"DEBUG: Response headers: {dict(response.headers)}")
        print(f"DEBUG: Response text: {response.text[:500]}...")
        
        response.raise_for_status()
        
        result = response.json()
        
        # Extract the response content
        if "choices" in result and len(result["choices"]) > 0:
            content = result["choices"][0]["message"]["content"]
            return content.strip()
        else:
            raise ValueError("Unexpected response format from API")
            
    except requests.RequestException as e:
        print(f"DEBUG: Request exception: {str(e)}")
        raise Exception(f"API request failed: {str(e)}")
    except (KeyError, IndexError, ValueError) as e:
        print(f"DEBUG: Response parsing error: {str(e)}")
        raise Exception(f"Failed to parse API response: {str(e)}")


def create_feedback_prompt(question_text: str, sample_solution: str, user_answer: str) -> str:
    """Create a brief feedback prompt."""
    return f"""You are an educational AI assistant. Provide brief, concise feedback on this student answer.

QUESTION: {question_text}
SAMPLE SOLUTION: {sample_solution}
STUDENT'S ANSWER: {user_answer}

Provide feedback in 2-3 sentences maximum. Include:
1. How good the answer is (poor/fair/good/excellent)
2. Key areas for improvement (if any)

Be direct and constructive. No introductory phrases or boilerplate text. Also, do not mention the sample solution explicitly.

FEEDBACK:"""


def create_advanced_feedback_prompt(question_text: str, sample_solution: str, user_answer: str) -> str:
    """Create an advanced feedback prompt with structured output."""
    return f"""You are an expert educational AI assistant that provides detailed, structured feedback on student answers.

QUESTION: {question_text}

SAMPLE SOLUTION: {sample_solution}

STUDENT'S ANSWER: {user_answer}

Please provide a comprehensive analysis of the student's answer. Structure your response as follows:

MAIN FEEDBACK: [Provide an overall assessment of the answer quality and understanding]

STRENGTHS: [List 2-4 specific strengths you identified in the answer. Be specific about what the student did well]

WEAKNESSES: [List 2-4 specific areas where the answer could be improved or where understanding seems lacking]

SUGGESTIONS: [List 2-4 specific, actionable suggestions for how the student can improve their understanding or answer]

SCORE: [Provide a numerical score from 0-100 based on correctness, completeness, and understanding]

Format your response exactly as shown above with the section headers in ALL CAPS followed by a colon.

RESPONSE:"""


def parse_advanced_feedback(feedback_text: str) -> Dict[str, Any]:
    """Parse the structured advanced feedback response."""
    result = {
        "main_feedback": "",
        "strengths": [],
        "weaknesses": [],
        "suggestions": [],
        "score": 75.0
    }
    
    lines = feedback_text.split('\n')
    current_section = "main_feedback"
    
    for line in lines:
        line = line.strip()
        if not line:
            continue
            
        if line.startswith("MAIN FEEDBACK:"):
            current_section = "main_feedback"
            content = line.replace("MAIN FEEDBACK:", "").strip()
            if content:
                result["main_feedback"] = content
        elif line.startswith("STRENGTHS:"):
            current_section = "strengths"
            content = line.replace("STRENGTHS:", "").strip()
            if content:
                result["strengths"].append(content)
        elif line.startswith("WEAKNESSES:"):
            current_section = "weaknesses"
            content = line.replace("WEAKNESSES:", "").strip()
            if content:
                result["weaknesses"].append(content)
        elif line.startswith("SUGGESTIONS:"):
            current_section = "suggestions"
            content = line.replace("SUGGESTIONS:", "").strip()
            if content:
                result["suggestions"].append(content)
        elif line.startswith("SCORE:"):
            try:
                score_text = line.replace("SCORE:", "").strip()
                # Extract numerical score
                import re
                score_match = re.search(r'(\d+(?:\.\d+)?)', score_text)
                if score_match:
                    result["score"] = float(score_match.group(1))
            except:
                pass
        else:
            # Content line for current section
            if current_section == "main_feedback" and line:
                if result["main_feedback"]:
                    result["main_feedback"] += " " + line
                else:
                    result["main_feedback"] = line
            elif current_section in ["strengths", "weaknesses", "suggestions"] and line:
                # Remove bullet points and add to appropriate list
                clean_line = line.lstrip("•-*").strip()
                if clean_line:
                    result[current_section].append(clean_line)
    
    return result


@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {"status": "healthy", "service": "GenAI Feedback Service"}


@app.post(
    "/feedback",
    response_model=FeedbackResponse,
    summary="Generate basic AI feedback",
    description="Generates basic AI feedback comparing student answer to sample solution"
)
async def generate_feedback(req: FeedbackRequest) -> FeedbackResponse:
    """
    Generate AI feedback for a student's answer.
    
    Args:
        req: Request containing the question, sample solution, and user answer
        
    Returns:
        FeedbackResponse containing structured feedback
        
    Raises:
        HTTPException: If the API call fails or other errors occur
    """
    try:
        if not req.user_answer.strip():
            raise HTTPException(
                status_code=400, 
                detail="user_answer cannot be empty"
            )
        
        if not req.sample_solution.strip():
            raise HTTPException(
                status_code=400, 
                detail="sample_solution cannot be empty"
            )
        
        # Generate feedback using direct API call
        prompt = create_feedback_prompt(req.question_text, req.sample_solution, req.user_answer)
        feedback_text = call_openwebui_api(prompt)
        
        # For basic feedback, provide a simple response
        return FeedbackResponse(
            feedback=feedback_text,
            strengths=[],
            weaknesses=[],
            suggestions=[],
            score=75.0,  # Default score for basic feedback
            model_used="llama3.3:latest (basic)",
            timestamp=datetime.datetime.now().isoformat()
        )
        
    except HTTPException:
        raise
    except Exception as e:
        print(f"Error generating feedback: {str(e)}")
        
        # Fallback response in case of error
        return FeedbackResponse(
            feedback="AI feedback service is temporarily unavailable. The system will still accept your answer for review.",
            strengths=[],
            weaknesses=[],
            suggestions=[],
            score=0.0,
            model_used="fallback",
            timestamp=datetime.datetime.now().isoformat()
        )


@app.post(
    "/feedback/advanced",
    response_model=FeedbackResponse,
    summary="Generate advanced AI feedback",
    description="Generates detailed AI feedback with structured analysis"
)
async def generate_advanced_feedback(req: AdvancedFeedbackRequest) -> FeedbackResponse:
    """
    Generate advanced AI feedback with detailed analysis.
    
    Args:
        req: Request containing the question, sample solution, and user answer
        
    Returns:
        FeedbackResponse containing detailed structured feedback
        
    Raises:
        HTTPException: If the API call fails or other errors occur
    """
    try:
        if not req.user_answer.strip():
            raise HTTPException(
                status_code=400, 
                detail="user_answer cannot be empty"
            )
        
        if not req.sample_solution.strip():
            raise HTTPException(
                status_code=400, 
                detail="sample_solution cannot be empty"
            )
        
        # Generate advanced feedback using direct API call
        prompt = create_advanced_feedback_prompt(req.question_text, req.sample_solution, req.user_answer)
        feedback_text = call_openwebui_api(prompt)
        
        # Parse the structured response
        parsed_feedback = parse_advanced_feedback(feedback_text)
        
        return FeedbackResponse(
            feedback=parsed_feedback.get("main_feedback", "Feedback generated successfully"),
            strengths=parsed_feedback.get("strengths", []),
            weaknesses=parsed_feedback.get("weaknesses", []),
            suggestions=parsed_feedback.get("suggestions", []),
            score=parsed_feedback.get("score", 75.0),
            model_used="llama3.3:latest (advanced)",
            timestamp=datetime.datetime.now().isoformat()
        )
        
    except HTTPException:
        raise
    except Exception as e:
        print(f"Error generating advanced feedback: {str(e)}")
        
        # Fallback response in case of error
        return FeedbackResponse(
            feedback="Advanced AI feedback service is temporarily unavailable.",
            strengths=[],
            weaknesses=[],
            suggestions=[],
            score=0.0,
            model_used="fallback",
            timestamp=datetime.datetime.now().isoformat()
        )


@app.post(
    "/feedback/semantic",
    response_model=FeedbackResponse,
    summary="Generate semantic similarity feedback",
    description="Generates feedback based on semantic similarity analysis without using generative AI"
)
async def generate_semantic_feedback(req: SemanticFeedbackRequest) -> FeedbackResponse:
    """
    Generate semantic similarity feedback without using generative AI.
    
    Args:
        req: Request containing the question, sample solution, and user answer
        
    Returns:
        FeedbackResponse containing semantic similarity analysis
        
    Raises:
        HTTPException: If the analysis fails or required fields are missing
    """
    try:
        if not req.user_answer.strip():
            raise HTTPException(
                status_code=400, 
                detail="user_answer cannot be empty"
            )
        
        if not req.sample_solution.strip():
            raise HTTPException(
                status_code=400, 
                detail="sample_solution cannot be empty"
            )
        
        # Get semantic analyzer instance
        analyzer = get_semantic_analyzer()
        
        # Generate semantic feedback
        semantic_result = analyzer.generate_semantic_feedback(
            req.question_text, 
            req.user_answer, 
            req.sample_solution
        )
        
        return FeedbackResponse(
            feedback=semantic_result.get("feedback", "Semantic analysis completed"),
            strengths=semantic_result.get("strengths", []),
            weaknesses=semantic_result.get("weaknesses", []),
            suggestions=semantic_result.get("suggestions", []),
            score=semantic_result.get("score", 50.0),
            model_used=semantic_result.get("model_used", "semantic_analyzer"),
            timestamp=datetime.datetime.now().isoformat()
        )
        
    except HTTPException:
        raise
    except Exception as e:
        print(f"Error generating semantic feedback: {str(e)}")
        
        # Fallback response in case of error
        return FeedbackResponse(
            feedback=f"Semantic analysis service encountered an error: {str(e)}",
            strengths=["Provided an answer"],
            weaknesses=["Analysis unavailable"],
            suggestions=["Please try again later"],
            score=50.0,
            model_used="semantic_analyzer_fallback",
            timestamp=datetime.datetime.now().isoformat()
        )


@app.get("/")
async def root():
    """Root endpoint with service information."""
    return {
        "service": "GenAI Feedback Service",
        "version": "1.0.0",
        "description": "Generates AI-powered feedback for quiz answers using Open WebUI",
        "endpoints": {
            "health": "/health",
            "feedback": "/feedback",
            "advanced_feedback": "/feedback/advanced",
            "semantic_feedback": "/feedback/semantic",
            "docs": "/docs"
        }
    }


# Entry point for direct execution
if __name__ == "__main__":
    """
    Entry point for `python main.py` invocation.
    Starts Uvicorn server serving this FastAPI app.

    Honors PORT environment variable (default: 5000).
    Reload=True enables live-reload during development.
    """
    import uvicorn

    port = int(os.getenv("PORT", 5000))
    
    print(f"Starting GenAI Feedback Service on port {port}")
    print(f"API Documentation available at: http://localhost:{port}/docs")
    
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=port,
        reload=True
    )
