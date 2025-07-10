#!/usr/bin/env python3
"""
Debug script to test the real local LLM behavior
"""

import requests
import json

def test_local_llm_feedback():
    """Test the local LLM feedback directly"""
    print("🔍 Testing Real Local LLM Feedback Generation...")
    
    genai_url = "http://localhost:8084"
    
    # Test request
    test_request = {
        "question_text": "What is Continuous Integration (CI)?",
        "user_answer": "CI is a practice where developers frequently integrate code changes into a shared repository.",
        "sample_solution": "Continuous Integration (CI) is a software development practice where developers regularly integrate their code changes into a central repository, followed by automated builds and tests.",
        "model_type": "local"  # Explicitly request local model
    }
    
    try:
        print(f"📤 Sending request to {genai_url}/api/feedback")
        print(f"   Model type: {test_request['model_type']}")
        
        response = requests.post(
            f"{genai_url}/api/feedback",
            json=test_request,
            headers={"Content-Type": "application/json"},
            timeout=30
        )
        
        if response.status_code == 200:
            feedback = response.json()
            print(f"✅ Response Status: {response.status_code}")
            print("📋 Feedback Response:")
            print(f"   Model Used: {feedback.get('model_used', 'N/A')}")
            print(f"   Feedback: {feedback.get('feedback', 'N/A')[:100]}...")
            print(f"   Suggestions count: {len(feedback.get('suggestions', []))}")
            print(f"   Strengths count: {len(feedback.get('strengths', []))}")
            print(f"   Weaknesses count: {len(feedback.get('weaknesses', []))}")
            
            # Print the actual arrays to see what's in them
            if feedback.get('suggestions'):
                print(f"   Suggestions: {feedback.get('suggestions')}")
            if feedback.get('strengths'):
                print(f"   Strengths: {feedback.get('strengths')}")
            if feedback.get('weaknesses'):
                print(f"   Weaknesses: {feedback.get('weaknesses')}")
                
            print("\n📊 Full Response:")
            print(json.dumps(feedback, indent=2))
            
        else:
            print(f"❌ Request Failed: {response.status_code}")
            print(f"   Error: {response.text}")
            
    except Exception as e:
        print(f"❌ Error: {e}")

def test_health():
    """Test the health endpoint to see available models"""
    print("\n🏥 Testing Health Endpoint...")
    
    genai_url = "http://localhost:8084"
    
    try:
        response = requests.get(f"{genai_url}/health", timeout=10)
        if response.status_code == 200:
            health = response.json()
            print(f"✅ Health Status: {health.get('status', 'unknown')}")
            models = health.get('models', {})
            print(f"   OpenAI available: {models.get('openai', False)}")
            print(f"   Local LLM available: {models.get('local_llm', False)}")
            print(f"   Semantic analyzer available: {models.get('semantic_analyzer', False)}")
        else:
            print(f"❌ Health check failed: {response.status_code}")
    except Exception as e:
        print(f"❌ Health check error: {e}")

if __name__ == "__main__":
    test_health()
    test_local_llm_feedback()
