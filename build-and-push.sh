#!/bin/bash

# Team DeployAAS - Docker Image Build and Push Script
# This script builds and pushes all Docker images for the study assistant application

set -e

# Configuration
REGISTRY="ghcr.io/aet-devops25/team-deployaas"
TAG="latest"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Navigate to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Check if we're in the right directory
if [ ! -f "docker-compose.yml" ]; then
    print_error "docker-compose.yml not found! Current directory: $(pwd)"
    print_error "Expected to be in the project root with docker-compose.yml"
    exit 1
fi

# Function to build and push a service
build_and_push_service() {
    local service_name=$1
    local dockerfile_path=$2
    local image_name="${REGISTRY}/${service_name}:${TAG}"
    
    print_status "Building ${service_name}..."
    
    if [ ! -d "$dockerfile_path" ]; then
        print_error "Directory $dockerfile_path not found!"
        return 1
    fi
    
    # Build the image for multiple platforms with no cache
    docker buildx build --no-cache --platform linux/amd64,linux/arm64 -t "$image_name" "$dockerfile_path" --push
    
    if [ $? -eq 0 ]; then
        print_status "Successfully built and pushed ${image_name}"
    else
        print_error "Failed to build ${image_name}"
        return 1
    fi
}

# Check Docker login
print_status "Checking Docker registry login..."
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running!"
    exit 1
fi

# Login to GitHub Container Registry
print_status "Logging into GitHub Container Registry..."

# Check if already logged in
if docker pull ghcr.io/hello-world > /dev/null 2>&1; then
    print_status "Already logged into GitHub Container Registry"
else
    print_warning "Please login to GitHub Container Registry:"
    print_status "Run: docker login ghcr.io -u YOUR_GITHUB_USERNAME"
    print_status "Use your Personal Access Token as the password"
    
    read -p "Press Enter after you've logged in, or Ctrl+C to exit..."
fi

# Build and push all services
print_status "Building and pushing all services..."

# Build client (Vue.js frontend)
build_and_push_service "client" "frontend/client-vue"

# Build quiz service
build_and_push_service "quiz-service" "quiz-service"

# Build flashcard service
build_and_push_service "flashcard-service" "flashcard-service"

# Build auth service
build_and_push_service "auth-service" "auth-service"

# Build genai service
build_and_push_service "genai-service" "genai-service"

print_status "All services built and pushed successfully!"
print_status "Images are now available at:"
echo "  - ${REGISTRY}/client:${TAG}"
echo "  - ${REGISTRY}/quiz-service:${TAG}"
echo "  - ${REGISTRY}/flashcard-service:${TAG}"
echo "  - ${REGISTRY}/auth-service:${TAG}"
echo "  - ${REGISTRY}/genai-service:${TAG}"

print_warning "Make sure these images are public or configure image pull secrets in Kubernetes!"
