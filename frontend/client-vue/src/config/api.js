// API Configuration
const isDevelopment = import.meta.env.MODE === 'development';

// In development, use localhost with ports for direct access
// In production, use relative URLs that will be proxied by nginx
export const apiBaseUrl = {
  quiz: isDevelopment ? 'http://localhost:8081/api/quiz' : '/api/quiz',
  flashcard: isDevelopment ? 'http://localhost:8082/api/flashcard' : '/api/flashcard',
  auth: isDevelopment ? 'http://localhost:8083/api/auth' : '/api/auth',
};

export default apiBaseUrl;