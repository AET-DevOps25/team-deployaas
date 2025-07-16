# Study Assistant Kubernetes Deployment Guide

## ✅ Configuration Validation Results

Your Helm configuration has been analyzed and **fixed**. Here are the key points:

### ✅ What's Working Well:
- ✅ **Network Communication**: All services use Kubernetes DNS names
- ✅ **Service Discovery**: Proper ClusterIP services defined
- ✅ **Environment Variables**: Database connections use ConfigMap/Secret references
- ✅ **Inter-service Communication**: Dynamic service URLs (no hardcoded IPs)
- ✅ **Nginx Proxy**: Correctly routes API calls to backend services

### 🔧 Issues Fixed:
1. **Database URL**: Now uses dynamic service names instead of hardcoded values
2. **Missing Ingress**: Added Ingress configuration for external access
3. **FRONTEND_ORIGIN**: Updated to match Ingress host
4. **Production Values**: Created separate production configuration

## 🚀 Deployment Instructions

### 1. Prerequisites
- Kubernetes cluster access (configured via student.yaml)
- Helm 3.x installed
- kubectl configured with proper context

### 2. Set up kubeconfig (using your student.yaml)
```bash
# Copy your student.yaml to kubeconfig
cp helm/study-assistant/student.yaml ~/.kube/config

# Test cluster access
kubectl cluster-info
```

### 3. Deploy to Development Environment
```bash
# Make deployment script executable
chmod +x deploy-helm.sh

# Deploy to dev namespace (default)
./deploy-helm.sh

# Or manually with Helm:
helm install study-assistant helm/study-assistant \
  --namespace dev \
  --create-namespace \
  --wait
```

### 4. Deploy to Custom Namespace
```bash
# Deploy to custom namespace
NAMESPACE=my-namespace ./deploy-helm.sh

# Or manually:
helm install study-assistant helm/study-assistant \
  --namespace my-namespace \
  --create-namespace \
  --values helm/study-assistant/values.yaml \
  --wait
```

### 5. Deploy to Production
```bash
# Deploy with production values
NAMESPACE=production VALUES_FILE=values-prod.yaml ./deploy-helm.sh

# Or manually:
helm install study-assistant helm/study-assistant \
  --namespace production \
  --create-namespace \
  --values helm/study-assistant/values-prod.yaml \
  --wait
```

## 🌐 Network Architecture

### Internal Service Communication:
```
Frontend (Nginx) → Backend Services → Database
     ↓
┌─────────────────────────────────────────┐
│ study-assistant-client:80               │
│ ├─ /api/auth/ → auth-service:8083       │
│ ├─ /api/quiz/ → quiz-service:8081       │
│ └─ /api/flashcard/ → flashcard:8082     │
└─────────────────────────────────────────┘
     ↓
┌─────────────────────────────────────────┐
│ Backend Services                        │
│ ├─ auth-service:8083                    │
│ ├─ quiz-service:8081                    │
│ ├─ flashcard-service:8082               │
│ └─ genai-service:5001                   │
└─────────────────────────────────────────┘
     ↓
┌─────────────────────────────────────────┐
│ study-assistant-postgresql:5432         │
└─────────────────────────────────────────┘
```

### External Access:
```
Internet → Ingress → study-assistant-client:80
```

## 🔍 Verification Commands

### Check deployment status:
```bash
kubectl get pods -n dev
kubectl get services -n dev
kubectl get ingress -n dev
```

### Check logs:
```bash
kubectl logs -f deployment/study-assistant-client -n dev
kubectl logs -f deployment/study-assistant-quiz-service -n dev
kubectl logs -f deployment/study-assistant-postgresql -n dev
```

### Port forward for local access:
```bash
kubectl port-forward service/study-assistant-client 8080:80 -n dev
```

## 🌍 Access Your Application

### Option 1: Via Ingress (Recommended)
1. Add to your `/etc/hosts` file:
   ```
   <CLUSTER_IP> study-assistant.local
   ```
2. Access: http://study-assistant.local

### Option 2: Via Port Forward
```bash
kubectl port-forward service/study-assistant-client 8080:80 -n dev
```
Then access: http://localhost:8080

## 🔧 Configuration Details

### Environment Variables:
- **DATABASE_URL**: `jdbc:postgresql://study-assistant-postgresql:5432/studyassistant`
- **SPRING_PROFILES_ACTIVE**: `dev` (or `prod` for production)
- **FRONTEND_ORIGIN**: `http://study-assistant.local`

### Secrets (Base64 encoded):
- **DATABASE_USERNAME**: `studyassistant`
- **DATABASE_PASSWORD**: `changeme123`
- **JWT_SECRET**: `your-jwt-secret-key-change-me`
- **WEBUI_API_KEY**: `your-webui-api-key`

## ⚠️ Important Notes

1. **Change Default Secrets**: Update secrets in `values-prod.yaml` for production
2. **Resource Limits**: Adjust resources based on your cluster capacity
3. **Storage**: PVC is enabled for PostgreSQL data persistence
4. **Security**: All services run as non-root except PostgreSQL
5. **Health Checks**: All services have liveness and readiness probes

## 🆘 Troubleshooting

### Common Issues:

1. **Pods Stuck in Pending**:
   ```bash
   kubectl describe pod <pod-name> -n dev
   # Check for resource constraints or PVC issues
   ```

2. **Service Connection Issues**:
   ```bash
   kubectl exec -it <pod-name> -n dev -- nslookup study-assistant-postgresql
   ```

3. **Database Connection Issues**:
   ```bash
   kubectl logs deployment/study-assistant-quiz-service -n dev
   ```

4. **Ingress Not Working**:
   ```bash
   kubectl describe ingress study-assistant-ingress -n dev
   ```

## 📝 Files Modified/Created:
- ✅ Fixed: `helm/study-assistant/templates/configmap.yaml` - Dynamic database URL
- ✅ Added: `helm/study-assistant/templates/ingress.yaml` - External access
- ✅ Updated: `helm/study-assistant/values.yaml` - Ingress config & FRONTEND_ORIGIN
- ✅ Created: `helm/study-assistant/values-prod.yaml` - Production configuration
- ✅ Created: `deploy-helm.sh` - Automated deployment script
- ✅ Created: `KUBERNETES_DEPLOYMENT_GUIDE.md` - This guide

Your Helm chart is now ready for deployment with proper network communication and no hardcoded values! 🎉
