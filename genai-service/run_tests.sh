#!/bin/bash

# Test runner script for GenAI Service
set -e

echo "Starting GenAI Service Tests..."

# Create virtual environment if it doesn't exist
if [ ! -d "venv" ]; then
    echo "Creating virtual environment..."
    python3 -m venv venv
fi

# Activate virtual environment
source venv/bin/activate

# Install dependencies
echo "Installing dependencies..."
pip install -r requirements.txt
pip install -r test_requirements.txt

# Set environment variables for testing
export PYTHONPATH=$(pwd)
export WEBUI_API_KEY="test_key_for_testing"

# Run tests
echo "Running unit tests..."
pytest tests/ -v

echo "Tests completed successfully!"

echo "All tests passed! ✅"
