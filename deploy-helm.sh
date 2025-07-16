#!/bin/bash

# Study Assistant Helm Deployment Script
# This script deploys the Study Assistant application to Kubernetes

set -e  # Exit on any error

# Configuration
NAMESPACE="${NAMESPACE:-dev}"
RELEASE_NAME="${RELEASE_NAME:-study-assistant}"
CHART_PATH="./helm/study-assistant"
VALUES_FILE="${VALUES_FILE:-values.yaml}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Deploying Study Assistant to Kubernetes${NC}"
echo "Namespace: $NAMESPACE"
echo "Release: $RELEASE_NAME"
echo "Chart: $CHART_PATH"
echo "Values: $VALUES_FILE"
echo ""

# Check if kubectl is configured
if ! kubectl cluster-info >/dev/null 2>&1; then
    echo -e "${RED}❌ kubectl is not configured or cluster is not accessible${NC}"
    echo "Please ensure you have a valid kubeconfig and cluster access"
    exit 1
fi

# Check if namespace exists, create if it doesn't
if kubectl get namespace "$NAMESPACE" >/dev/null 2>&1; then
    echo -e "${GREEN}✅ Namespace '$NAMESPACE' already exists${NC}"
else
    echo -e "${YELLOW}📦 Creating namespace '$NAMESPACE'${NC}"
    kubectl create namespace "$NAMESPACE"
fi

# Check if Helm is installed
if ! command -v helm >/dev/null 2>&1; then
    echo -e "${RED}❌ Helm is not installed${NC}"
    echo "Please install Helm: https://helm.sh/docs/intro/install/"
    exit 1
fi

# Validate Helm chart
echo -e "${YELLOW}🔍 Validating Helm chart...${NC}"
if ! helm lint "$CHART_PATH"; then
    echo -e "${RED}❌ Helm chart validation failed${NC}"
    exit 1
fi

# Check if release already exists
if helm list -n "$NAMESPACE" | grep -q "$RELEASE_NAME"; then
    echo -e "${YELLOW}🔄 Release '$RELEASE_NAME' exists. Upgrading...${NC}"
    helm upgrade "$RELEASE_NAME" "$CHART_PATH" \
        --namespace "$NAMESPACE" \
        --values "$CHART_PATH/$VALUES_FILE" \
        --wait \
        --timeout=600s
else
    echo -e "${YELLOW}🆕 Installing new release '$RELEASE_NAME'...${NC}"
    helm install "$RELEASE_NAME" "$CHART_PATH" \
        --namespace "$NAMESPACE" \
        --values "$CHART_PATH/$VALUES_FILE" \
        --wait \
        --timeout=600s \
        --create-namespace
fi

# Check deployment status
echo -e "${YELLOW}🔍 Checking deployment status...${NC}"
kubectl get pods -n "$NAMESPACE" -l "app.kubernetes.io/instance=$RELEASE_NAME"

# Wait for all pods to be ready
echo -e "${YELLOW}⏳ Waiting for all pods to be ready...${NC}"
kubectl wait --for=condition=Ready pods -l "app.kubernetes.io/instance=$RELEASE_NAME" -n "$NAMESPACE" --timeout=300s

# Show services
echo -e "${GREEN}🌐 Services:${NC}"
kubectl get services -n "$NAMESPACE" -l "app.kubernetes.io/instance=$RELEASE_NAME"

# Show ingress (if enabled)
if kubectl get ingress -n "$NAMESPACE" -l "app.kubernetes.io/instance=$RELEASE_NAME" >/dev/null 2>&1; then
    echo -e "${GREEN}🌍 Ingress:${NC}"
    kubectl get ingress -n "$NAMESPACE" -l "app.kubernetes.io/instance=$RELEASE_NAME"
fi

echo ""
echo -e "${GREEN}✅ Deployment completed successfully!${NC}"
echo ""
echo -e "${YELLOW}📝 Useful commands:${NC}"
echo "  Check pods:     kubectl get pods -n $NAMESPACE"
echo "  Check services: kubectl get services -n $NAMESPACE"
echo "  Check logs:     kubectl logs -f deployment/study-assistant-client -n $NAMESPACE"
echo "  Port forward:   kubectl port-forward service/study-assistant-client 8080:80 -n $NAMESPACE"
echo ""

# If ingress is enabled, show access instructions
if kubectl get ingress -n "$NAMESPACE" "study-assistant-ingress" >/dev/null 2>&1; then
    INGRESS_HOST=$(kubectl get ingress -n "$NAMESPACE" "study-assistant-ingress" -o jsonpath='{.spec.rules[0].host}')
    echo -e "${GREEN}🌐 Access your application:${NC}"
    echo "  URL: http://$INGRESS_HOST"
    echo "  Note: Add '$INGRESS_HOST' to your /etc/hosts file pointing to your cluster IP if using a local cluster"
else
    echo -e "${YELLOW}💡 To access the application locally:${NC}"
    echo "  kubectl port-forward service/study-assistant-client 8080:80 -n $NAMESPACE"
    echo "  Then open http://localhost:8080"
fi
