#!/bin/bash

# Set environment variables
export JWT_SECRET="mySecretKey123456789012345678901234567890"
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/initexample"
export SPRING_DATASOURCE_USERNAME="initexample"
export SPRING_DATASOURCE_PASSWORD="initexample"
export FRONTEND_ORIGIN="http://localhost:3000"
export QUIZ_SERVICE_URL="http://localhost:8081"

echo "Environment variables set for flashcard service testing"
echo "JWT_SECRET: $JWT_SECRET"
echo "SPRING_DATASOURCE_URL: $SPRING_DATASOURCE_URL"
echo "FRONTEND_ORIGIN: $FRONTEND_ORIGIN"
echo "QUIZ_SERVICE_URL: $QUIZ_SERVICE_URL"
