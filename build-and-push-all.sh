#!/bin/bash

# Build and push all Docker images for team-deployaas
# Registry: ghcr.io/aet-devops25/team-deployaas/
# Uses --no-cache to ensure fresh builds with latest changes

set -e

REGISTRY="ghcr.io/aet-devops25/team-deployaas"

echo "🔄 Building and pushing all Docker images to $REGISTRY (no cache for fresh builds)"

# Build frontend/client
echo "🏗️  Building frontend client..."
cd frontend/client-vue
docker buildx build --no-cache --platform linux/amd64,linux/arm64 -t $REGISTRY/client:latest . --push
cd ../..

# Build auth-service
echo "🏗️  Building auth-service..."
cd auth-service
docker buildx build --no-cache --platform linux/amd64,linux/arm64 -t $REGISTRY/auth-service:latest . --push
cd ..

# Build quiz-service
echo "🏗️  Building quiz-service..."
cd quiz-service
docker buildx build --no-cache --platform linux/amd64,linux/arm64 -t $REGISTRY/quiz-service:latest . --push
cd ..

# Build flashcard-service
echo "🏗️  Building flashcard-service..."
cd flashcard-service
docker buildx build --no-cache --platform linux/amd64,linux/arm64 -t $REGISTRY/flashcard-service:latest . --push
cd ..

# Build genai-service
echo "🏗️  Building genai-service..."
cd genai-service
docker buildx build --no-cache --platform linux/amd64,linux/arm64 -t $REGISTRY/genai-service:latest . --push
cd ..

echo "✅ All Docker images built and pushed successfully!"
echo "📋 Images built:"
echo "   - $REGISTRY/client:latest"
echo "   - $REGISTRY/auth-service:latest"
echo "   - $REGISTRY/quiz-service:latest"
echo "   - $REGISTRY/flashcard-service:latest"
echo "   - $REGISTRY/genai-service:latest"
