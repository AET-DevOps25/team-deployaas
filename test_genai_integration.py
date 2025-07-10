#!/usr/bin/env python3
"""
Test script to verify GenAI integration functionality
"""

import requests
import json
import time

def test_genai_service():
    """Test the GenAI service directly"""
    print("🧪 Testing GenAI Service...")
    
    base_url = "http://localhost:8084"
    
    # Test health endpoint
    try:
        response = requests.get(f"{base_url}/health", timeout=10)
        print(f"✅ Health Check: {response.status_code} - {response.json()}")
    except Exception as e:
        print(f"❌ Health Check Failed: {e}")
        return False
    
    # Test feedback endpoint
    test_request = {
        "question_text": "What is the purpose of Continuous Integration (CI)?",
        "user_answer": "CI is used to automatically build and test code when changes are made to the repository.",
        "sample_solution": "Continuous Integration (CI) is a development practice where developers integrate code into a shared repository frequently. Each integration is verified by an automated build and automated tests to detect integration errors as quickly as possible.",
        "model_type": "local"
    }
    
    try:
        response = requests.post(
            f"{base_url}/api/feedback", 
            json=test_request,
            headers={"Content-Type": "application/json"},
            timeout=30
        )
        
        if response.status_code == 200:
            feedback = response.json()
            print(f"✅ Feedback Generation: Success")
            print(f"   Model: {feedback.get('model_used', 'N/A')}")
            print(f"   Feedback: {feedback.get('feedback', 'N/A')[:100]}...")
            return True
        else:
            print(f"❌ Feedback Generation Failed: {response.status_code} - {response.text}")
            return False
            
    except Exception as e:
        print(f"❌ Feedback Generation Error: {e}")
        return False

def test_quiz_service_integration():
    """Test the Quiz Service GenAI integration"""
    print("\n🧪 Testing Quiz Service Integration...")
    
    base_url = "http://localhost:8081"
    
    # Test GenAI health endpoint
    try:
        response = requests.get(f"{base_url}/api/quiz/genai/health", timeout=10)
        if response.status_code == 200:
            health = response.json()
            print(f"✅ GenAI Integration Health: {health.get('genai_service_available', False)}")
        else:
            print(f"❌ GenAI Integration Health Failed: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ GenAI Integration Health Error: {e}")
        return False
    
    # Test getting questions (needed for submission test)
    try:
        # First get chapters
        chapters_response = requests.get(f"{base_url}/api/quiz/chapters", timeout=10)
        if chapters_response.status_code != 200:
            print(f"❌ Could not get chapters: {chapters_response.status_code}")
            return False
            
        chapters = chapters_response.json()
        if not chapters:
            print("❌ No chapters found")
            return False
            
        chapter_id = chapters[0]['id']
        
        # Get questions for first chapter
        questions_response = requests.get(
            f"{base_url}/api/quiz/chapters/{chapter_id}/questions", 
            timeout=10
        )
        
        if questions_response.status_code != 200:
            print(f"❌ Could not get questions: {questions_response.status_code}")
            return False
            
        questions = questions_response.json()
        if not questions:
            print("❌ No questions found")
            return False
            
        question_id = questions[0]['id']
        print(f"✅ Found question: {question_id}")
        
        # Test answer submission
        test_submission = {
            "answer": "Continuous Integration (CI) is a practice where developers frequently integrate their code changes into a shared repository, with each integration being automatically verified through builds and tests.",
            "model_type": "local"
        }
        
        submit_response = requests.post(
            f"{base_url}/api/quiz/questions/{question_id}/submit",
            json=test_submission,
            headers={"Content-Type": "application/json"},
            timeout=45
        )
        
        if submit_response.status_code == 200:
            result = submit_response.json()
            print(f"✅ Answer Submission: Success")
            print(f"   Feedback: {result.get('feedback', 'N/A')[:100]}...")
            return True
        else:
            print(f"❌ Answer Submission Failed: {submit_response.status_code} - {submit_response.text}")
            return False
            
    except Exception as e:
        print(f"❌ Quiz Service Integration Error: {e}")
        return False

def test_advanced_feedback():
    """Test advanced feedback functionality"""
    print("\n🧪 Testing Advanced Feedback...")
    
    genai_url = "http://localhost:8084"
    
    test_request = {
        "question_text": "Explain the benefits of Infrastructure as Code (IaC)",
        "user_answer": "IaC allows you to manage infrastructure using code files instead of manual processes.",
        "sample_solution": "Infrastructure as Code (IaC) provides several key benefits: 1) Version control and change tracking for infrastructure, 2) Reproducible and consistent environments, 3) Reduced manual errors and configuration drift, 4) Faster provisioning and scaling, 5) Better collaboration through code reviews, 6) Cost optimization through automated resource management."
    }
    
    try:
        response = requests.post(
            f"{genai_url}/api/feedback/advanced",
            json=test_request,
            headers={"Content-Type": "application/json"},
            timeout=45
        )
        
        if response.status_code == 200:
            feedback = response.json()
            print(f"✅ Advanced Feedback: Success")
            print(f"   Strengths: {len(feedback.get('strengths', []))} items")
            print(f"   Suggestions: {len(feedback.get('suggestions', []))} items")
            return True
        else:
            print(f"❌ Advanced Feedback Failed: {response.status_code} - {response.text}")
            return False
            
    except Exception as e:
        print(f"❌ Advanced Feedback Error: {e}")
        return False

if __name__ == "__main__":
    print("🚀 Starting GenAI Integration Tests\n")
    
    print("⏳ Waiting for services to be ready...")
    time.sleep(5)
    
    success_count = 0
    total_tests = 3
    
    if test_genai_service():
        success_count += 1
    
    if test_quiz_service_integration():
        success_count += 1
        
    if test_advanced_feedback():
        success_count += 1
    
    print(f"\n📊 Test Results: {success_count}/{total_tests} tests passed")
    
    if success_count == total_tests:
        print("🎉 All tests passed! GenAI integration is working correctly.")
    else:
        print("⚠️  Some tests failed. Check the logs above for details.")
        exit(1)
