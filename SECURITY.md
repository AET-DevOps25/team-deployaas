# Helm Deployment Security Guide

## ⚠️ Important Security Considerations

### Current Secrets Usage

Your Helm chart uses the following secrets:

1. **DATABASE_USERNAME/DATABASE_PASSWORD** - PostgreSQL credentials
2. **JWT_SECRET** - JWT token signing key
3. **WEBUI_API_KEY** - External GenAI service API key

### Security Issues with Default Setup

❌ **Secrets stored in values.yaml**
- Plain text in version control
- Visible to anyone with repository access
- Base64 encoding is NOT encryption

❌ **Hardcoded default values**
- Predictable passwords ("changeme123")
- Generic JWT secrets
- Default API keys

### ✅ Secure Deployment Options

#### Option 1: Development (Quick but less secure)
```bash
# Uses secrets embedded in Helm template
./deploy-helm-secure.sh dev
```

#### Option 2: Production (Recommended)
```bash
# Generate secure secrets
./deploy-helm-secure.sh secrets

# Edit secrets.yaml with your actual values
vi secrets.yaml

# Apply secrets to cluster
kubectl apply -f secrets.yaml

# Deploy with external secrets
./deploy-helm-secure.sh prod
```

#### Option 3: Manual Secret Creation
```bash
# Create secrets manually
kubectl create secret generic teamdeployaas-study-assistant-secrets \
  --from-literal=DATABASE_USERNAME=studyassistant \
  --from-literal=DATABASE_PASSWORD=$(openssl rand -base64 32) \
  --from-literal=JWT_SECRET=$(openssl rand -base64 64) \
  --from-literal=WEBUI_API_KEY=your-actual-api-key \
  --namespace teamdeployaas-studyapp

# Deploy without creating secrets in template
helm install teamdeployaas-study-assistant ./helm/study-assistant \
  --namespace teamdeployaas-studyapp \
  --set secrets.createInTemplate=false
```

#### Option 4: External Secret Management
For enterprise deployments, consider:
- **HashiCorp Vault** with vault-secrets-operator
- **AWS Secrets Manager** with external-secrets-operator
- **Azure Key Vault** with secrets-store-csi-driver
- **Google Secret Manager**

### 🔐 Best Practices

1. **Never commit real secrets to git**
   ```bash
   echo "secrets.yaml" >> .gitignore
   echo "*.secret" >> .gitignore
   ```

2. **Use strong, unique passwords**
   ```bash
   # Generate strong database password
   openssl rand -base64 32
   
   # Generate strong JWT secret
   openssl rand -base64 64
   ```

3. **Rotate secrets regularly**
   - Database passwords every 90 days
   - JWT secrets every 30 days
   - API keys as recommended by provider

4. **Use different secrets per environment**
   - Development: Simple but unique
   - Staging: Production-like security
   - Production: Maximum security

5. **Limit secret access**
   ```bash
   # Only specific users can read secrets
   kubectl create rolebinding secret-reader \
     --clusterrole=view \
     --user=user@company.com \
     --namespace=teamdeployaas-studyapp
   ```

### 🔍 Security Verification

After deployment, verify secrets are properly configured:

```bash
# Check if secrets exist (don't show values)
kubectl get secrets -n teamdeployaas-studyapp

# Verify pods can access secrets
kubectl exec -n teamdeployaas-studyapp \
  deployment/teamdeployaas-study-assistant-quiz-service \
  -- env | grep -E "(DATABASE|JWT)"

# Check for hardcoded secrets in pod environment
kubectl get pods -n teamdeployaas-studyapp -o yaml | \
  grep -i -E "(password|secret|key)" || echo "✅ No hardcoded secrets found"
```

### 🚨 Security Checklist

Before going to production:

- [ ] All default passwords changed
- [ ] JWT secret is cryptographically strong (>64 characters)
- [ ] API keys are valid and restricted
- [ ] Secrets are not in git repository
- [ ] secrets.createInTemplate is set to false
- [ ] External secret management is configured
- [ ] Pod security contexts are enabled
- [ ] Network policies restrict inter-pod communication
- [ ] Ingress has TLS termination
- [ ] Regular security scanning is scheduled

### 📋 Troubleshooting

**Issue: Pods failing to start with "secret not found"**
```bash
# Check if secret exists
kubectl get secret teamdeployaas-study-assistant-secrets -n teamdeployaas-studyapp

# If not, create it:
kubectl apply -f secrets.yaml
```

**Issue: Database connection failed**
```bash
# Check database credentials
kubectl get secret teamdeployaas-study-assistant-secrets -n teamdeployaas-studyapp -o yaml

# Verify database is running
kubectl get pods -n teamdeployaas-studyapp -l app.kubernetes.io/component=postgresql
```

**Issue: JWT authentication failed**
```bash
# Check JWT secret is set
kubectl exec -n teamdeployaas-studyapp \
  deployment/teamdeployaas-study-assistant-auth-service \
  -- env | grep JWT_SECRET
```
