#!/bin/bash

# Helm Deployment Script for Study Assistant
# This script demonstrates secure deployment approaches

set -e

NAMESPACE="teamdeployaas-studyapp"
RELEASE_NAME="teamdeployaas-study-assistant"
CHART_PATH="./helm/study-assistant"

echo "🚀 Study Assistant Helm Deployment"
echo "=================================="

# Function to check if kubectl is configured
check_kubectl() {
    if ! kubectl auth can-i create namespaces --request-timeout=10s &> /dev/null; then
        echo "❌ kubectl is not configured or cluster is unreachable"
        echo "💡 Make sure to set KUBECONFIG to your cluster config file"
        exit 1
    fi
    echo "✅ Kubectl configured and cluster reachable"
}

# Function to create namespace if it doesn't exist
create_namespace() {
    if kubectl get namespace "$NAMESPACE" &> /dev/null; then
        echo "✅ Namespace '$NAMESPACE' already exists"
    else
        echo "📝 Creating namespace '$NAMESPACE'"
        kubectl create namespace "$NAMESPACE"
        echo "✅ Namespace '$NAMESPACE' created successfully"
    fi
}

# Function for development deployment (less secure but easier)
deploy_dev() {
    echo "🔧 Development Deployment (secrets in values)"
    echo "⚠️  WARNING: This includes secrets in the Helm template - only for development!"
    
    helm upgrade --install "$RELEASE_NAME" "$CHART_PATH" \
        --namespace "$NAMESPACE" \
        --create-namespace \
        --values "$CHART_PATH/values.yaml" \
        --set secrets.createInTemplate=true \
        --timeout 10m \
        --wait
}

# Function for production deployment (more secure)
deploy_prod() {
    echo "🔒 Production Deployment (external secrets)"
    echo "📋 Prerequisites:"
    echo "   1. Create secrets manually or via external secret management"
    echo "   2. Ensure secrets exist in namespace '$NAMESPACE'"
    
    # Check if secrets exist
    if ! kubectl get secret "${RELEASE_NAME}-secrets" -n "$NAMESPACE" &> /dev/null; then
        echo "❌ Secret '${RELEASE_NAME}-secrets' not found in namespace '$NAMESPACE'"
        echo "💡 Create secrets first using:"
        echo "   kubectl apply -f secrets.yaml"
        echo "   Or use the secrets-example.yaml as a template"
        exit 1
    fi
    
    echo "✅ Found existing secrets"
    
    helm upgrade --install "$RELEASE_NAME" "$CHART_PATH" \
        --namespace "$NAMESPACE" \
        --values "$CHART_PATH/values.yaml" \
        --set secrets.createInTemplate=false \
        --timeout 10m
}

# Function to generate secure secrets
generate_secrets() {
    echo "🔐 Generating secure secrets template"
    
    DB_PASSWORD=$(openssl rand -base64 32)
    JWT_SECRET=$(openssl rand -base64 64)
    
    cat > secrets.yaml << EOF
apiVersion: v1
kind: Secret
metadata:
  name: ${RELEASE_NAME}-secrets
  namespace: ${NAMESPACE}
type: Opaque
data:
  DATABASE_USERNAME: $(echo -n "studyassistant" | base64)
  DATABASE_PASSWORD: $(echo -n "$DB_PASSWORD" | base64)
  JWT_SECRET: $(echo -n "$JWT_SECRET" | base64)
  WEBUI_API_KEY: $(echo -n "your-webui-api-key-here" | base64)
EOF
    
    echo "✅ Generated secrets.yaml with secure random values"
    echo "💡 Edit secrets.yaml to set your actual WEBUI_API_KEY"
    echo "💡 Apply with: kubectl apply -f secrets.yaml"
}

# Function to check deployment status
check_deployment() {
    echo "📊 Checking deployment status..."
    
    echo "Pods:"
    kubectl get pods -n "$NAMESPACE" -l app.kubernetes.io/instance="$RELEASE_NAME" || echo "No pods found or access denied"
    
    echo -e "\nServices:"
    kubectl get services -n "$NAMESPACE" -l app.kubernetes.io/instance="$RELEASE_NAME" || echo "No services found or access denied"
    
    echo -e "\nIngress:"
    kubectl get ingress -n "$NAMESPACE" -l app.kubernetes.io/instance="$RELEASE_NAME" 2>/dev/null || echo "No ingress found or access denied"
}

# Function to show logs
show_logs() {
    echo "📋 Recent logs from quiz-service:"
    kubectl logs -n "$NAMESPACE" -l app.kubernetes.io/component=quiz-service --tail=50
}

# Main script logic
case "${1:-dev}" in
    "dev"|"development")
        check_kubectl
        create_namespace
        deploy_dev
        check_deployment
        ;;
    "prod"|"production")
        check_kubectl
        create_namespace
        deploy_prod
        check_deployment
        ;;
    "secrets")
        generate_secrets
        ;;
    "status")
        check_kubectl
        check_deployment
        ;;
    "logs")
        check_kubectl
        show_logs
        ;;
    "clean")
        echo "🧹 Cleaning up deployment"
        helm uninstall "$RELEASE_NAME" -n "$NAMESPACE" || true
        kubectl delete namespace "$NAMESPACE" || true
        ;;
    *)
        echo "Usage: $0 [dev|prod|secrets|status|logs|clean]"
        echo ""
        echo "Commands:"
        echo "  dev      - Deploy with secrets in template (development only)"
        echo "  prod     - Deploy with external secrets (production)"
        echo "  secrets  - Generate secure secrets template"
        echo "  status   - Check deployment status"
        echo "  logs     - Show recent logs"
        echo "  clean    - Remove deployment and namespace"
        echo ""
        echo "Examples:"
        echo "  $0 dev                    # Quick development deployment"
        echo "  $0 secrets && $0 prod     # Secure production deployment"
        exit 1
        ;;
esac
