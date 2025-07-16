# Study Assistant Deployment Guide

## Overview
This Helm chart deploys the Study Assistant application with all its microservices to Kubernetes/Rancher.

## Services Included
- **PostgreSQL Database** - Primary data store
- **Auth Service** - Authentication and authorization (Spring Boot)
- **Quiz Service** - Quiz management (Spring Boot) 
- **Flashcard Service** - Flashcard management (Spring Boot)
- **GenAI Service** - AI functionality (Python/FastAPI)
- **Client** - Vue.js frontend application
- **pgAdmin** - Database administration tool

## Prerequisites
- Kubernetes cluster with kubectl access
- Helm 3.x installed
- Ingress controller (nginx) configured
- cert-manager for TLS certificates
- Container registry access (GitHub Container Registry)

## Quick Start

### 1. Deploy using Helm
```bash
# Install the application
helm install study-assistant ./helm/study-assistant

# Or upgrade if already installed
helm upgrade study-assistant ./helm/study-assistant

# Check deployment status
kubectl get pods -n devops25-team-deployaas
```

### 2. Using Kubernetes Manifests
```bash
# Apply all manifests
kubectl apply -f k8s/manifests/

# Check deployment
kubectl get all -n devops25-team-deployaas
```

## Configuration

### Environment Variables
Key configuration is managed through:
- `values.yaml` - Main configuration file
- ConfigMap - Application configuration
- Secrets - Sensitive data (passwords, API keys)

### Important Settings
- **Database credentials**: `postgres/initexample`
- **JWT Secret**: Update before production deployment
- **WEBUI_API_KEY**: Configure for GenAI service
- **Domain names**: Update ingress hosts for your environment

### Customization
Edit `helm/study-assistant/values.yaml` to customize:
- Resource limits and requests
- Replica counts
- Image tags
- Ingress hostnames
- Database settings

## Services & URLs
After deployment, services will be available at:
- **Main App**: https://study-assistant.devops25-team-deployaas.rancher.ase.cit.tum.de
- **Auth API**: https://auth.devops25-team-deployaas.rancher.ase.cit.tum.de
- **Quiz API**: https://quiz.devops25-team-deployaas.rancher.ase.cit.tum.de
- **Flashcard API**: https://flashcard.devops25-team-deployaas.rancher.ase.cit.tum.de
- **pgAdmin**: https://pgadmin.devops25-team-deployaas.rancher.ase.cit.tum.de

## Monitoring & Troubleshooting

### Check Pod Status
```bash
kubectl get pods -n devops25-team-deployaas
kubectl describe pod <pod-name> -n devops25-team-deployaas
```

### View Logs
```bash
kubectl logs <pod-name> -n devops25-team-deployaas
kubectl logs -f deployment/study-assistant-auth-service -n devops25-team-deployaas
```

### Check Services
```bash
kubectl get svc -n devops25-team-deployaas
kubectl get ingress -n devops25-team-deployaas
```

## Security Notes
- Update default passwords before production
- Configure proper RBAC
- Review security contexts
- Use network policies if required

## Scaling
```bash
# Scale individual services
kubectl scale deployment study-assistant-auth-service --replicas=3 -n devops25-team-deployaas

# Or update values.yaml and helm upgrade
```

## Cleanup
```bash
# Remove Helm deployment
helm uninstall study-assistant

# Or remove manifests
kubectl delete -f k8s/manifests/
```
