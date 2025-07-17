import pytest
from unittest.mock import Mock, patch, MagicMock
import json
from fastapi import HTTPException

from main import app


class TestHealthEndpoint:
    """Test the health check endpoint."""

    def test_health_check(self, client):
        """Test health check endpoint returns correct response."""
        response = client.get("/health")
        
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "healthy"
        assert data["service"] == "GenAI Feedback Service"


class TestRootEndpoint:
    """Test the root endpoint."""

    def test_root_endpoint(self, client):
        """Test root endpoint returns service information."""
        response = client.get("/")
        
        assert response.status_code == 200
        data = response.json()
        assert data["service"] == "GenAI Feedback Service"
        assert data["version"] == "1.0.0"
        assert "endpoints" in data
        assert "health" in data["endpoints"]
        assert "feedback" in data["endpoints"]


class TestBasicFeedbackEndpoint:
    """Test the basic feedback endpoint."""

    @patch('main.call_openwebui_api')
    def test_feedback_endpoint_success(self, mock_api_call, client, sample_feedback_request):
        """Test successful feedback generation."""
        mock_api_call.return_value = "Good answer! Consider adding more details."
        
        response = client.post("/feedback", json=sample_feedback_request)
        
        assert response.status_code == 200
        data = response.json()
        assert data["feedback"] == "Good answer! Consider adding more details."
        assert data["score"] == 75.0
        assert data["model_used"] == "llama3.3:latest (basic)"
        assert "timestamp" in data

    def test_feedback_endpoint_empty_user_answer(self, client):
        """Test feedback endpoint with empty user answer."""
        request_data = {
            "user_answer": "",
            "sample_solution": "Valid solution",
            "question_text": "Valid question",
            "model_type": "local"
        }
        
        response = client.post("/feedback", json=request_data)
        
        assert response.status_code == 400
        assert "user_answer cannot be empty" in response.json()["detail"]

    def test_feedback_endpoint_whitespace_user_answer(self, client):
        """Test feedback endpoint with whitespace-only user answer."""
        request_data = {
            "user_answer": "   ",
            "sample_solution": "Valid solution",
            "question_text": "Valid question",
            "model_type": "local"
        }
        
        response = client.post("/feedback", json=request_data)
        
        assert response.status_code == 400
        assert "user_answer cannot be empty" in response.json()["detail"]

    def test_feedback_endpoint_empty_sample_solution(self, client):
        """Test feedback endpoint with empty sample solution."""
        request_data = {
            "user_answer": "Valid answer",
            "sample_solution": "",
            "question_text": "Valid question",
            "model_type": "local"
        }
        
        response = client.post("/feedback", json=request_data)
        
        assert response.status_code == 400
        assert "sample_solution cannot be empty" in response.json()["detail"]

    @patch('main.call_openwebui_api')
    def test_feedback_endpoint_api_error_fallback(self, mock_api_call, client, sample_feedback_request):
        """Test feedback endpoint with API error returns fallback response."""
        mock_api_call.side_effect = Exception("API Error")
        
        response = client.post("/feedback", json=sample_feedback_request)
        
        assert response.status_code == 200
        data = response.json()
        assert "temporarily unavailable" in data["feedback"]
        assert data["score"] == 0.0
        assert data["model_used"] == "fallback"

    def test_feedback_endpoint_missing_required_fields(self, client):
        """Test feedback endpoint with missing required fields."""
        request_data = {
            "user_answer": "Valid answer"
            # Missing required fields
        }
        
        response = client.post("/feedback", json=request_data)
        
        assert response.status_code == 422  # Validation error

    def test_feedback_endpoint_invalid_json(self, client):
        """Test feedback endpoint with invalid JSON."""
        response = client.post(
            "/feedback",
            data="invalid json",
            headers={"Content-Type": "application/json"}
        )
        
        assert response.status_code == 422

    @patch('main.call_openwebui_api')
    def test_feedback_endpoint_different_model_types(self, mock_api_call, client):
        """Test feedback endpoint with different model types."""
        mock_api_call.return_value = "Test response"
        
        test_cases = ["local", "openai", "custom"]
        
        for model_type in test_cases:
            request_data = {
                "user_answer": "Test answer",
                "sample_solution": "Test solution",
                "question_text": "Test question",
                "model_type": model_type
            }
            
            response = client.post("/feedback", json=request_data)
            
            assert response.status_code == 200
            data = response.json()
            assert data["feedback"] == "Test response"


