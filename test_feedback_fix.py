#!/usr/bin/env python3
"""
Test script to verify that the real local LLM returns only overall feedback
without structured feedback (strengths, weaknesses, suggestions).
"""

import requests
import json

# Test data
test_data = {
    "question": "What is the capital of France?",
    "answer": "Paris",
    "model_type": "local"  # This should use the real local LLM
}

def test_feedback_endpoint():
    """Test the feedback endpoint with real local LLM"""
    print("Testing feedback endpoint with real local LLM...")
    print(f"Request data: {json.dumps(test_data, indent=2)}")
    
    try:
        # Make request to the feedback endpoint
        response = requests.post(
            "http://localhost:8084/genai/feedback",
            json=test_data,
            timeout=30
        )
        
        print(f"\nResponse status: {response.status_code}")
        
        if response.status_code == 200:
            response_data = response.json()
            print(f"Response data: {json.dumps(response_data, indent=2)}")
            
            # Check if the response has the expected structure
            if "overall_feedback" in response_data:
                print(f"\n✓ Overall feedback present: {response_data['overall_feedback']}")
            else:
                print("\n✗ Overall feedback missing!")
                
            # Check for structured feedback (should be empty for real local LLM)
            strengths = response_data.get("strengths", [])
            weaknesses = response_data.get("weaknesses", [])
            suggestions = response_data.get("suggestions", [])
            
            print("\nStructured feedback check:")
            print(f"  Strengths: {strengths} (should be empty)")
            print(f"  Weaknesses: {weaknesses} (should be empty)")
            print(f"  Suggestions: {suggestions} (should be empty)")
            
            # Verify the fix
            if len(strengths) == 0 and len(weaknesses) == 0 and len(suggestions) == 0:
                print("\n🎉 SUCCESS: Real local LLM returns only overall feedback (no structured feedback)")
            else:
                print("\n❌ ISSUE: Real local LLM still returns structured feedback")
                
        else:
            print(f"Error response: {response.text}")
            
    except requests.exceptions.RequestException as e:
        print(f"Request failed: {e}")
        print("This might indicate the GenAI service is not running or not ready yet")

def test_semantic_analyzer():
    """Test with semantic analyzer to ensure it still provides structured feedback"""
    print("\n" + "="*60)
    print("Testing feedback endpoint with semantic analyzer...")
    
    semantic_data = test_data.copy()
    semantic_data["model_type"] = "semantic"
    
    print(f"Request data: {json.dumps(semantic_data, indent=2)}")
    
    try:
        response = requests.post(
            "http://localhost:8084/genai/feedback",
            json=semantic_data,
            timeout=30
        )
        
        print(f"\nResponse status: {response.status_code}")
        
        if response.status_code == 200:
            response_data = response.json()
            print(f"Response data: {json.dumps(response_data, indent=2)}")
            
            # Check for structured feedback (should be present for semantic analyzer)
            strengths = response_data.get("strengths", [])
            weaknesses = response_data.get("weaknesses", [])
            suggestions = response_data.get("suggestions", [])
            
            print("\nStructured feedback check:")
            print(f"  Strengths: {len(strengths)} items")
            print(f"  Weaknesses: {len(weaknesses)} items")
            print(f"  Suggestions: {len(suggestions)} items")
            
            if len(strengths) > 0 or len(weaknesses) > 0 or len(suggestions) > 0:
                print("\n✓ Semantic analyzer provides structured feedback as expected")
            else:
                print("\n⚠️  Semantic analyzer returned empty structured feedback (might be normal)")
                
        else:
            print(f"Error response: {response.text}")
            
    except requests.exceptions.RequestException as e:
        print(f"Request failed: {e}")

if __name__ == "__main__":
    test_feedback_endpoint()
    test_semantic_analyzer()
