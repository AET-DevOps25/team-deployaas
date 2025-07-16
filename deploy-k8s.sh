#!/bin/bash

# Study Assistant Helm Deployment Script
# This script deploys the Study Assistant application to Rancher using the student kubeconfig

set -e

# Configuration
NAMESPACE="dev"
RELEASE_NAME="studyapp"
CHART_PATH="./helm/study-assistant"
KUBECONFIG_PATH="./helm/study-assistant/student.yaml"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if required files exist
if [ ! -f "$KUBECONFIG_PATH" ]; then
    print_error "Kubeconfig file not found at $KUBECONFIG_PATH"
    exit 1
fi

if [ ! -d "$CHART_PATH" ]; then
    print_error "Helm chart not found at $CHART_PATH"
    exit 1
fi

# Export kubeconfig
export KUBECONFIG="$KUBECONFIG_PATH"

print_status "Using kubeconfig: $KUBECONFIG_PATH"
print_status "Deploying to namespace: $NAMESPACE"
print_status "Release name: $RELEASE_NAME"

# Check if kubectl is working
print_status "Testing Kubernetes connection..."
if ! kubectl cluster-info > /dev/null 2>&1; then
    print_error "Cannot connect to Kubernetes cluster. Check your kubeconfig."
    exit 1
fi

print_status "Successfully connected to Kubernetes cluster"

# Check if Helm is installed
if ! command -v helm &> /dev/null; then
    print_error "Helm is not installed. Please install Helm first."
    exit 1
fi

# Validate Helm chart
print_status "Validating Helm chart..."
if ! helm lint "$CHART_PATH"; then
    print_error "Helm chart validation failed"
    exit 1
fi

print_status "Helm chart validation passed"

# Create namespace if it doesn't exist
print_status "Creating namespace $NAMESPACE if it doesn't exist..."
kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

# Check if release already exists
if helm list -n "$NAMESPACE" | grep -q "$RELEASE_NAME"; then
    print_warning "Release $RELEASE_NAME already exists. Upgrading..."
    HELM_COMMAND="upgrade"
else
    print_status "Installing new release $RELEASE_NAME..."
    HELM_COMMAND="install"
fi

# Deploy/Upgrade the application
print_status "Deploying Study Assistant application..."
helm $HELM_COMMAND "$RELEASE_NAME" "$CHART_PATH" \
    --namespace "$NAMESPACE" \
    --create-namespace \
    --wait \
    --timeout 10m \
    --values "$CHART_PATH/values.yaml" \
    --values "$CHART_PATH/values-dev.yaml"

if [ $? -eq 0 ]; then
    print_status "Deployment successful!"
    
    # Show deployment status
    print_status "Checking deployment status..."
    kubectl get pods -n "$NAMESPACE" -l "app.kubernetes.io/instance=$RELEASE_NAME"
    
    print_status "Checking services..."
    kubectl get services -n "$NAMESPACE" -l "app.kubernetes.io/instance=$RELEASE_NAME"
    
    print_status "To check logs, use:"
    echo "kubectl logs -n $NAMESPACE -l app.kubernetes.io/instance=$RELEASE_NAME -f"
    
    print_status "To port-forward to the frontend:"
    echo "kubectl port-forward -n $NAMESPACE svc/$RELEASE_NAME-study-assistant-client 3000:80"
    
else
    print_error "Deployment failed!"
    exit 1
fi
