// API configuration that works in both development and production
// We need to detect environment at runtime, not build time
const isDevelopment = () => {
  // In development, we're typically on localhost
  // In production, we're on the actual server IP
  return window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
};

// In development, use the proxy (which forwards to localhost services)
// In production, use the current host with the service ports
const getApiBaseUrl = (service) => {
  if (isDevelopment()) {
    // In development, use the proxy paths
    return '/api';
  } else {
    // In production, use the current hostname (EC2 instance) with service ports
    const host = window.location.hostname;
    
    const servicePorts = {
      quiz: '8081',
      flashcard: '8082',
      auth: '8083',
      genai: '8084'
    };
    
    return `http://${host}:${servicePorts[service]}`;
  }
};

export const API_ENDPOINTS = {
  quiz: `${getApiBaseUrl('quiz')}/api/quiz`,
  flashcard: `${getApiBaseUrl('flashcard')}/api/flashcard`,
  auth: `${getApiBaseUrl('auth')}/api/auth`,
  genai: `${getApiBaseUrl('genai')}/api/genai`
};

// For backwards compatibility in development mode
export const getQuizApiUrl = () => isDevelopment() ? '/api/quiz' : API_ENDPOINTS.quiz;
export const getFlashcardApiUrl = () => isDevelopment() ? '/api/flashcard' : API_ENDPOINTS.flashcard;
export const getAuthApiUrl = () => isDevelopment() ? '/api/auth' : API_ENDPOINTS.auth;
