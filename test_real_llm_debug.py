#!/usr/bin/env python3
"""
Debug script to test the real local LLM specifically
"""

import requests
import json

def test_real_llm_directly():
    """Test the real local LLM endpoint directly"""
    print("🔍 Testing Real Local LLM Debug...")
    
    genai_url = "http://localhost:8084"
    
    test_request = {
        "question_text": "What is DevOps?",
        "user_answer": "DevOps is about collaboration between development and operations teams.",
        "sample_solution": "DevOps is a set of practices that combines software development and IT operations to shorten the development lifecycle.",
        "model_type": "local"  # This should trigger the real local LLM
    }
    
    try:
        print(f"📤 Sending request to {genai_url}/api/feedback")
        print(f"Request: {json.dumps(test_request, indent=2)}")
        
        response = requests.post(
            f"{genai_url}/api/feedback",
            json=test_request,
            headers={"Content-Type": "application/json"},
            timeout=30
        )
        
        print(f"\n📥 Response Status: {response.status_code}")
        
        if response.status_code == 200:
            feedback_data = response.json()
            print(f"✅ Success! Response:")
            print(json.dumps(feedback_data, indent=2))
            
            # Check what we actually got
            suggestions = feedback_data.get('suggestions', [])
            strengths = feedback_data.get('strengths', [])
            weaknesses = feedback_data.get('weaknesses', [])
            model_used = feedback_data.get('model_used', 'unknown')
            
            print(f"\n🔍 Analysis:")
            print(f"   Model used: {model_used}")
            print(f"   Suggestions count: {len(suggestions)}")
            print(f"   Strengths count: {len(strengths)}")
            print(f"   Weaknesses count: {len(weaknesses)}")
            
            if len(suggestions) == 0 and len(strengths) == 0 and len(weaknesses) == 0:
                print("✅ CORRECT: Real Local LLM returned empty structured arrays")
            else:
                print("❌ PROBLEM: Real Local LLM returned structured feedback:")
                if suggestions:
                    print(f"   Suggestions: {suggestions}")
                if strengths:
                    print(f"   Strengths: {strengths}")
                if weaknesses:
                    print(f"   Weaknesses: {weaknesses}")
                    
        else:
            print(f"❌ Request failed: {response.status_code}")
            print(f"Response: {response.text}")
            
    except Exception as e:
        print(f"❌ Error: {e}")

def test_models_endpoint():
    """Test the models endpoint to see what's available"""
    print("\n🔍 Testing Models Endpoint...")
    
    genai_url = "http://localhost:8084"
    
    try:
        response = requests.get(f"{genai_url}/api/models", timeout=10)
        
        if response.status_code == 200:
            models_data = response.json()
            print(f"✅ Models available:")
            print(json.dumps(models_data, indent=2))
        else:
            print(f"❌ Models endpoint failed: {response.status_code}")
            
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    test_models_endpoint()
    test_real_llm_directly()
