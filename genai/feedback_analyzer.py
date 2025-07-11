"""
Semantic similarity analysis for educational feedback
"""

from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
import re
import logging
from typing import Tuple, List

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
                     'would', 'may', 'might', 'must'}
        
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
    
    def generate_semantic_feedback(self, question_text: str, user_answer: str, sample_solution: str) -> dict:
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
            
            # Generate feedback based on similarity score
            if similarity >= 0.8:
                feedback = f"Excellent semantic match ({similarity:.2f}). Your answer aligns very well with the expected solution."
            elif similarity >= 0.6:
                feedback = f"Good semantic match ({similarity:.2f}). Your answer covers the main concepts well."
            elif similarity >= 0.4:
                feedback = f"Moderate semantic match ({similarity:.2f}). Your answer addresses some key points."
            else:
                feedback = f"Low semantic match ({similarity:.2f}). Your answer differs significantly from the expected solution."
            
            # Generate structured components
            strengths = []
            if covered:
                strengths.append(f"Covered key concepts: {', '.join(covered[:3])}")
            if similarity >= 0.6:
                strengths.append("Good conceptual understanding demonstrated")
            if not strengths:
                strengths.append("Attempted to answer the question")
            
            weaknesses = []
            if missing:
                weaknesses.append(f"Missing concepts: {', '.join(missing[:3])}")
            if similarity < 0.6:
                weaknesses.append("Could be more comprehensive")
            
            suggestions = [
                "Review the sample solution for completeness",
                "Focus on incorporating key concepts",
                "Practice explaining concepts in your own words"
            ]
            
            return {
                "feedback": feedback,
                "suggestions": suggestions,
                "strengths": strengths,
                "weaknesses": weaknesses,
                "similarity_score": similarity
            }
            
        except Exception as e:
            logger.error(f"Error generating semantic feedback: {e}")
            return {
                "feedback": "Unable to analyze similarity",
                "suggestions": ["Try again later"],
                "strengths": ["Provided an answer"],
                "weaknesses": ["Analysis unavailable"],
                "similarity_score": 0.5
            }
