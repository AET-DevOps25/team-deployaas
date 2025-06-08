# 🧱 Stage 1: Build React app
FROM node:19-alpine AS builder

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy all source code and build
COPY . .
RUN npm run build

# 🚀 Stage 2: Serve using Nginx
FROM nginx:alpine

# Copy build files
COPY --from=builder /app/build /usr/share/nginx/html

# Copy custom nginx config (optional)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Nginx serves on port 80 by default; expose if needed for local dev
EXPOSE 80

# Start Nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