class TestAdvancedFeedbackEndpoint:
    """Test the advanced feedback endpoint."""

    @patch('main.call_openwebui_api')
    def test_advanced_feedback_endpoint_success(self, mock_api_call, client, sample_advanced_feedback_request, mock_advanced_openwebui_response):
        """Test successful advanced feedback generation."""
        mock_api_call.return_value = mock_advanced_openwebui_response["choices"][0]["message"]["content"]
        
        response = client.post("/feedback/advanced", json=sample_advanced_feedback_request)
        
        assert response.status_code == 200
        data = response.json()
        assert "solid understanding" in data["feedback"]
        assert len(data["strengths"]) > 0
        assert len(data["weaknesses"]) > 0
        assert len(data["suggestions"]) > 0
        assert data["score"] == 75.0
        assert data["model_used"] == "llama3.3:latest (advanced)"

    def test_advanced_feedback_endpoint_empty_user_answer(self, client):
        """Test advanced feedback endpoint with empty user answer."""
        request_data = {
            "user_answer": "",
            "sample_solution": "Valid solution",
            "question_text": "Valid question"
        }
        
        response = client.post("/feedback/advanced", json=request_data)
        
        assert response.status_code == 400
        assert "user_answer cannot be empty" in response.json()["detail"]

    def test_advanced_feedback_endpoint_empty_sample_solution(self, client):
        """Test advanced feedback endpoint with empty sample solution."""
        request_data = {
            "user_answer": "Valid answer",
            "sample_solution": "",
            "question_text": "Valid question"
        }
        
        response = client.post("/feedback/advanced", json=request_data)
        
        assert response.status_code == 400
        assert "sample_solution cannot be empty" in response.json()["detail"]

    @patch('main.call_openwebui_api')
    def test_advanced_feedback_endpoint_api_error_fallback(self, mock_api_call, client, sample_advanced_feedback_request):
        """Test advanced feedback endpoint with API error returns fallback response."""
        mock_api_call.side_effect = Exception("API Error")
        
        response = client.post("/feedback/advanced", json=sample_advanced_feedback_request)
        
        assert response.status_code == 200
        data = response.json()
        assert "temporarily unavailable" in data["feedback"]
        assert data["score"] == 0.0
        assert data["model_used"] == "fallback"

    @patch('main.call_openwebui_api')
    def test_advanced_feedback_endpoint_malformed_response(self, mock_api_call, client, sample_advanced_feedback_request):
        """Test advanced feedback endpoint with malformed API response."""
        mock_api_call.return_value = "This is not properly formatted feedback without sections."
        
        response = client.post("/feedback/advanced", json=sample_advanced_feedback_request)
        
        assert response.status_code == 200
        data = response.json()
        assert data["model_used"] == "llama3.3:latest (advanced)"


class TestSemanticFeedbackEndpoint:
    """Test the semantic feedback endpoint."""

    @patch('main.get_semantic_analyzer')
    def test_semantic_feedback_endpoint_success(self, mock_get_analyzer, client, sample_semantic_feedback_request, mock_semantic_analyzer):
        """Test successful semantic feedback generation."""
        mock_get_analyzer.return_value = mock_semantic_analyzer
        
        response = client.post("/feedback/semantic", json=sample_semantic_feedback_request)
        
        assert response.status_code == 200
        data = response.json()
        assert "85% semantic similarity" in data["feedback"]
        assert len(data["strengths"]) == 2
        assert len(data["weaknesses"]) == 2
        assert len(data["suggestions"]) == 2
        assert data["score"] == 85.0
        assert data["model_used"] == "semantic_analyzer"

    def test_semantic_feedback_endpoint_empty_user_answer(self, client):
        """Test semantic feedback endpoint with empty user answer."""
        request_data = {
            "user_answer": "",
            "sample_solution": "Valid solution",
            "question_text": "Valid question"
        }
        
        response = client.post("/feedback/semantic", json=request_data)
        
        assert response.status_code == 400
        assert "user_answer cannot be empty" in response.json()["detail"]

    def test_semantic_feedback_endpoint_empty_sample_solution(self, client):
        """Test semantic feedback endpoint with empty sample solution."""
        request_data = {
            "user_answer": "Valid answer",
            "sample_solution": "",
            "question_text": "Valid question"
        }
        
        response = client.post("/feedback/semantic", json=request_data)
        
        assert response.status_code == 400
        assert "sample_solution cannot be empty" in response.json()["detail"]

    @patch('main.get_semantic_analyzer')
    def test_semantic_feedback_endpoint_analyzer_error(self, mock_get_analyzer, client, sample_semantic_feedback_request):
        """Test semantic feedback endpoint with analyzer error returns fallback response."""
        mock_analyzer = Mock()
        mock_analyzer.generate_semantic_feedback.side_effect = Exception("Analyzer Error")
        mock_get_analyzer.return_value = mock_analyzer
        
        response = client.post("/feedback/semantic", json=sample_semantic_feedback_request)
        
        assert response.status_code == 200
        data = response.json()
        assert "encountered an error" in data["feedback"]
        assert data["score"] == 50.0
        assert data["model_used"] == "semantic_analyzer_fallback"

    @patch('main.get_semantic_analyzer')
    def test_semantic_feedback_endpoint_missing_analyzer(self, mock_get_analyzer, client, sample_semantic_feedback_request):
        """Test semantic feedback endpoint when analyzer cannot be created."""
        mock_get_analyzer.side_effect = Exception("Cannot create analyzer")
        
        response = client.post("/feedback/semantic", json=sample_semantic_feedback_request)
        
        assert response.status_code == 200
        data = response.json()
        assert "encountered an error" in data["feedback"]
        assert data["model_used"] == "semantic_analyzer_fallback"


class TestEndpointIntegration:
    """Test endpoint integration scenarios."""

    def test_all_endpoints_accessible(self, client):
        """Test that all main endpoints are accessible."""
        endpoints = [
            ("/", "GET"),
            ("/health", "GET"),
            ("/docs", "GET"),
        ]
        
        for endpoint, method in endpoints:
            if method == "GET":
                response = client.get(endpoint)
                assert response.status_code == 200

    @patch('main.call_openwebui_api')
    @patch('main.get_semantic_analyzer')
    def test_multiple_concurrent_requests(self, mock_get_analyzer, mock_api_call, client, mock_semantic_analyzer):
        """Test handling multiple concurrent requests."""
        mock_api_call.return_value = "Test feedback"
        mock_get_analyzer.return_value = mock_semantic_analyzer
        
        request_data = {
            "user_answer": "Test answer",
            "sample_solution": "Test solution",
            "question_text": "Test question"
        }
        
        # Send multiple requests
        responses = []
        for _ in range(5):
            response = client.post("/feedback", json=request_data)
            responses.append(response)
        
        # All should succeed
        for response in responses:
            assert response.status_code == 200
