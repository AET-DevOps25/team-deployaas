import pytest
import asyncio
from unittest.mock import Mock, patch
from fastapi.testclient import TestClient
import sys
import os

# Add the parent directory to the path so we can import the modules
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from main import app
from semantic_analyzer import SemanticAnalyzer


@pytest.fixture(scope="session")
def event_loop():
    """Create an instance of the default event loop for the test session."""
    loop = asyncio.get_event_loop_policy().new_event_loop()
    yield loop
    loop.close()


@pytest.fixture
def client():
    """Create a test client for the FastAPI app."""
    return TestClient(app)


@pytest.fixture
def mock_webui_api_key():
    """Mock the WebUI API key environment variable."""
    with patch.dict(os.environ, {"WEBUI_API_KEY": "test_api_key"}):
        yield "test_api_key"


@pytest.fixture
def sample_feedback_request():
    """Sample feedback request data."""
    return {
        "user_answer": "DevOps is a culture that combines development and operations teams.",
        "sample_solution": "DevOps is a set of practices that combines software development and IT operations to shorten the development lifecycle.",
        "question_text": "What is DevOps?"
    }


@pytest.fixture
def sample_advanced_feedback_request():
    """Sample advanced feedback request data."""
    return {
        "user_answer": "CI/CD stands for Continuous Integration and Continuous Deployment. It helps automate the software development process.",
        "sample_solution": "CI/CD stands for Continuous Integration and Continuous Deployment. It is a method to frequently deliver apps to customers by introducing automation into the stages of app development.",
        "question_text": "Explain CI/CD and its benefits."
    }


@pytest.fixture
def sample_semantic_feedback_request():
    """Sample semantic feedback request data."""
    return {
        "user_answer": "Kubernetes is a container orchestration platform.",
        "sample_solution": "Kubernetes is an open-source container orchestration system for automating application deployment, scaling, and management.",
        "question_text": "What is Kubernetes?"
    }


@pytest.fixture
def mock_openwebui_response():
    """Mock response from OpenWebUI API."""
    return {
        "choices": [
            {
                "message": {
                    "content": "Good answer! You correctly identified the main concept. Consider adding more details about automation and benefits."
                }
            }
        ]
    }


@pytest.fixture
def mock_advanced_openwebui_response():
    """Mock advanced response from OpenWebUI API."""
    return {
        "choices": [
            {
                "message": {
                    "content": """MAIN FEEDBACK: Your answer demonstrates a solid understanding of CI/CD concepts and correctly identifies the core components.

STRENGTHS: 
- Correctly defined CI/CD acronym
- Mentioned automation aspect
- Clear and concise explanation

WEAKNESSES:
- Missing details about specific benefits
- Could elaborate on deployment strategies
- No mention of testing automation

SUGGESTIONS:
- Add information about automated testing
- Explain the benefits of faster feedback loops
- Mention deployment strategies like blue-green or rolling deployments

SCORE: 75"""
                }
            }
        ]
    }


@pytest.fixture
def mock_semantic_analyzer():
    """Mock semantic analyzer."""
    mock_analyzer = Mock(spec=SemanticAnalyzer)
    mock_analyzer.generate_semantic_feedback.return_value = {
        "feedback": "Your answer shows good understanding with 85% semantic similarity to the sample solution.",
        "strengths": ["Correctly identified container orchestration", "Mentioned platform concept"],
        "weaknesses": ["Could include more details about automation", "Missing scaling and management aspects"],
        "suggestions": ["Add information about automated scaling", "Mention management capabilities"],
        "score": 85.0,
        "model_used": "semantic_analyzer"
    }
    return mock_analyzer


@pytest.fixture
def mock_requests_post():
    """Mock requests.post for API calls."""
    mock_response = Mock()
    mock_response.status_code = 200
    mock_response.raise_for_status.return_value = None
    
    with patch('requests.post', return_value=mock_response) as mock:
        yield mock


@pytest.fixture
def empty_request_data():
    """Empty request data for testing validation."""
    return {
        "user_answer": "",
        "sample_solution": "",
        "question_text": ""
    }


@pytest.fixture
def invalid_request_data():
    """Invalid request data for testing validation."""
    return {
        "user_answer": "   ",  # Only whitespace
        "sample_solution": "Valid solution",
        "question_text": "Valid question"
    }
