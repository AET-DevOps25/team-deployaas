// API Configuration
// Check if we're running in development (localhost) or production (Kubernetes)
const isDevelopment = import.meta.env.MODE === 'development' && window.location.hostname === 'localhost';

// In development, use localhost with ports for direct access
// In production/Kubernetes, use relative URLs that will be proxied by nginx
export const apiBaseUrl = {
  quiz: isDevelopment ? 'http://localhost:8081/api/quiz' : '/api/quiz',
  flashcard: isDevelopment ? 'http://localhost:8082/api/flashcard' : '/api/flashcard',
  auth: isDevelopment ? 'http://localhost:8083/api/auth' : '/api/auth',
};

export default apiBaseUrl;