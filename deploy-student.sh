#!/bin/bash

# Study Assistant Minimal Deployment for Student Environment
# This script deploys the app with minimal permissions - no ConfigMaps, Secrets, etc.

set -e

# Configuration
NAMESPACE="teamdeployaas-studyapp"
KUBECONFIG_PATH="./student-kubeconfig.yaml"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_status "🚀 Study Assistant Minimal Deployment"
print_status "====================================="

# Set kubeconfig
if [ ! -f "$KUBECONFIG_PATH" ]; then
    print_error "Kubeconfig file not found at $KUBECONFIG_PATH"
    exit 1
fi

export KUBECONFIG="$KUBECONFIG_PATH"
print_status "Using kubeconfig: $KUBECONFIG_PATH"

# Test connection
print_status "Testing Kubernetes connection..."
if ! kubectl cluster-info > /dev/null 2>&1; then
    print_error "Cannot connect to Kubernetes cluster"
    exit 1
fi
print_status "✅ Connected to Kubernetes cluster"

# Create namespace
print_status "Creating namespace $NAMESPACE..."
kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f - || {
    print_error "Failed to create namespace. Check if you have permission to create namespaces."
    exit 1
}
print_status "✅ Namespace $NAMESPACE ready"

# Create minimal deployment manifests
print_status "Creating minimal deployment manifests..."

# PostgreSQL deployment
cat > postgres-deployment.yaml << 'EOF'
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  labels:
    app: postgres
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:14
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_DB
          value: "studyassistant"
        - name: POSTGRES_USER
          value: "studyassistant"
        - name: POSTGRES_PASSWORD
          value: "changeme123"
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: postgres
spec:
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
EOF

# Auth Service deployment
cat > auth-service-deployment.yaml << 'EOF'
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
  labels:
    app: auth-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
    spec:
      containers:
      - name: auth-service
        image: ghcr.io/aet-devops25/team-deployaas/auth-service:latest
        ports:
        - containerPort: 8083
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "dev"
        - name: DATABASE_HOST
          value: "postgres"
        - name: DATABASE_PORT
          value: "5432"
        - name: DATABASE_NAME
          value: "studyassistant"
        - name: DATABASE_USERNAME
          value: "studyassistant"
        - name: DATABASE_PASSWORD
          value: "changeme123"
        - name: JWT_SECRET
          value: "your-jwt-secret-key-change-me-in-production"
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: auth-service
spec:
  selector:
    app: auth-service
  ports:
  - port: 8083
    targetPort: 8083
EOF

# Quiz Service deployment
cat > quiz-service-deployment.yaml << 'EOF'
apiVersion: apps/v1
kind: Deployment
metadata:
  name: quiz-service
  labels:
    app: quiz-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: quiz-service
  template:
    metadata:
      labels:
        app: quiz-service
    spec:
      containers:
      - name: quiz-service
        image: ghcr.io/aet-devops25/team-deployaas/quiz-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "dev"
        - name: DATABASE_HOST
          value: "postgres"
        - name: DATABASE_PORT
          value: "5432"
        - name: DATABASE_NAME
          value: "studyassistant"
        - name: DATABASE_USERNAME
          value: "studyassistant"
        - name: DATABASE_PASSWORD
          value: "changeme123"
        - name: AUTH_SERVICE_URL
          value: "http://auth-service:8083"
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: quiz-service
spec:
  selector:
    app: quiz-service
  ports:
  - port: 8081
    targetPort: 8081
EOF

# Flashcard Service deployment
cat > flashcard-service-deployment.yaml << 'EOF'
apiVersion: apps/v1
kind: Deployment
metadata:
  name: flashcard-service
  labels:
    app: flashcard-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: flashcard-service
  template:
    metadata:
      labels:
        app: flashcard-service
    spec:
      containers:
      - name: flashcard-service
        image: ghcr.io/aet-devops25/team-deployaas/flashcard-service:latest
        ports:
        - containerPort: 8082
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "dev"
        - name: DATABASE_HOST
          value: "postgres"
        - name: DATABASE_PORT
          value: "5432"
        - name: DATABASE_NAME
          value: "studyassistant"
        - name: DATABASE_USERNAME
          value: "studyassistant"
        - name: DATABASE_PASSWORD
          value: "changeme123"
        - name: AUTH_SERVICE_URL
          value: "http://auth-service:8083"
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: flashcard-service
spec:
  selector:
    app: flashcard-service
  ports:
  - port: 8082
    targetPort: 8082
