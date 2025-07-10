"""
Real Local LLM implementation using transformers library for educational feedback generation.
This provides actual generative AI capabilities using lightweight models.
"""

import os
import logging
import time
import numpy as np
from typing import Dict, Any, List, Optional
from sentence_transformers import SentenceTransformer
from transformers import AutoTokenizer, AutoModelForCausalLM, pipeline
import torch
import re
import numpy as np

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class RealLocalLLM:
    """Real Local LLM using HuggingFace transformers for actual text generation"""
    
    def __init__(self):
        """Initialize the real local LLM service"""
        self.embedding_model = None
        self.generative_model = None
        self.tokenizer = None
        self.text_generator = None
        self.device = "cpu"  # Use CPU for compatibility
        self.max_length = 200  # Keep responses concise
        
    def initialize(self) -> bool:
        """Initialize both embedding and generative models"""
        try:
            logger.info("Initializing Real Local LLM with generative capabilities...")
            start_time = time.time()
            
            # Initialize sentence transformer for similarity analysis
            logger.info("Loading sentence transformer model...")
            self.embedding_model = SentenceTransformer('all-MiniLM-L6-v2')
            logger.info("Sentence transformer loaded successfully")
            
            # Initialize lightweight generative model
            logger.info("Loading generative language model...")
            model_name = "gpt2"  # General-purpose text generation model
            
            # Load tokenizer and model
            self.tokenizer = AutoTokenizer.from_pretrained(model_name, padding_side='left')
            self.generative_model = AutoModelForCausalLM.from_pretrained(model_name)
            
            # Add padding token if not present
            if self.tokenizer.pad_token is None:
                self.tokenizer.pad_token = self.tokenizer.eos_token
            
            # Create text generation pipeline
            self.text_generator = pipeline(
                "text-generation",
                model=self.generative_model,
                tokenizer=self.tokenizer,
                device=0 if torch.cuda.is_available() else -1,
                max_new_tokens=50,
                do_sample=True,
                temperature=0.7,
                pad_token_id=self.tokenizer.eos_token_id
            )
            
            init_time = time.time() - start_time
            logger.info(f"Real Local LLM initialized successfully in {init_time:.2f}s")
            return True
            
        except Exception as e:
            logger.error(f"Failed to initialize Real Local LLM: {e}")
            return False
    
    def is_available(self) -> bool:
        """Check if the real local LLM is available and ready to use"""
        return (self.embedding_model is not None and 
                self.generative_model is not None and 
                self.text_generator is not None)
    
    def _calculate_similarity(self, text1: str, text2: str) -> float:
        """Calculate semantic similarity between two texts"""
        try:
            if not self.embedding_model:
                return 0.5  # Default similarity
            
            embeddings = self.embedding_model.encode([text1, text2])
            similarity = float(embeddings[0] @ embeddings[1].T / 
                             (np.linalg.norm(embeddings[0]) * np.linalg.norm(embeddings[1])))
            return max(0.0, min(1.0, similarity))
        except Exception as e:
            logger.error(f"Similarity calculation error: {e}")
            return 0.5
    
    def _generate_llm_feedback(self, question: str, user_answer: str, sample_solution: str, similarity: float) -> str:
        """Generate feedback using the real LLM"""
        try:
            # Create a simple, focused prompt for educational feedback
            prompt = f"Student answer: {user_answer[:150]}\nExpected: {sample_solution[:150]}\nFeedback:"

            # Generate response using the LLM
            response = self.text_generator(
                prompt,
                max_new_tokens=30,  # Shorter for better quality
                num_return_sequences=1,
                temperature=0.8,
                do_sample=True,
                truncation=True,
                pad_token_id=self.tokenizer.eos_token_id
            )
            
            # Extract the generated text
            generated_text = response[0]['generated_text']
            
            # Extract only the new part (after the prompt)
            feedback = generated_text[len(prompt):].strip()
            
            # Clean up the feedback
            cleaned_feedback = self._clean_generated_feedback(feedback)
            
            # Log what we got
            logger.info(f"LLM generated: {feedback[:100]}...")
            logger.info(f"Cleaned feedback: {cleaned_feedback}")
            
            # Use the cleaned feedback if it's good, otherwise use raw, otherwise fallback
            if cleaned_feedback and len(cleaned_feedback) > 10:
                return cleaned_feedback
            elif feedback and len(feedback) > 5:
                return feedback[:200]  # Limit length
            else:
                logger.warning("LLM generated insufficient feedback, using fallback")
                return self._fallback_feedback_text(similarity)
            
        except Exception as e:
            logger.error(f"LLM generation error: {e}")
            return self._fallback_feedback_text(similarity)
    
    def _clean_generated_feedback(self, text: str) -> str:
        """Clean and format the generated feedback"""
        # Remove common artifacts
        text = re.sub(r'^[^\w]*', '', text)  # Remove leading punctuation
        text = re.sub(r'[^\w\s.,!?-]*$', '', text)  # Remove trailing artifacts
        
        # Take only the first 1-2 sentences
        sentences = re.split(r'[.!?]+', text)
        clean_sentences = [s.strip() for s in sentences[:2] if s.strip()]
        
        if clean_sentences:
            result = '. '.join(clean_sentences)
            if not result.endswith(('.', '!', '?')):
                result += '.'
            return result
        
        return ""
    
    def _fallback_feedback_text(self, similarity: float) -> str:
        """Generate fallback feedback when LLM fails"""
        if similarity >= 0.8:
            return "Excellent answer! You demonstrate strong understanding of the key concepts."
        elif similarity >= 0.6:
            return "Good answer! You show solid understanding with room for improvement."
        elif similarity >= 0.4:
            return "Your answer shows basic understanding but could be more comprehensive."
        else:
            return "Your answer needs improvement. Focus on the core concepts and be more specific."
    
    def _extract_key_concepts(self, text: str) -> List[str]:
        """Extract key concepts from text using simple NLP"""
        # Simple concept extraction using capitalized words and important terms
        words = re.findall(r'\b[A-Z][a-z]+\b|\b(?:process|system|method|approach|technique|principle|concept|idea|theory|model|framework|structure|function|component|element|factor|aspect|feature|characteristic|property|attribute|benefit|advantage|disadvantage|problem|solution|result|outcome|effect|impact|cause|reason|purpose|goal|objective|requirement|condition|criteria|standard|rule|law|policy|procedure|step|stage|phase|level|type|kind|category|class|group|set|part|section|area|field|domain|scope|range|extent|degree|amount|quantity|quality|value|importance|significance|relevance|relationship|connection|link|association|correlation|dependency|interaction|communication|collaboration|cooperation|coordination|integration|implementation|execution|performance|evaluation|assessment|analysis|comparison|contrast|difference|similarity|pattern|trend|change|development|improvement|enhancement|optimization|efficiency|effectiveness|productivity|reliability|stability|security|safety|risk|challenge|issue|concern|consideration|recommendation|suggestion|advice|guidance|instruction|direction|example|instance|case|scenario|situation|context|background|history|experience|knowledge|understanding|learning|education|training|skill|ability|capability|capacity|potential|opportunity|advantage|benefit)\b', text, re.IGNORECASE)
        
        # Remove duplicates and return unique concepts
        unique_concepts = list(set([word.lower() for word in words]))
        return unique_concepts[:10]  # Limit to top 10 concepts
    
    def generate_feedback(self, user_answer: str, sample_solution: str, question_text: str) -> Dict[str, Any]:
        """
        Generate educational feedback using real LLM capabilities
        
        Args:
            user_answer: Student's answer
            sample_solution: The sample/correct solution
            question_text: The original question
            
        Returns:
            Dictionary containing feedback, suggestions, strengths, weaknesses, and model_used
        """
        try:
            logger.info("Generating feedback using Real Local LLM...")
            start_time = time.time()
            
            # Calculate similarity between answers
            similarity_score = self._calculate_similarity(user_answer, sample_solution)
            
            # Generate AI-powered feedback using the real LLM
            ai_feedback = self._generate_llm_feedback(question_text, user_answer, sample_solution, similarity_score)
            
            # Extract concepts for analysis
            user_concepts = set(self._extract_key_concepts(user_answer))
            sample_concepts = set(self._extract_key_concepts(sample_solution))
            
            covered_concepts = list(user_concepts.intersection(sample_concepts))
            missing_concepts = list(sample_concepts - user_concepts)
            
            # Generate structured components
            strengths = self._generate_strengths(similarity_score, covered_concepts)
            weaknesses = self._generate_weaknesses(similarity_score, missing_concepts)
            suggestions = self._generate_suggestions(similarity_score, missing_concepts, user_answer, sample_solution)
            
            generation_time = time.time() - start_time
            logger.info(f"Real LLM feedback generated in {generation_time:.2f}s")
            
            return {
                "feedback": ai_feedback,
                "suggestions": suggestions,
                "strengths": strengths,
                "weaknesses": weaknesses,
                "model_used": "real-local-llm",
                "similarity_score": similarity_score,
                "generation_time": generation_time
            }
            
        except Exception as e:
            logger.error(f"Real LLM feedback generation error: {e}")
            return self._fallback_response()
    
    def _generate_strengths(self, similarity: float, covered_concepts: List[str]) -> List[str]:
        """Generate strengths based on analysis"""
        strengths = []
        
        if similarity >= 0.7:
            strengths.append("Your answer demonstrates good understanding of the topic")
        
        if covered_concepts:
            concepts_text = ", ".join(covered_concepts[:3])
            strengths.append(f"You correctly addressed key concepts: {concepts_text}")
        
        if similarity >= 0.8:
            strengths.append("Excellent conceptual grasp and explanation")
        elif similarity >= 0.6:
            strengths.append("Solid foundation with the core ideas")
        
        return strengths[:3] if strengths else ["You attempted to answer the question"]
    
    def _generate_weaknesses(self, similarity: float, missing_concepts: List[str]) -> List[str]:
        """Generate weaknesses based on analysis"""
        weaknesses = []
        
        if missing_concepts:
            concepts_text = ", ".join(missing_concepts[:3])
            weaknesses.append(f"Consider including: {concepts_text}")
        
        if similarity < 0.6:
            weaknesses.append("Answer could be more comprehensive and detailed")
        
        if similarity < 0.4:
            weaknesses.append("Review the fundamental concepts for this topic")
        
        return weaknesses[:3] if weaknesses else []
    
    def _generate_suggestions(self, similarity: float, missing_concepts: List[str], 
                            user_answer: str, sample_solution: str) -> List[str]:
        """Generate actionable suggestions"""
        suggestions = []
        
        if missing_concepts:
            suggestions.append(f"Include discussion of: {', '.join(missing_concepts[:2])}")
        
        if similarity < 0.7:
            suggestions.append("Review the sample solution to identify additional key points")
        
        if len(user_answer.split()) < len(sample_solution.split()) * 0.6:
            suggestions.append("Provide more detailed explanations and examples")
        
        suggestions.append("Practice explaining concepts in your own words")
        
        return suggestions[:3]
    
    def _fallback_response(self) -> Dict[str, Any]:
        """Fallback response when LLM fails"""
        return {
            "feedback": "Your answer has been received. Please review the sample solution to improve your understanding.",
            "suggestions": ["Review the sample solution", "Practice with similar questions", "Ask for clarification if needed"],
            "strengths": ["You provided an answer"],
            "weaknesses": ["LLM analysis temporarily unavailable"],
            "model_used": "real-local-llm-fallback",
            "similarity_score": 0.5
        }
