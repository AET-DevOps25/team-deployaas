#!/bin/bash

# Health check script for Study Assistant deployment
set -e

NAMESPACE="devops25-team-deployaas"
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "🔍 Study Assistant Health Check"
echo "==============================="

# Check if namespace exists
if ! kubectl get namespace $NAMESPACE &> /dev/null; then
    echo -e "${RED}✗${NC} Namespace $NAMESPACE does not exist"
    exit 1
fi
echo -e "${GREEN}✓${NC} Namespace $NAMESPACE exists"

# Check deployments
deployments=(
    "study-assistant-postgres"
    "study-assistant-auth-service" 
    "study-assistant-quiz-service"
    "study-assistant-flashcard-service"
    "study-assistant-genai-service"
    "study-assistant-client"
    "study-assistant-pgadmin"
)

echo ""
echo "📊 Checking Deployments:"
for deployment in "${deployments[@]}"; do
    if kubectl get deployment $deployment -n $NAMESPACE &> /dev/null; then
        ready=$(kubectl get deployment $deployment -n $NAMESPACE -o jsonpath='{.status.readyReplicas}')
        desired=$(kubectl get deployment $deployment -n $NAMESPACE -o jsonpath='{.spec.replicas}')
        
        if [ "$ready" = "$desired" ]; then
            echo -e "${GREEN}✓${NC} $deployment ($ready/$desired ready)"
        else
            echo -e "${YELLOW}⚠${NC} $deployment ($ready/$desired ready)"
        fi
    else
        echo -e "${RED}✗${NC} $deployment (not found)"
    fi
done

# Check services
echo ""
echo "🌐 Checking Services:"
services=(
    "study-assistant-postgres"
    "study-assistant-auth-service"
    "study-assistant-quiz-service" 
    "study-assistant-flashcard-service"
    "study-assistant-genai-service"
    "study-assistant-client"
    "study-assistant-pgadmin"
)

for service in "${services[@]}"; do
    if kubectl get service $service -n $NAMESPACE &> /dev/null; then
        echo -e "${GREEN}✓${NC} $service"
    else
        echo -e "${RED}✗${NC} $service (not found)"
    fi
done

# Check ingress
echo ""
echo "🔗 Checking Ingress:"
if kubectl get ingress study-assistant-ingress -n $NAMESPACE &> /dev/null; then
    echo -e "${GREEN}✓${NC} Ingress configured"
    echo "   Hosts:"
    kubectl get ingress study-assistant-ingress -n $NAMESPACE -o jsonpath='{.spec.rules[*].host}' | tr ' ' '\n' | sed 's/^/     /'
else
    echo -e "${RED}✗${NC} Ingress not found"
fi

# Check pod health
echo ""
echo "🏥 Pod Health Status:"
pods=$(kubectl get pods -n $NAMESPACE -o jsonpath='{.items[*].metadata.name}')
for pod in $pods; do
    status=$(kubectl get pod $pod -n $NAMESPACE -o jsonpath='{.status.phase}')
    ready=$(kubectl get pod $pod -n $NAMESPACE -o jsonpath='{.status.containerStatuses[0].ready}')
    
    if [ "$status" = "Running" ] && [ "$ready" = "true" ]; then
        echo -e "${GREEN}✓${NC} $pod (Running & Ready)"
    elif [ "$status" = "Running" ]; then
        echo -e "${YELLOW}⚠${NC} $pod (Running but not ready)"
    else
        echo -e "${RED}✗${NC} $pod ($status)"
    fi
done

echo ""
echo "🔍 Quick connectivity tests:"

# Test database connectivity (if postgres pod is running)
postgres_pod=$(kubectl get pods -n $NAMESPACE -l app.kubernetes.io/component=database -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")
if [ -n "$postgres_pod" ]; then
    if kubectl exec $postgres_pod -n $NAMESPACE -- pg_isready -U postgres &> /dev/null; then
        echo -e "${GREEN}✓${NC} Database connectivity"
    else
        echo -e "${RED}✗${NC} Database connectivity"
    fi
fi

echo ""
echo "📋 Summary:"
echo "View logs with: kubectl logs -f deployment/<service-name> -n $NAMESPACE"
echo "Scale services: kubectl scale deployment <service-name> --replicas=<count> -n $NAMESPACE"
echo "Port forward: kubectl port-forward service/<service-name> <local-port>:<service-port> -n $NAMESPACE"