EOF

# GenAI Service deployment
cat > genai-service-deployment.yaml << 'EOF'
apiVersion: apps/v1
kind: Deployment
metadata:
  name: genai-service
  labels:
    app: genai-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: genai-service
  template:
    metadata:
      labels:
        app: genai-service
    spec:
      containers:
      - name: genai-service
        image: ghcr.io/aet-devops25/team-deployaas/genai-service:latest
        ports:
        - containerPort: 5001
        env:
        - name: WEBUI_API_KEY
          value: "your-webui-api-key-here"
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: genai-service
spec:
  selector:
    app: genai-service
  ports:
  - port: 5001
    targetPort: 5001
EOF

# Frontend deployment
cat > frontend-deployment.yaml << 'EOF'
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend
  labels:
    app: frontend
spec:
  replicas: 1
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
    spec:
      containers:
      - name: frontend
        image: ghcr.io/aet-devops25/team-deployaas/client-vue:latest
        ports:
        - containerPort: 80
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
---
apiVersion: v1
kind: Service
metadata:
  name: frontend
spec:
  selector:
    app: frontend
  ports:
  - port: 80
    targetPort: 80
  type: NodePort
EOF

print_status "✅ Deployment manifests created"

# Apply deployments
print_status "Deploying PostgreSQL..."
kubectl apply -f postgres-deployment.yaml -n "$NAMESPACE"

print_status "Deploying Auth Service..."
kubectl apply -f auth-service-deployment.yaml -n "$NAMESPACE"

print_status "Deploying Quiz Service..."
kubectl apply -f quiz-service-deployment.yaml -n "$NAMESPACE"

print_status "Deploying Flashcard Service..."
kubectl apply -f flashcard-service-deployment.yaml -n "$NAMESPACE"

print_status "Deploying GenAI Service..."
kubectl apply -f genai-service-deployment.yaml -n "$NAMESPACE"

print_status "Deploying Frontend..."
kubectl apply -f frontend-deployment.yaml -n "$NAMESPACE"

# Wait for deployments
print_status "Waiting for deployments to be ready..."
kubectl wait --for=condition=available deployment/postgres -n "$NAMESPACE" --timeout=300s || print_warning "PostgreSQL deployment timeout"
kubectl wait --for=condition=available deployment/auth-service -n "$NAMESPACE" --timeout=300s || print_warning "Auth service deployment timeout"
kubectl wait --for=condition=available deployment/quiz-service -n "$NAMESPACE" --timeout=300s || print_warning "Quiz service deployment timeout"
kubectl wait --for=condition=available deployment/flashcard-service -n "$NAMESPACE" --timeout=300s || print_warning "Flashcard service deployment timeout"
kubectl wait --for=condition=available deployment/genai-service -n "$NAMESPACE" --timeout=300s || print_warning "GenAI service deployment timeout"
kubectl wait --for=condition=available deployment/frontend -n "$NAMESPACE" --timeout=300s || print_warning "Frontend deployment timeout"

# Show status
print_status "📊 Deployment Status"
echo ""
echo "Pods:"
kubectl get pods -n "$NAMESPACE"
echo ""
echo "Services:"
kubectl get services -n "$NAMESPACE"

# Get frontend service details
FRONTEND_NODEPORT=$(kubectl get service frontend -n "$NAMESPACE" -o jsonpath='{.spec.ports[0].nodePort}')
print_status "✅ Deployment completed!"
print_status "Frontend is available on NodePort: $FRONTEND_NODEPORT"
print_status "To access the app, you may need to setup port forwarding:"
echo "kubectl port-forward service/frontend 8080:80 -n $NAMESPACE"

# Cleanup temp files
rm -f postgres-deployment.yaml auth-service-deployment.yaml quiz-service-deployment.yaml
rm -f flashcard-service-deployment.yaml genai-service-deployment.yaml frontend-deployment.yaml

print_status "🎉 Study Assistant deployed successfully with minimal permissions!"