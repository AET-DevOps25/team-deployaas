# Study Assistant Helm Deployment

This Helm chart deploys the Study Assistant microservices application to Kubernetes using the provided student kubeconfig for Rancher.

## Quick Start

1. **Deploy to Development Environment:**
   ```bash
   ./deploy-k8s.sh
   ```

2. **Access the Application:**
   ```bash
   # Port-forward to access the frontend
   kubectl port-forward -n dev svc/studyapp-study-assistant-client 3000:80
   
   # Then open http://localhost:3000 in your browser
   ```

## Architecture

The application consists of the following services:

- **Frontend (client)**: Vue.js application with Nginx
- **Auth Service**: Spring Boot authentication service (port 8083)
- **Quiz Service**: Spring Boot quiz management service (port 8081)
- **Flashcard Service**: Spring Boot flashcard service (port 8082)
- **GenAI Service**: Python FastAPI service for AI feedback (port 5001)
- **PostgreSQL**: Database service (port 5432)

## Service Communication

Services communicate using Kubernetes DNS:
- `studyapp-study-assistant-auth-service:8083`
- `studyapp-study-assistant-quiz-service:8081`
- `studyapp-study-assistant-flashcard-service:8082`
- `studyapp-study-assistant-genai-service:5001`
- `studyapp-study-assistant-postgresql:5432`

## Configuration

### Values Files

- `values.yaml`: Default values
- `values-dev.yaml`: Development environment overrides

### Key Configuration Points

1. **Database Connection**: Services use the ConfigMap to get the database URL
2. **Service Discovery**: Nginx is configured to proxy API calls to the correct backend services
3. **Secrets**: JWT secrets and API keys are stored in Kubernetes secrets

## Manual Deployment Steps

If you prefer manual deployment:

1. **Set Kubeconfig:**
   ```bash
   export KUBECONFIG=./helm/study-assistant/student.yaml
   ```

2. **Install/Upgrade:**
   ```bash
   helm upgrade --install studyapp ./helm/study-assistant \
     --namespace dev \
     --create-namespace \
     --values ./helm/study-assistant/values-dev.yaml \
     --wait
   ```

3. **Check Status:**
   ```bash
   kubectl get pods -n dev
   kubectl get services -n dev
   ```

## Troubleshooting

### Common Issues

1. **Image Pull Errors**: Ensure images are available in the registry
2. **Service Communication**: Check that service names match in the configuration
3. **Database Connection**: Verify PostgreSQL is running and accessible

### Debugging Commands

```bash
# Check pod logs
kubectl logs -n dev -l app.kubernetes.io/instance=studyapp -f

# Check specific service logs
kubectl logs -n dev deployment/studyapp-study-assistant-quiz-service -f

# Check service endpoints
kubectl get endpoints -n dev

# Describe pods for detailed status
kubectl describe pods -n dev
```

### Port Forwarding for Testing

```bash
# Frontend
kubectl port-forward -n dev svc/studyapp-study-assistant-client 3000:80

# Auth Service
kubectl port-forward -n dev svc/studyapp-study-assistant-auth-service 8083:8083

# Quiz Service
kubectl port-forward -n dev svc/studyapp-study-assistant-quiz-service 8081:8081

# Database
kubectl port-forward -n dev svc/studyapp-study-assistant-postgresql 5432:5432
```

## Important Notes

### Localhost Dependencies Fixed

The following localhost dependencies have been addressed:

1. **Frontend API calls**: Now proxied through Nginx to Kubernetes services
2. **Service-to-service communication**: Uses Kubernetes DNS names
3. **Database connections**: Uses ConfigMap values with proper service names
4. **Health checks**: Updated to use container-internal endpoints

### Security Considerations

- Secrets are base64 encoded in Kubernetes secrets
- Services run as non-root users where possible
- Service account token auto-mounting is disabled
- Network policies can be added for additional security

## Cleanup

To remove the deployment:

```bash
helm uninstall studyapp -n dev
kubectl delete namespace dev
```
