# 🚀 Kubernetes Deployment Guide

## Team DeployAAS - Study Assistant Platform

This document explains the complete deployment process for the Study Assistant Platform to Kubernetes using GitHub Actions, Helm charts, and secure secret management.

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Prerequisites](#prerequisites)
4. [GitHub Actions Workflow](#github-actions-workflow)
5. [Helm Configuration](#helm-configuration)
6. [Secrets Management](#secrets-management)
7. [Deployment Process](#deployment-process)
8. [Services Overview](#services-overview)
9. [Monitoring & Observability](#monitoring--observability)
10. [Troubleshooting](#troubleshooting)
11. [Manual Deployment](#manual-deployment)

---

## 🏗️ Overview

The Study Assistant Platform is deployed to Kubernetes using a CI/CD pipeline that:
- Builds Docker images for all microservices
- Pushes images to GitHub Container Registry (GHCR)
- Deploys using Helm charts with secure secret management
- Includes monitoring with Prometheus and Grafana
- Features Swagger API documentation for all services

### 🎯 Key Features
- **Microservices Architecture**: Auth, Quiz, Flashcard, GenAI, and Frontend services
- **Auto-scaling**: Kubernetes deployments with resource limits and health checks
- **Secure Configuration**: GitHub Secrets integration with Kubernetes secrets
- **API Documentation**: Swagger UI for all Spring Boot services
- **Monitoring**: Prometheus metrics collection and Grafana dashboards
- **SSL/TLS**: Let's Encrypt certificates with nginx ingress

---

## 🏛️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   GitHub Actions   │ ──►│  Docker Registry  │ ──►│   Kubernetes    │
│                 │    │      (GHCR)      │    │     Cluster     │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                        │
                                                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Kubernetes Namespace: dev                    │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
│  │    Client   │  │ Auth Service│  │Quiz Service │  │Flashcard Svc│ │
│  │   (Vue.js)  │  │   :8083     │  │   :8081     │  │   :8082     │ │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │
│                                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
│  │GenAI Service│  │ PostgreSQL  │  │ Prometheus  │  │   Grafana   │ │
│  │   :5001     │  │   :5432     │  │   :9090     │  │   :3000     │ │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
                    ┌─────────────────────┐
                    │  Nginx Ingress      │
                    │  TLS Termination    │
                    │  team-deployaas.    │
                    │  student.k8s.aet.   │
                    │  cit.tum.de         │
                    └─────────────────────┘
```

---

## ✅ Prerequisites

### GitHub Repository Setup
1. **GitHub Secrets** configured in repository settings:
   ```
   POSTGRES_PASSWORD    # Database password
   JWT_SECRET          # JWT signing secret
   GENAI_API_KEY       # GenAI service API key
   GRAFANA_PASSWORD    # Grafana admin password
   KUBE_CONFIG         # Base64 encoded kubeconfig
   ```

2. **Kubernetes Environment** configured with:
   - Namespace: `dev`
   - Ingress controller (nginx)
   - Cert-manager for TLS certificates

### Local Development
- Docker with BuildKit enabled
- kubectl configured with cluster access
- Helm 3.12.0 or later

---

## 🔄 GitHub Actions Workflow

### Workflow File: `.github/workflows/build-and-deploy-k8s.yml`

The workflow consists of two main jobs:

### 🏗️ Build Job
```yaml
jobs:
  build:
    name: Build & Push Docker Images
    runs-on: ubuntu-latest
    
    strategy:
      matrix:
        include:
          - service: client
          - service: auth-service
          - service: quiz-service
          - service: flashcard-service
          - service: genai-service
```

**What it does:**
1. **Checkout code** from the repository
2. **Login to GHCR** using GitHub token
3. **Build multi-platform images** (linux/amd64, linux/arm64)
4. **Push to registry**: `ghcr.io/aet-devops25/team-deployaas/`
5. **Tag images** with latest, branch name, and SHA

### 🚀 Deploy Job
```yaml
deploy:
  name: Deploy to Kubernetes
  needs: build
  environment: kubernetes
```

**What it does:**
1. **Setup tools** (kubectl, helm)
2. **Configure Kubernetes** connection
3. **Deploy with Helm** using secure values
4. **Verify deployment** status
5. **Output application URL**

### 🔐 Secrets Integration
```bash
helm upgrade --install study-assistant . \
  --values values-secure.yaml \
  --set secrets.postgresPassword="${{ secrets.POSTGRES_PASSWORD }}" \
  --set secrets.jwtSecret="${{ secrets.JWT_SECRET }}" \
  --set secrets.genaiApiKey="${{ secrets.GENAI_API_KEY }}" \
  --set secrets.grafanaPassword="${{ secrets.GRAFANA_PASSWORD }}"
```

---

## ⚙️ Helm Configuration

### 📁 Helm Chart Structure
```
helm/study-assistant/
├── Chart.yaml                 # Chart metadata and dependencies
├── values.yaml                # Default values (development)
├── values-secure.yaml          # Production values with secrets
├── values-prod.yaml           # Production values with placeholders
├── deploy.sh                  # Manual deployment script
└── templates/
    ├── auth-service-deployment.yaml
    ├── quiz-service-deployment.yaml
    ├── flashcard-service-deployment.yaml
    ├── genai-service-deployment.yaml
    ├── client-deployment.yaml
    ├── services.yaml
    ├── ingress.yaml
    ├── secrets.yaml
    ├── prometheus-deployment.yaml
    ├── grafana-deployment.yaml
    └── postgresql/ (from Bitnami dependency)
```

### 🔧 Configuration Files

#### `values.yaml` - Development
- Hardcoded passwords for local development
- NodePort services for direct access
- Basic configuration

#### `values-secure.yaml` - Production
- Uses Kubernetes secrets via `valueFrom.secretKeyRef`
- ClusterIP services
- Secure configuration with secret references

#### `values-prod.yaml` - Template
- Placeholder values for manual deployments
- Used with sed/envsubst for value replacement

---

## 🔐 Secrets Management

### GitHub Secrets → Kubernetes Secrets Flow

1. **GitHub Secrets** stored in repository settings
2. **GitHub Actions** passes secrets to Helm via `--set`
3. **Helm** creates Kubernetes secrets from values
4. **Pods** reference secrets via `valueFrom.secretKeyRef`

### Kubernetes Secret Creation
```yaml
# templates/secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: study-assistant-secrets
type: Opaque
data:
  postgres-password: {{ .Values.secrets.postgresPassword | b64enc }}
  jwt-secret: {{ .Values.secrets.jwtSecret | b64enc }}
  genai-api-key: {{ .Values.secrets.genaiApiKey | b64enc }}
  grafana-password: {{ .Values.secrets.grafanaPassword | b64enc }}
```

### Pod Secret References
```yaml
env:
  - name: SPRING_DATASOURCE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: study-assistant-secrets
        key: postgres-password
```

---

## 🚀 Deployment Process

### 1. Automatic Deployment (Recommended)

**Triggers:**
- Push to `dev` or `main` branch
- Manual workflow dispatch

**Process:**
1. Navigate to **Actions** tab in GitHub
2. Select **"Build and Deploy to Kubernetes"**
3. Click **"Run workflow"**
4. Select branch and run

### 2. Deployment Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Code Push │ ──►│Build Images │ ──►│Push to GHCR │ ──►│Deploy to K8s│
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
      │                   │                   │                   │
      ▼                   ▼                   ▼                   ▼
 GitHub Repo      Docker Build       GHCR Registry      Helm Deploy
 (dev/main)       Multi-platform     Tagged Images      Kubernetes
```

### 3. Verification Steps
After deployment, the workflow automatically:
```bash
# Check pod status
kubectl get pods -n dev -l app.kubernetes.io/instance=study-assistant

# Check services
kubectl get svc -n dev -l app.kubernetes.io/instance=study-assistant

# Check ingress
kubectl get ingress -n dev -l app.kubernetes.io/instance=study-assistant

# Wait for pods to be ready (5 minute timeout)
kubectl wait --for=condition=ready pod -l app.kubernetes.io/instance=study-assistant -n dev --timeout=300s
```

---

## 🛠️ Services Overview

### 🔐 Auth Service (Port 8083)
- **Purpose**: User authentication and JWT token management
- **Database**: PostgreSQL
- **Health Check**: `/api/auth/test`
- **Swagger**: `/auth/swagger-ui/index.html`
- **Metrics**: `/actuator/prometheus`

### 🧠 Quiz Service (Port 8081)
- **Purpose**: Course management and quiz functionality
- **Dependencies**: Auth Service, GenAI Service
- **Health Check**: `/actuator/health`
- **Swagger**: `/quiz/swagger-ui/index.html`
- **Metrics**: `/actuator/prometheus`

### 📚 Flashcard Service (Port 8082)
- **Purpose**: Flashcard deck and card management
- **Dependencies**: Auth Service, Quiz Service
- **Health Check**: `/actuator/health`
- **Swagger**: `/flashcard/swagger-ui/index.html`
- **Metrics**: `/actuator/prometheus`

### 🤖 GenAI Service (Port 5001)
- **Purpose**: AI-powered feedback generation
- **Framework**: FastAPI
- **Health Check**: `/health`
- **Documentation**: `/docs` (FastAPI auto-docs)
- **Metrics**: `/metrics`

### 🎨 Frontend Client (Port 80)
- **Purpose**: Vue.js web application
- **Framework**: Vue 3 + Vite
- **Served by**: Nginx
- **Health Check**: `/`

### 🗄️ PostgreSQL Database (Port 5432)
- **Purpose**: Primary database for all services
- **Managed by**: Bitnami Helm chart
- **Persistence**: 8Gi PVC
- **Authentication**: Kubernetes secrets

---

## 📊 Monitoring & Observability

### Prometheus (Port 9090)
- **Purpose**: Metrics collection and storage
- **Targets**: All Spring Boot services + GenAI service
- **Storage**: 8Gi persistent volume
- **Retention**: 15 days
- **Access**: `/prometheus` path via ingress

### Grafana (Port 3000)
- **Purpose**: Metrics visualization and dashboards
- **Data Source**: Prometheus
- **Admin User**: `admin`
- **Password**: From GitHub secrets
- **Access**: `/grafana` path via ingress

### Alertmanager (Port 9093)
- **Purpose**: Alert management and routing
- **Configuration**: Basic default receiver
- **Storage**: 1Gi persistent volume
- **Access**: `/alertmanager` path via ingress

### API Documentation
- **Auth Service**: `https://team-deployaas.student.k8s.aet.cit.tum.de/auth/swagger-ui/`
- **Quiz Service**: `https://team-deployaas.student.k8s.aet.cit.tum.de/quiz/swagger-ui/`
- **Flashcard Service**: `https://team-deployaas.student.k8s.aet.cit.tum.de/flashcard/swagger-ui/`
- **GenAI Service**: `https://team-deployaas.student.k8s.aet.cit.tum.de/api/genai/docs`

---

## 🔧 Troubleshooting

### Common Issues

#### 1. Image Pull Errors
```bash
# Check if images exist in registry
docker pull ghcr.io/aet-devops25/team-deployaas/auth-service:latest

# Check image pull secrets
kubectl get secrets -n dev
```

#### 2. Pod CrashLoopBackOff
```bash
# Check pod logs
kubectl logs -n dev <pod-name> --previous

# Check pod description
kubectl describe pod -n dev <pod-name>
```

#### 3. Database Connection Issues
```bash
# Check PostgreSQL pod status
kubectl get pods -n dev -l app.kubernetes.io/component=postgresql

# Check service endpoints
kubectl get endpoints -n dev study-assistant-postgresql
```

#### 4. Secrets Not Found
```bash
# List secrets in namespace
kubectl get secrets -n dev

# Check secret content (base64 encoded)
kubectl get secret study-assistant-secrets -n dev -o yaml
```

### Useful Commands

#### Check Deployment Status
```bash
# All pods in namespace
kubectl get pods -n dev

# Specific application pods
kubectl get pods -n dev -l app.kubernetes.io/instance=study-assistant

# Pod logs with follow
kubectl logs -f -n dev deployment/study-assistant-auth-service
```

#### Check Services and Ingress
```bash
# All services
kubectl get svc -n dev

# Ingress configuration
kubectl get ingress -n dev
kubectl describe ingress -n dev study-assistant
```

#### Debug Network Issues
```bash
# Test service connectivity from another pod
kubectl run -it --rm debug --image=busybox --restart=Never -n dev -- sh

# Inside the debug pod:
nslookup study-assistant-auth-service
wget -qO- http://study-assistant-auth-service:8083/api/auth/test
```

---

## 🛠️ Manual Deployment

### Using the Deploy Script
```bash
cd helm/study-assistant
./deploy.sh

# Check status only
./deploy.sh status

# Clean up deployment
./deploy.sh cleanup
```

### Manual Helm Commands
```bash
# Add Bitnami repository
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Update dependencies
cd helm/study-assistant
helm dependency update

# Deploy with development values
helm upgrade --install study-assistant . \
  --namespace dev \
  --values values.yaml \
  --timeout 10m \
  --wait

# Deploy with secure values (requires GitHub secrets)
helm upgrade --install study-assistant . \
  --namespace dev \
  --values values-secure.yaml \
  --set secrets.postgresPassword="your-secure-password" \
  --set secrets.jwtSecret="your-jwt-secret" \
  --set secrets.genaiApiKey="your-api-key" \
  --set secrets.grafanaPassword="your-grafana-password" \
  --timeout 10m \
  --wait
```

### Environment-Specific Deployments
```bash
# Development (local values)
helm upgrade --install study-assistant . --values values.yaml

# Staging (secure values with test secrets)
helm upgrade --install study-assistant . --values values-secure.yaml \
  --set secrets.postgresPassword="test-password"

# Production (secure values with production secrets)
helm upgrade --install study-assistant . --values values-secure.yaml \
  --set secrets.postgresPassword="${POSTGRES_PASSWORD}" \
  --set secrets.jwtSecret="${JWT_SECRET}"
```

---

## 🌐 Access URLs

After successful deployment, the application is available at:

### Main Application
- **Frontend**: `https://team-deployaas.student.k8s.aet.cit.tum.de/`

### API Endpoints
- **Auth API**: `https://team-deployaas.student.k8s.aet.cit.tum.de/api/auth/`
- **Quiz API**: `https://team-deployaas.student.k8s.aet.cit.tum.de/api/quiz/`
- **Flashcard API**: `https://team-deployaas.student.k8s.aet.cit.tum.de/api/flashcard/`
- **GenAI API**: `https://team-deployaas.student.k8s.aet.cit.tum.de/api/genai/`

### Documentation
- **Auth Swagger**: `https://team-deployaas.student.k8s.aet.cit.tum.de/auth/swagger-ui/`
- **Quiz Swagger**: `https://team-deployaas.student.k8s.aet.cit.tum.de/quiz/swagger-ui/`
- **Flashcard Swagger**: `https://team-deployaas.student.k8s.aet.cit.tum.de/flashcard/swagger-ui/`
- **GenAI Docs**: `https://team-deployaas.student.k8s.aet.cit.tum.de/api/genai/docs`

### Monitoring
- **Prometheus**: `https://team-deployaas.student.k8s.aet.cit.tum.de/prometheus/`
- **Grafana**: `https://team-deployaas.student.k8s.aet.cit.tum.de/grafana/`
- **Alertmanager**: `https://team-deployaas.student.k8s.aet.cit.tum.de/alertmanager/`

---

## 📝 Notes

- **TLS Certificates**: Automatically managed by cert-manager with Let's Encrypt
- **Resource Limits**: All services have configured CPU and memory limits
- **Health Checks**: Liveness and readiness probes for all services
- **Persistent Storage**: Database and monitoring data are persisted
- **Secrets Security**: All sensitive data managed through Kubernetes secrets
- **Multi-Platform Images**: Built for both AMD64 and ARM64 architectures

---

## 🆘 Support

For deployment issues:
1. Check the GitHub Actions workflow logs
2. Review Kubernetes pod logs and events
3. Verify GitHub secrets are properly configured
4. Ensure the Kubernetes cluster has sufficient resources

**Repository**: `https://github.com/AET-DevOps25/team-deployaas`
**Team**: Team DeployAAS
