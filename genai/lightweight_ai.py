"""
Lightweight AI handler using optimized transformers models for quiz feedback.
This replaces GPT4All with much smaller, faster models while maintaining quality.
"""
import logging
from sentence_transformers import SentenceTransformer
import numpy as np
from typing import Dict, List
import time

logger = logging.getLogger(__name__)

class LightweightAI:
    """
    Lightweight AI model for quiz feedback using semantic similarity.
    
    Models used:
    - all-MiniLM-L6-v2: Sentence transformer for similarity (~80MB)
    - Rule-based feedback generation for speed and reliability
    
    Total storage: ~80MB vs 3-7GB for GPT4All
    Inference time: ~0.5-1 seconds vs 10-30 seconds for GPT4All
    """
    
    def __init__(self):
        self.similarity_model = None
        self.is_initialized = False
        
    def initialize(self) -> bool:
        """Initialize lightweight models"""
        try:
            logger.info("Initializing lightweight AI models...")
            start_time = time.time()
            
            # Load sentence transformer for semantic similarity (~80MB)
            logger.info("Loading sentence transformer model...")
            self.similarity_model = SentenceTransformer('all-MiniLM-L6-v2')
            
            self.is_initialized = True
            init_time = time.time() - start_time
            logger.info(f"Lightweight AI models initialized successfully in {init_time:.2f}s")
            return True
            
        except Exception as e:
            logger.error(f"Failed to initialize lightweight AI: {e}")
            return False
    
    def is_available(self) -> bool:
        """Check if models are available"""
        return self.is_initialized and self.similarity_model is not None
    
    def calculate_similarity(self, text1: str, text2: str) -> float:
        """Calculate semantic similarity between two texts"""
        try:
            # Clean texts
            text1 = text1.strip()
            text2 = text2.strip()
            
            if not text1 or not text2:
                return 0.0
                
            # Generate embeddings
            embeddings = self.similarity_model.encode([text1, text2])
            
            # Calculate cosine similarity
            similarity = np.dot(embeddings[0], embeddings[1]) / (
                np.linalg.norm(embeddings[0]) * np.linalg.norm(embeddings[1])
            )
            
            # Ensure score is between 0 and 1
            return max(0.0, min(1.0, float(similarity)))
            
        except Exception as e:
            logger.error(f"Similarity calculation error: {e}")
            return 0.5
    
    def generate_feedback_text(self, similarity_score: float) -> str:
        """Generate contextual feedback based on similarity score"""
        try:
            # Create simple, direct feedback based on similarity score
            if similarity_score >= 0.8:
                return "Your answer demonstrates excellent understanding of the key concepts."
            elif similarity_score >= 0.6:
                return "Good answer with solid understanding. Consider adding more detail."
            elif similarity_score >= 0.4:
                return "Your answer shows basic understanding but could be more comprehensive."
            else:
                return "Your answer needs improvement. Review the key concepts and try to be more specific."
            
        except Exception as e:
            logger.error(f"Text generation error: {e}")
            return ""
    
    def analyze_answer_concepts(self, user_answer: str, sample_solution: str) -> Dict[str, List[str]]:
        """Analyze concept coverage using keyword matching and similarity"""
        try:
            # Extract key concepts (simplified approach)
            def extract_concepts(text: str) -> set:
                words = text.lower().split()
                # Filter important technical terms (length > 2, not common words)
                stop_words = {'the', 'and', 'or', 'but', 'in', 'on', 'at', 'to', 'for', 'of', 'with', 'by', 'this', 'that', 'these', 'those', 'is', 'are', 'was', 'were', 'be', 'been', 'have', 'has', 'had', 'do', 'does', 'did', 'will', 'would', 'could', 'should', 'may', 'might', 'can', 'must'}
                concepts = {word for word in words if len(word) > 2 and word not in stop_words}
                return concepts
            
            user_concepts = extract_concepts(user_answer)
            sample_concepts = extract_concepts(sample_solution)
            
            # Find covered and missing concepts
            covered = list(user_concepts.intersection(sample_concepts))
            missing = list(sample_concepts - user_concepts)
            extra = list(user_concepts - sample_concepts)
            
            return {
                "covered": covered[:5],  # Limit to top 5
                "missing": missing[:5],
                "extra": extra[:3]
            }
            
        except Exception as e:
            logger.error(f"Concept analysis error: {e}")
            return {"covered": [], "missing": [], "extra": []}
    
    def generate_feedback(self, user_answer: str, sample_solution: str) -> Dict:
        """Generate comprehensive feedback using lightweight AI"""
        try:
            start_time = time.time()
            
            # Calculate semantic similarity
            similarity_score = self.calculate_similarity(user_answer, sample_solution)
            
            # Analyze concepts
            concepts = self.analyze_answer_concepts(user_answer, sample_solution)
            
            # Generate AI feedback text
            ai_feedback = self.generate_feedback_text(similarity_score)
            
            # Generate structured feedback
            feedback_data = self._create_structured_feedback(
                similarity_score, concepts, ai_feedback, user_answer, sample_solution
            )
            
            inference_time = time.time() - start_time
            logger.info(f"Lightweight AI feedback generated in {inference_time:.2f}s")
            
            return feedback_data
            
        except Exception as e:
            logger.error(f"Feedback generation error: {e}")
            return self._fallback_feedback()
    
    def _create_structured_feedback(self, similarity: float, concepts: Dict, ai_feedback: str, 
                                  user_answer: str, sample_solution: str) -> Dict:
        """Create structured feedback based on analysis"""
        
        # Determine overall quality based on similarity
        if similarity >= 0.85:
            quality_level = "excellent"
            base_feedback = "Excellent answer! You demonstrate a strong understanding of the concepts."
        elif similarity >= 0.7:
            quality_level = "good"
            base_feedback = "Good answer! You show solid understanding with room for improvement."
        elif similarity >= 0.5:
            quality_level = "fair"
            base_feedback = "Fair answer. You have basic understanding but need to expand on key concepts."
        else:
            quality_level = "needs_improvement"
            base_feedback = "Your answer needs improvement. Focus on the core concepts and be more specific."
        
        # Combine base feedback with AI-generated feedback
        if ai_feedback:
            feedback = f"{base_feedback} {ai_feedback}"
        else:
            feedback = base_feedback
        
        # Generate strengths
        strengths = []
        if concepts["covered"]:
            strengths.append(f"Correctly identified key concepts: {', '.join(concepts['covered'][:3])}")
        if similarity >= 0.6:
            strengths.append("Shows understanding of the main topic")
        if len(user_answer.split()) >= len(sample_solution.split()) * 0.5:
            strengths.append("Provided detailed explanation")
        
        # Generate weaknesses
        weaknesses = []
        if concepts["missing"]:
            weaknesses.append(f"Missing important concepts: {', '.join(concepts['missing'][:3])}")
        if similarity < 0.7:
            weaknesses.append("Could be more comprehensive")
        if len(user_answer.split()) < len(sample_solution.split()) * 0.3:
            weaknesses.append("Answer could be more detailed")
        
        # Generate suggestions
        suggestions = []
        if concepts["missing"]:
            suggestions.append(f"Include discussion of: {', '.join(concepts['missing'][:2])}")
        if similarity < 0.6:
            suggestions.append("Review the sample solution for key points you may have missed")
        suggestions.append("Consider adding specific examples to strengthen your answer")
        
        # Ensure we have at least one item in each category
        if not strengths:
            strengths = ["You attempted to answer the question"]
        if not weaknesses and similarity < 0.9:
            weaknesses = ["Could provide more detail"]
        if not suggestions:
            suggestions = ["Review course materials for additional insights"]
        
        return {
            "score": float(similarity),
            "feedback": feedback,
            "strengths": strengths[:3],  # Limit to 3 items
            "weaknesses": weaknesses[:3],
            "suggestions": suggestions[:3],
            "quality_level": quality_level,
            "concepts_analysis": concepts
        }
    
    def _fallback_feedback(self) -> Dict:
        """Fallback feedback when AI fails"""
        return {
            "score": 0.5,
            "feedback": "Your answer has been received. Please compare it with the sample solution to identify areas for improvement.",
            "strengths": ["You provided an answer"],
            "weaknesses": ["AI analysis temporarily unavailable"],
            "suggestions": ["Review the sample solution", "Try submitting again"],
            "quality_level": "unknown",
            "concepts_analysis": {"covered": [], "missing": [], "extra": []}
        }
