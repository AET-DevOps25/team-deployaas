"""
Semantic similarity analysis for educational feedback
"""

from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
import re
import logging
from typing import Tuple, List, Dict, Any, Optional
import os

logger = logging.getLogger(__name__)

class SemanticAnalyzer:
    """
    Semantic similarity analyzer for educational feedback.
    This provides pure semantic analysis without generative AI.
    """
    
    def __init__(self):
        """Initialize the analyzer with sentence transformer model"""
        try:
            # Use a lightweight but effective sentence transformer
            self.sentence_model = SentenceTransformer('all-MiniLM-L6-v2')
            logger.info("Sentence transformer model loaded successfully")
        except Exception as e:
            logger.error(f"Failed to load sentence transformer: {e}")
            self.sentence_model = None
    
    def analyze_semantic_similarity(self, user_answer: str, sample_solution: str) -> float:
        """
        Calculate semantic similarity between user answer and sample solution
        
        Returns:
            float: Similarity score between 0.0 and 1.0
        """
        if not self.sentence_model:
            return 0.5  # Default score if model not available
            
        try:
            # Clean and prepare texts
            user_clean = self.clean_text(user_answer)
            sample_clean = self.clean_text(sample_solution)
            
            # Generate embeddings
            embeddings = self.sentence_model.encode([user_clean, sample_clean])
            
            # Calculate cosine similarity
            similarity = cosine_similarity([embeddings[0]], [embeddings[1]])[0][0]
            
            # Ensure score is between 0 and 1
            return max(0.0, min(1.0, float(similarity)))
            
        except Exception as e:
            logger.error(f"Error calculating semantic similarity: {e}")
            return 0.5
    
    def clean_text(self, text: str) -> str:
        """Clean and normalize text for analysis"""
        # Remove extra whitespace
        text = re.sub(r'\s+', ' ', text.strip())
        
        # Convert to lowercase for better comparison
        text = text.lower()
        
        # Remove common punctuation that doesn't affect meaning
        text = re.sub(r'[^\w\s\-\']', ' ', text)
        
        # Clean up any double spaces created by punctuation removal
        text = re.sub(r'\s+', ' ', text.strip())
        
        return text    
        
    def extract_key_concepts(self, text: str) -> List[str]:
        """Extract key concepts from text (simplified approach)"""
        # This is a basic implementation - could be enhanced with NER
        words = self.clean_text(text).split()
        
        # Filter out common words and keep important terms
        stop_words = {'a', 'an', 'and', 'are', 'as', 'at', 'be', 'by', 'for', 
                     'from', 'has', 'he', 'in', 'is', 'it', 'its', 'of', 'on', 
                     'that', 'the', 'to', 'was', 'were', 'will', 'with', 'this',
                     'they', 'them', 'these', 'those', 'can', 'could', 'should',
                     'would', 'may', 'might', 'must', 'have', 'had', 'do', 'does',
                     'did', 'get', 'got', 'make', 'made', 'take', 'took', 'go',
                     'went', 'come', 'came', 'see', 'saw', 'know', 'knew', 'think',
                     'thought', 'say', 'said', 'tell', 'told', 'give', 'gave'}
        
        key_concepts = [word for word in words 
                       if len(word) > 2 and word not in stop_words]
        
        return list(set(key_concepts))  # Remove duplicates
    
    def analyze_coverage(self, user_answer: str, sample_solution: str) -> Tuple[List[str], List[str]]:
        """
        Analyze concept coverage between user answer and sample solution
        
        Returns:
            Tuple of (covered_concepts, missing_concepts)
        """
        user_concepts = set(self.extract_key_concepts(user_answer))
        sample_concepts = set(self.extract_key_concepts(sample_solution))
        
        # Find overlapping concepts (what user covered)
        covered = list(user_concepts.intersection(sample_concepts))
        
        # Find missing concepts (what user missed)
        missing = list(sample_concepts - user_concepts)
        
        return covered, missing
    
    def generate_semantic_feedback(self, question_text: str, user_answer: str, sample_solution: str) -> Dict[str, Any]:
        """
        Generate structured feedback using semantic similarity analysis
        
        Returns:
            dict: Structured feedback with feedback, suggestions, strengths, weaknesses
        """
        try:
            # Calculate similarity
            similarity = self.analyze_semantic_similarity(user_answer, sample_solution)
            
            # Analyze concept coverage
            covered, missing = self.analyze_coverage(user_answer, sample_solution)
            
            # Convert similarity to score out of 100
            score = similarity * 100
            
            # Generate feedback based on similarity score
            if similarity >= 0.8:
                feedback = f"Excellent semantic match (similarity: {similarity:.3f}). Your answer aligns very well with the expected solution and demonstrates strong understanding of the key concepts."
            elif similarity >= 0.6:
                feedback = f"Good semantic match (similarity: {similarity:.3f}). Your answer covers the main concepts well and shows solid understanding."
            elif similarity >= 0.4:
                feedback = f"Moderate semantic match (similarity: {similarity:.3f}). Your answer addresses some key points but could be more comprehensive."
            else:
                feedback = f"Low semantic match (similarity: {similarity:.3f}). Your answer differs significantly from the expected solution. Consider reviewing the key concepts."
            
            # Generate structured components
            strengths = []
            if covered:
                if len(covered) <= 3:
                    strengths.append(f"Successfully covered key concepts: {', '.join(covered)}")
                else:
                    strengths.append(f"Successfully covered key concepts: {', '.join(covered[:3])} and {len(covered)-3} more")
            
            if similarity >= 0.7:
                strengths.append("Strong conceptual understanding demonstrated")
            elif similarity >= 0.5:
                strengths.append("Good foundational understanding shown")
            
            if len(user_answer.strip()) > 50:  # Substantial answer
                strengths.append("Provided a detailed response")
            
            if not strengths:
                strengths.append("Attempted to answer the question")
            
            weaknesses = []
            if missing:
                if len(missing) <= 3:
                    weaknesses.append(f"Missing important concepts: {', '.join(missing)}")
                else:
                    weaknesses.append(f"Missing important concepts: {', '.join(missing[:3])} and {len(missing)-3} more")
            
            if similarity < 0.5:
                weaknesses.append("Answer could be more aligned with expected solution")
            
            if similarity < 0.3:
                weaknesses.append("May need to review fundamental concepts")
            
            suggestions = []
            if missing:
                suggestions.append("Review the missing concepts to strengthen your understanding")
            if similarity < 0.6:
                suggestions.append("Focus on incorporating more key concepts from the topic")
            if len(covered) > 0 and len(missing) > len(covered):
                suggestions.append("Build upon the concepts you've correctly identified")
            
            suggestions.append("Practice explaining concepts in your own words")
            
            return {
                "feedback": feedback,
                "suggestions": suggestions,
                "strengths": strengths,
                "weaknesses": weaknesses,
                "score": round(score, 1),
                "similarity_score": round(similarity, 3),
                "model_used": "semantic_analyzer",
                "timestamp": "",  # Will be set by the main service
                "covered_concepts": covered,
                "missing_concepts": missing
            }
            
        except Exception as e:
            logger.error(f"Error generating semantic feedback: {e}")
            return {
                "feedback": f"Unable to perform semantic analysis due to technical error: {str(e)}",
                "suggestions": ["Please try again later", "Check your internet connection"],
                "strengths": ["Provided an answer"],
                "weaknesses": ["Analysis unavailable due to technical issues"],
                "score": 50.0,
                "similarity_score": 0.5,
                "model_used": "semantic_analyzer_fallback",
                "timestamp": "",
                "covered_concepts": [],
                "missing_concepts": []
            }


# Global instance to reuse the model
_semantic_analyzer = None

def get_semantic_analyzer() -> Optional[SemanticAnalyzer]:
    """Get or create semantic analyzer instance"""
    global _semantic_analyzer
    if _semantic_analyzer is None:
        try:
            _semantic_analyzer = SemanticAnalyzer()
        except Exception as e:
            print(f"Failed to initialize semantic analyzer: {e}")
            return None
    return _semantic_analyzer
