#!/bin/bash

# Helm Chart Validation Script

set -e

CHART_PATH="./helm/study-assistant"

echo "🔍 Validating Helm Chart..."

# Check if chart directory exists
if [ ! -d "$CHART_PATH" ]; then
    echo "❌ Chart directory not found: $CHART_PATH"
    exit 1
fi

# Lint the chart
echo "📋 Running helm lint..."
helm lint "$CHART_PATH"

# Check chart structure
echo "📁 Checking chart structure..."
required_files=(
    "Chart.yaml"
    "values.yaml"
    "values-dev.yaml"
    "templates/_helpers.tpl"
    "templates/configmap.yaml"
    "templates/postgresql.yaml"
    "templates/auth-service.yaml"
    "templates/quiz-service.yaml"
    "templates/flashcard-service.yaml"
    "templates/genai-service.yaml"
    "templates/client.yaml"
    "templates/nginx-configmap.yaml"
)

for file in "${required_files[@]}"; do
    if [ -f "$CHART_PATH/$file" ]; then
        echo "✅ $file"
    else
        echo "❌ Missing: $file"
        exit 1
    fi
done

# Template validation
echo "🔧 Testing template rendering..."
helm template studyapp "$CHART_PATH" \
    --values "$CHART_PATH/values-dev.yaml" \
    --namespace dev > /dev/null

echo "✨ Chart validation completed successfully!"
echo ""
echo "Next steps:"
echo "1. Run: ./deploy-k8s.sh"
echo "2. Check status: kubectl get pods -n dev"
echo "3. Access app: kubectl port-forward -n dev svc/studyapp-study-assistant-client 3000:80"
