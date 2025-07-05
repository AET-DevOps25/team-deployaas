"""
Advanced feedback analysis using semantic similarity and NLP techniques
"""

from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
import re
import logging
from typing import Tuple, List

logger = logging.getLogger(__name__)

class AdvancedFeedbackAnalyzer:
    """
    Advanced analyzer that uses semantic similarity and NLP techniques
    to provide detailed feedback on quiz answers.
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
    
    def generate_detailed_feedback(self, 
                                 question: str,
                                 user_answer: str, 
                                 sample_solution: str) -> dict:
        """
        Generate comprehensive feedback using multiple analysis techniques
        """
        # Calculate semantic similarity
        similarity_score = self.analyze_semantic_similarity(user_answer, sample_solution)
        
        # Analyze concept coverage
        covered_concepts, missing_concepts = self.analyze_coverage(user_answer, sample_solution)
        
        # Basic length and structure analysis
        user_length = len(user_answer.split())
        sample_length = len(sample_solution.split())
        length_ratio = user_length / max(sample_length, 1)
        
        # Generate feedback components
        feedback_data = {
            "similarity_score": similarity_score,
            "covered_concepts": covered_concepts,
            "missing_concepts": missing_concepts,
            "length_analysis": {
                "user_words": user_length,
                "sample_words": sample_length,
                "ratio": length_ratio
            }
        }
        
        # Generate structured feedback
        strengths = self.generate_strengths(feedback_data)
        weaknesses = self.generate_weaknesses(feedback_data)
        suggestions = self.generate_suggestions(feedback_data, missing_concepts)
        overall_feedback = self.generate_overall_feedback(feedback_data)
        
        return {
            "feedback": overall_feedback,
            "strengths": strengths,
            "weaknesses": weaknesses,
            "suggestions": suggestions,
            "analysis": feedback_data
        }
    
    def calculate_final_score(self, similarity: float, covered: List[str], 
                            missing: List[str], length_ratio: float) -> float:
        """Calculate final score using weighted metrics"""
        
        # Semantic similarity (50% weight)
        semantic_weight = 0.5
        
        # Concept coverage (30% weight)
        total_concepts = len(covered) + len(missing)
        coverage_score = len(covered) / max(total_concepts, 1) if total_concepts > 0 else 0.5
        coverage_weight = 0.3
        
        # Length appropriateness (20% weight)
        # Penalize too short or too long answers
        length_score = 1.0 if 0.5 <= length_ratio <= 2.0 else max(0.3, 1.0 - abs(length_ratio - 1.0) * 0.2)
        length_weight = 0.2
        
        final_score = (similarity * semantic_weight + 
                      coverage_score * coverage_weight + 
                      length_score * length_weight)
        
        return max(0.0, min(1.0, final_score))
    
    def generate_strengths(self, feedback_data: dict) -> List[str]:
        """Generate list of strengths based on analysis"""
        strengths = []
        
        if feedback_data["similarity_score"] > 0.7:
            strengths.append("Your answer shows strong conceptual understanding")
        
        if len(feedback_data["covered_concepts"]) > 2:
            strengths.append(f"You covered key concepts: {', '.join(feedback_data['covered_concepts'][:3])}")
        
        if 0.8 <= feedback_data["length_analysis"]["ratio"] <= 1.5:
            strengths.append("Your answer has appropriate length and detail")
        
        if not strengths:
            strengths.append("You provided a complete response to the question")
        
        return strengths
    
    def generate_weaknesses(self, feedback_data: dict) -> List[str]:
        """Generate list of weaknesses based on analysis"""
        weaknesses = []
        
        if feedback_data["similarity_score"] < 0.5:
            weaknesses.append("Your answer could be more aligned with the key concepts")
        
        if len(feedback_data["missing_concepts"]) > 2:
            weaknesses.append(f"Consider including concepts like: {', '.join(feedback_data['missing_concepts'][:3])}")
        
        if feedback_data["length_analysis"]["ratio"] < 0.5:
            weaknesses.append("Your answer could be more detailed and comprehensive")
        elif feedback_data["length_analysis"]["ratio"] > 2.0:
            weaknesses.append("Your answer might be too verbose - focus on key points")
        
        if not weaknesses:
            weaknesses.append("Minor improvements could enhance clarity")
        
        return weaknesses
    
    def generate_suggestions(self, feedback_data: dict, missing_concepts: List[str]) -> List[str]:
        """Generate actionable suggestions"""
        suggestions = []
        
        if missing_concepts:
            suggestions.append(f"Include discussion of: {', '.join(missing_concepts[:2])}")
        
        if feedback_data["similarity_score"] < 0.6:
            suggestions.append("Review the sample solution to understand key points")
        
        if feedback_data["length_analysis"]["ratio"] < 0.7:
            suggestions.append("Provide more detailed explanations and examples")
        
        suggestions.append("Practice explaining concepts in your own words")
        
        return suggestions
    
    def generate_overall_feedback(self, feedback_data: dict) -> str:
        """Generate overall feedback summary"""
        score = feedback_data["similarity_score"]
        
        if score >= 0.8:
            return "Excellent answer! You demonstrate strong understanding of the key concepts."
        elif score >= 0.6:
            return "Good answer with solid understanding. Some minor improvements could strengthen your response."
        elif score >= 0.4:
            return "Decent attempt with room for improvement. Focus on key concepts and provide more detail."
        else:
            return "Your answer needs significant improvement. Review the material and try to address the core concepts."
