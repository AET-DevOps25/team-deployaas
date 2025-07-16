#!/bin/bash

# Study Assistant Deployment Script
set -e

echo "🚀 Study Assistant Deployment Script"
echo "======================================"

# Configuration
NAMESPACE="devops25-team-deployaas"
HELM_RELEASE="study-assistant"
HELM_CHART="./helm/study-assistant"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    echo "🔍 Checking prerequisites..."
    
    if ! command -v kubectl &> /dev/null; then
        print_error "kubectl is not installed"
        exit 1
    fi
    print_status "kubectl is available"
    
    if ! command -v helm &> /dev/null; then
        print_error "helm is not installed"
        exit 1
    fi
    print_status "helm is available"
    
    # Check cluster connection
    if ! kubectl cluster-info &> /dev/null; then
        print_error "Cannot connect to Kubernetes cluster"
        exit 1
    fi
    print_status "Connected to Kubernetes cluster"
}

# Deploy function
deploy() {
    echo "📦 Deploying Study Assistant..."
    
    # Check if release exists
    if helm list -q | grep -q "^${HELM_RELEASE}$"; then
        print_warning "Release ${HELM_RELEASE} exists. Upgrading..."
        helm upgrade ${HELM_RELEASE} ${HELM_CHART} --namespace ${NAMESPACE}
    else
        print_status "Installing new release..."
        helm install ${HELM_RELEASE} ${HELM_CHART} --namespace ${NAMESPACE} --create-namespace
    fi
    
    print_status "Deployment initiated"
}

# Wait for deployment
wait_for_deployment() {
    echo "⏳ Waiting for deployments to be ready..."
    
    deployments=(
        "study-assistant-postgres"
        "study-assistant-auth-service" 
        "study-assistant-quiz-service"
        "study-assistant-flashcard-service"
        "study-assistant-genai-service"
        "study-assistant-client"
        "study-assistant-pgadmin"
    )
    
    for deployment in "${deployments[@]}"; do
        echo "Waiting for ${deployment}..."
        kubectl rollout status deployment/${deployment} -n ${NAMESPACE} --timeout=300s
        print_status "${deployment} is ready"
    done
}

# Show deployment status
show_status() {
    echo "📊 Deployment Status"
    echo "===================="
    
    echo ""
    echo "Pods:"
    kubectl get pods -n ${NAMESPACE}
    
    echo ""
    echo "Services:"
    kubectl get svc -n ${NAMESPACE}
    
    echo ""
    echo "Ingress:"
    kubectl get ingress -n ${NAMESPACE}
    
    echo ""
    print_status "Application URLs:"
    echo "  Main App: https://study-assistant.devops25-team-deployaas.rancher.ase.cit.tum.de"
    echo "  Auth API: https://auth.devops25-team-deployaas.rancher.ase.cit.tum.de"
    echo "  Quiz API: https://quiz.devops25-team-deployaas.rancher.ase.cit.tum.de"
    echo "  Flashcard API: https://flashcard.devops25-team-deployaas.rancher.ase.cit.tum.de"
    echo "  pgAdmin: https://pgadmin.devops25-team-deployaas.rancher.ase.cit.tum.de"
}

# Main execution
case "${1:-deploy}" in
    "check")
        check_prerequisites
        ;;
    "deploy")
        check_prerequisites
        deploy
        wait_for_deployment
        show_status
        ;;
    "status")
        show_status
        ;;
    "logs")
        service=${2:-auth-service}
        echo "📝 Showing logs for study-assistant-${service}..."
        kubectl logs -f deployment/study-assistant-${service} -n ${NAMESPACE}
        ;;
    "clean")
        print_warning "Removing Study Assistant deployment..."
        helm uninstall ${HELM_RELEASE} --namespace ${NAMESPACE}
        print_status "Deployment removed"
        ;;
    *)
        echo "Usage: $0 {deploy|check|status|logs|clean}"
        echo ""
        echo "Commands:"
        echo "  deploy  - Deploy the application (default)"
        echo "  check   - Check prerequisites only"
        echo "  status  - Show deployment status"
        echo "  logs    - Show logs (specify service: auth-service, quiz-service, etc.)"
        echo "  clean   - Remove the deployment"
        exit 1
        ;;
esac
