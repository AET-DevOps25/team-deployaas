#!/bin/bash

# Team DeployAAS - Study Assistant Deployment Script
# This script helps deploy the Study Assistant application to Kubernetes

set -e

# Configuration
NAMESPACE="dev"  # Using dev namespace
RELEASE_NAME="study-assistant"
CHART_PATH="."

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

# Function to check if namespace exists
check_namespace() {
    if ! kubectl get namespace "$NAMESPACE" &> /dev/null; then
        print_error "Namespace '$NAMESPACE' does not exist!"
        print_status "Creating namespace '$NAMESPACE'..."
        kubectl create namespace "$NAMESPACE"
    else
        print_status "Namespace '$NAMESPACE' exists."
    fi
}

# Function to check if secrets exist
check_secrets() {
    print_status "Checking required secrets..."
    
    if ! kubectl get secret postgres-secret -n "$NAMESPACE" &> /dev/null; then
        print_warning "postgres-secret not found!"
        print_status "Creating example postgres-secret..."
        kubectl create secret generic postgres-secret \
            --from-literal=username=postgres \
            --from-literal=password=changeme123 \
            -n "$NAMESPACE"
        print_warning "Please update the postgres password in the secret!"
    fi
    
    if ! kubectl get secret genai-secret -n "$NAMESPACE" &> /dev/null; then
        print_warning "genai-secret not found!"
        print_status "Creating example genai-secret..."
        kubectl create secret generic genai-secret \
            --from-literal=WEBUI_API_KEY=your-api-key-here \
            -n "$NAMESPACE"
        print_warning "Please update the GenAI API key in the secret!"
    fi
}

# Function to deploy the application
deploy_app() {
    print_status "Deploying Study Assistant application..."
    
    # Update dependencies if needed
    if [ -f "Chart.yaml" ]; then
        helm dependency update
    fi
    
    # Deploy the application
    helm upgrade --install "$RELEASE_NAME" "$CHART_PATH" \
        --namespace "$NAMESPACE" \
        --timeout 10m \
        --wait
    
    if [ $? -eq 0 ]; then
        print_status "Deployment successful!"
    else
        print_error "Deployment failed!"
        exit 1
    fi
}

# Function to show deployment status
show_status() {
    print_status "Deployment Status:"
    echo ""
    
    print_status "Pods:"
    kubectl get pods -n "$NAMESPACE" -l app.kubernetes.io/instance="$RELEASE_NAME"
    echo ""
    
    print_status "Services:"
    kubectl get svc -n "$NAMESPACE" -l app.kubernetes.io/instance="$RELEASE_NAME"
    echo ""
    
    print_status "Ingress:"
    kubectl get ingress -n "$NAMESPACE" -l app.kubernetes.io/instance="$RELEASE_NAME"
    echo ""
    
    # Get the application URL
    INGRESS_HOST=$(kubectl get ingress -n "$NAMESPACE" -l app.kubernetes.io/instance="$RELEASE_NAME" -o jsonpath='{.items[0].spec.rules[0].host}' 2>/dev/null)
    if [ -n "$INGRESS_HOST" ]; then
        print_status "Application URL: https://$INGRESS_HOST"
    fi
}

# Main execution
main() {
    print_status "Starting deployment of Team DeployAAS Study Assistant..."
    
    # Check if we're in the right directory
    if [ ! -f "Chart.yaml" ]; then
        print_error "Chart.yaml not found! Please run this script from the helm chart directory."
        exit 1
    fi
    
    # Check kubectl connection
    if ! kubectl cluster-info &> /dev/null; then
        print_error "Unable to connect to Kubernetes cluster!"
        exit 1
    fi
    
    check_namespace
    check_secrets
    deploy_app
    show_status
    
    print_status "Deployment completed successfully!"
    print_warning "Don't forget to update the secrets with real values!"
}

# Handle command line arguments
case "${1:-deploy}" in
    "deploy")
        main
        ;;
    "status")
        show_status
        ;;
    "cleanup")
        print_status "Cleaning up deployment..."
        helm uninstall "$RELEASE_NAME" -n "$NAMESPACE"
        print_status "Cleanup completed!"
        ;;
    *)
        echo "Usage: $0 [deploy|status|cleanup]"
        echo "  deploy  - Deploy the application (default)"
        echo "  status  - Show deployment status"
        echo "  cleanup - Remove the deployment"
        exit 1
        ;;
esac
