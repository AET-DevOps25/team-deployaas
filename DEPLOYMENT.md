# Deployment Guide

This document outlines the automated deployment process for the Team DeployAAS application using Terraform, Ansible, and GitHub Actions.

## Architecture

The application consists of:
- **Frontend**: Vue.js client (Port 3000)
- **Backend Services**:
  - Quiz Service (Java/Spring Boot, Port 8081)
  - Auth Service (Java/Spring Boot, Port 8082)
  - Flashcard Service (Java/Spring Boot, Port 8083)
  - GenAI Service (Python/FastAPI, Port 8084)
- **Database**: PostgreSQL (Port 5432)
- **Admin Tools**: PgAdmin (Port 8080)

## Deployment Options

### Option 1: Automated Infrastructure + Deployment (Recommended)

Uses the `deploy-terraform-ansible.yml` workflow to:
1. Provision new EC2 infrastructure with Terraform
2. Configure the server with Docker and dependencies
3. Deploy the full application stack

**Required GitHub Secrets:**
- `AWS_ACCESS_KEY_ID`: Your AWS access key
- `AWS_SECRET_ACCESS_KEY`: Your AWS secret key
- `AWS_SESSION_TOKEN`: AWS session token (for temporary credentials)
- `SSH_PRIVATE_KEY`: SSH private key for EC2 access

**Trigger:** Push to `ansible-terraform` branch or manual workflow dispatch

### Option 2: Deploy to Existing EC2

Uses the `deploy-existing-ec2.yml` workflow to deploy to a pre-configured EC2 instance.

**Additional Required Secret:**
- `EC2_INSTANCE_IP`: The IP address of your existing EC2 instance

## Deployment Process

### Phase 1: Infrastructure Setup (`playbook-simple.yml`)
1. Update system packages
2. Install Docker, Docker Compose, Git, Python3
3. Start and enable Docker service
4. Create deployment user with Docker permissions
5. Verify installations

### Phase 2: Application Deployment (`playbook-deploy.yml`)
1. Clone/update application repository (ansible-terraform branch)
2. Create environment configuration
3. Build and start all services with Docker Compose
4. Verify service health

## Environment Configuration

The application automatically adapts to the deployment environment:

- **Frontend**: Uses `window.location.hostname` to detect the server IP at runtime
- **Services**: Use environment variables for database connections and API keys
- **Database**: Configured with secure credentials via environment variables

## Service URLs

After successful deployment, the following services will be available:

- **Frontend**: `http://<EC2_IP>:3000`
- **Quiz API**: `http://<EC2_IP>:8081`
- **Auth API**: `http://<EC2_IP>:8082`
- **Flashcard API**: `http://<EC2_IP>:8083`
- **GenAI API**: `http://<EC2_IP>:8084`
- **PgAdmin**: `http://<EC2_IP>:8080`

## Security Features

- Security groups automatically updated with current public IP for SSH access
- Database credentials secured via environment variables
- SSH key-based authentication for server access
- No hardcoded localhost or development credentials in production

## Monitoring and Troubleshooting

### Check Service Status
```bash
# SSH into the EC2 instance
ssh -i devops.pem ec2-user@<EC2_IP>

# Check running containers
docker ps

# Check service logs
cd team-deployaas
docker-compose logs <service-name>
```

### Common Issues
1. **Services not starting**: Check Docker Compose logs for missing environment variables
2. **Frontend API errors**: Verify backend services are running and accessible
3. **Database connection issues**: Check PostgreSQL container status and credentials

## GitHub Actions Workflow Features

- **Dynamic IP Detection**: Automatically updates security groups with current public IP
- **Robust Error Handling**: Includes timeouts and retry logic
- **Environment Awareness**: No hardcoded values, adapts to any AWS region/VPC
- **Sequential Deployment**: Runs infrastructure setup before application deployment
- **Service Verification**: Tests connectivity and displays service URLs

## Branch Strategy

- **Main Branch**: Stable production code
- **ansible-terraform**: Deployment-ready branch with infrastructure code
- Workflows trigger on pushes to `ansible-terraform` branch

## Cost Optimization

- Infrastructure is provisioned on-demand
- Can be easily destroyed after testing with `terraform destroy`
- Uses t2.micro instances (eligible for AWS Free Tier)

## Future Enhancements

- SSL/TLS termination with Application Load Balancer
- Auto-scaling groups for high availability
- RDS for managed database service
- CloudWatch monitoring and logging
- Blue-green deployment strategy
