# 🏗️ AWS Infrastructure Deployment Guide

## Team DeployAAS - Terraform + Ansible Deployment

This document explains the complete infrastructure deployment process for the Study Assistant Platform to AWS using Terraform for infrastructure provisioning and Ansible for application deployment.

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Prerequisites](#prerequisites)
4. [GitHub Actions Workflow](#github-actions-workflow)
5. [Terraform Configuration](#terraform-configuration)
6. [Ansible Configuration](#ansible-configuration)
7. [Deployment Process](#deployment-process)
8. [Infrastructure Components](#infrastructure-components)
9. [Application Deployment](#application-deployment)
10. [Monitoring & Services](#monitoring--services)
11. [Troubleshooting](#troubleshooting)
12. [Manual Deployment](#manual-deployment)

---

## 🏗️ Overview

The Study Assistant Platform uses a Infrastructure as Code (IaC) approach to deploy to AWS:
- **Terraform** provisions AWS infrastructure (VPC, EC2, Security Groups)
- **Ansible** configures the server and deploys the application stack
- **Docker Compose** orchestrates the microservices on the EC2 instance
- **GitHub Actions** automates the entire deployment pipeline

### 🎯 Key Features
- **Infrastructure as Code**: Reproducible infrastructure with Terraform
- **Configuration Management**: Automated server setup with Ansible
- **Docker Orchestration**: Multi-service application deployment
- **Security**: SSH key management and security group configuration
- **Monitoring**: Prometheus, Grafana, and Alertmanager stack
- **CI/CD Integration**: Automated deployments from GitHub

---

## 🏛️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   GitHub Actions   │ ──►│    Terraform     │ ──►│       AWS       │
│   CI/CD Pipeline   │    │  Infrastructure  │    │    Resources    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                        │
                                                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                         AWS Infrastructure                      │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                    VPC: 10.0.0.0/16                       │ │
│  │  ┌───────────────────────────────────────────────────────┐ │ │
│  │  │              Subnet: 10.0.1.0/24                    │ │ │
│  │  │                                                     │ │ │
│  │  │  ┌─────────────────────────────────────────────────┐ │ │ │
│  │  │  │           EC2 Instance (t3.medium)             │ │ │ │
│  │  │  │                                                 │ │ │ │
│  │  │  │  ┌─────────────────────────────────────────────┐│ │ │ │
│  │  │  │  │          Docker Compose Stack              ││ │ │ │
│  │  │  │  │                                             ││ │ │ │
│  │  │  │  │ • Frontend (Vue.js)      :3000              ││ │ │ │
│  │  │  │  │ • Auth Service           :8083              ││ │ │ │
│  │  │  │  │ • Quiz Service           :8081              ││ │ │ │
│  │  │  │  │ • Flashcard Service      :8082              ││ │ │ │
│  │  │  │  │ • GenAI Service          :5001              ││ │ │ │
│  │  │  │  │ • PostgreSQL             :5432              ││ │ │ │
│  │  │  │  │ • PgAdmin                :8080              ││ │ │ │
│  │  │  │  │ • Prometheus             :9090              ││ │ │ │
│  │  │  │  │ • Grafana                :3001              ││ │ │ │
│  │  │  │  │ • Alertmanager           :9093              ││ │ │ │
│  │  │  │  └─────────────────────────────────────────────┘│ │ │ │
│  │  │  └─────────────────────────────────────────────────┘ │ │ │
│  │  └───────────────────────────────────────────────────────┘ │ │
│  └─────────────────────────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                    Security Group                          │ │
│  │  • SSH (22) from deployment IP                             │ │
│  │  • HTTP (80) from anywhere                                 │ │
│  │  • Custom ports (3000, 5001, 8080-8083, 9090, 9093)      │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
                    ┌─────────────────────┐
                    │  Internet Gateway   │
                    │  Public Access      │
                    └─────────────────────┘
```

---

## ✅ Prerequisites

### GitHub Repository Setup
1. **GitHub Secrets** configured in repository settings:
   ```
   AWS_ACCESS_KEY_ID       # AWS access key
   AWS_SECRET_ACCESS_KEY   # AWS secret key
   AWS_SESSION_TOKEN       # AWS session token (if using temporary credentials)
   LOCAL_SSH_PRIVATE_KEY   # SSH private key for EC2 access
   LOCAL_IP               # Optional: Your local IP for SSH access
   OPENAI_API_KEY         # OpenAI API key for GenAI service
   ```

2. **AWS Account** with permissions for:
   - EC2 instances
   - VPC management
   - Security groups
   - Key pairs

### Local Development
- AWS CLI configured
- Terraform 1.6.0+
- Ansible installed
- SSH key pair generated

---

## 🔄 GitHub Actions Workflow

### Workflow File: `.github/workflows/deploy-terraform-ansible.yml`

The workflow consists of a single job with multiple steps:

### 🏗️ Infrastructure Provisioning Phase
```yaml
jobs:
  deploy:
    name: Deploy Infrastructure and Application
    runs-on: ubuntu-latest
    environment: AWS
```

**What it does:**
1. **Setup AWS credentials** and tools
2. **Generate SSH keys** for EC2 access
3. **Run Terraform** to provision infrastructure
4. **Extract EC2 IP** for Ansible inventory
5. **Wait for instance** to be ready

### 🔧 Configuration Management Phase
```yaml
- name: Run initial server setup
  working-directory: infrastructure/ansible
  run: |
    ansible-playbook -i inventory.ini playbook-simple.yml
```

**What it does:**
1. **Install Docker** and dependencies
2. **Configure users** and permissions
3. **Setup Docker Compose**
4. **Prepare environment** for application

### 🚀 Application Deployment Phase
```yaml
- name: Run application deployment
  working-directory: infrastructure/ansible
  run: |
    ansible-playbook -i inventory.ini playbook-deploy.yml \
      -e "openai_api_key=${{ secrets.OPENAI_API_KEY }}"
```

**What it does:**
1. **Clone application** repository
2. **Configure environment** variables
3. **Deploy services** with Docker Compose
4. **Verify deployment** health

---

## ⚙️ Terraform Configuration

### 📁 Infrastructure Structure
```
infrastructure/
├── main.tf                    # Main Terraform configuration
├── variables.tf               # Variable definitions
├── terraform.tfvars          # Variable values
├── outputs.tf                # Output definitions
└── ansible/                  # Ansible playbooks
    ├── inventory.ini          # Generated dynamically
    ├── playbook-simple.yml    # Server setup
    └── playbook-deploy.yml    # Application deployment
```

### 🔧 Key Resources

#### VPC and Networking
```hcl
# VPC with custom CIDR
resource "aws_vpc" "myapp-vpc" {
  cidr_block = var.vpc_cidr_block
  tags = {
    Name = "${var.env_prefix}-vpc"
  }
}

# Public subnet
resource "aws_subnet" "myapp-subnet-1" {
  vpc_id            = aws_vpc.myapp-vpc.id
  cidr_block        = var.subnet_1_cidr_block
  availability_zone = var.avail_zone
}

# Internet gateway for public access
resource "aws_internet_gateway" "myapp-igw" {
  vpc_id = aws_vpc.myapp-vpc.id
}
```

#### Security Groups
```hcl
resource "aws_security_group" "myapp-sg" {
  name_prefix = "${var.env_prefix}-sg"
  vpc_id      = aws_vpc.myapp-vpc.id

  # SSH access from deployment IP
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.my_ip]
  }

  # Application ports
  ingress {
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  # Additional ports: 5001, 8080-8083, 9090, 9093
  # ... (full configuration in main.tf)
}
```

#### EC2 Instance
```hcl
resource "aws_instance" "myapp-server" {
  ami                         = data.aws_ami.amazon-linux-image.id
  instance_type               = var.instance_type
  key_name                    = aws_key_pair.ssh-key.key_name
  associate_public_ip_address = true
  subnet_id                   = aws_subnet.myapp-subnet-1.id
  vpc_security_group_ids      = [aws_security_group.myapp-sg.id]
  availability_zone           = var.avail_zone

  root_block_device {
    volume_type = "gp3"
    volume_size = 30
    encrypted   = true
  }
}
```

### 🎯 Variables Configuration

#### `terraform.tfvars`
```hcl
vpc_cidr_block      = "10.0.0.0/16"
subnet_1_cidr_block = "10.0.1.0/24"
avail_zone          = "us-east-1a"
env_prefix          = "devops"
instance_type       = "t3.medium"
```

#### Runtime Variables
- `my_ip`: Current IP for SSH access (auto-detected)
- `ssh_key`: SSH public key path
- `ssh_private_key`: SSH private key path
- `local_ip`: Optional local IP for additional SSH access

---

## 🔧 Ansible Configuration

### 📖 Playbook Overview

#### `playbook-simple.yml` - Server Setup
**Purpose**: Initial server configuration and Docker installation

**Tasks:**
1. **System Updates**
   ```yaml
   - name: Update system packages
     yum:
       name: '*'
       state: latest
       update_cache: true
   ```

2. **Package Installation**
   ```yaml
   - name: Install required system packages
     yum:
       name:
         - docker
         - python3-pip
         - git
       state: present
   ```

3. **Docker Configuration**
   ```yaml
   - name: Start and enable Docker service
     systemd:
       name: docker
       state: started
       enabled: true
   ```

4. **User Management**
   ```yaml
   - name: Create deployment user
     user:
       name: deployuser
       groups: docker
       append: yes
       create_home: yes
   ```

#### `playbook-deploy.yml` - Application Deployment
**Purpose**: Deploy the complete application stack

**Key Variables:**
```yaml
vars:
  github_repo: "https://github.com/aet-devops25/team-deployaas.git"
  app_directory: "/home/ec2-user/team-deployaas"
  deployment_user: "ec2-user"
```

**Deployment Tasks:**
1. **Repository Management**
   ```yaml
   - name: Clone GitHub repository
     git:
       repo: "{{ github_repo }}"
       dest: "{{ app_directory }}"
       version: dev
       force: yes
   ```

2. **Environment Configuration**
   ```yaml
   - name: Create .env file for deployment
     copy:
       dest: "{{ app_directory }}/.env"
       content: |
         POSTGRES_USER=initexample
         POSTGRES_PASSWORD=initexample
         POSTGRES_DB=initexample
         OPENAI_API_KEY={{ openai_api_key }}
         FRONTEND_ORIGIN=http://{{ ansible_host }}:3000
   ```

3. **Service Deployment**
   ```yaml
   - name: Deploy with Docker Compose
     shell: |
       cd {{ app_directory }}
       sudo docker-compose up -d
   ```

### 🎯 Inventory Management
```ini
# Generated dynamically by GitHub Actions
<EC2_PUBLIC_IP> ansible_user=ec2-user ansible_ssh_private_key_file=~/.ssh/devops.pem
```

---

## 🚀 Deployment Process

### 1. Automatic Deployment (Recommended)

**Triggers:**
- Push to `dev`, `ansible-terraform`, or `main` branch
- Manual workflow dispatch

**Process:**
1. Navigate to **Actions** tab in GitHub
2. Select **"Deploy to AWS with Terraform + Ansible"**
3. Click **"Run workflow"**
4. Select branch and run

### 2. Deployment Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Setup AWS  │ ──►│  Terraform  │ ──►│   Ansible   │ ──►│   Verify    │
│ Credentials │    │ Provision   │    │  Configure  │    │ Deployment  │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
      │                   │                   │                   │
      ▼                   ▼                   ▼                   ▼
   Get IP &          Create VPC,        Install Docker     Check Services
   Setup SSH         EC2, Security      Deploy App Stack   Display URLs
                     Groups, etc.
```

### 3. Infrastructure Provisioning Details
```bash
# Terraform commands executed:
terraform init
terraform plan \
  -var="my_ip=${CURRENT_IP}/32" \
  -var="ssh_key=devops.pub" \
  -var="ssh_private_key=~/.ssh/devops.pem" \
  -out=tfplan
terraform apply -auto-approve tfplan
```

### 4. Configuration Management Details
```bash
# Ansible commands executed:
ansible-playbook -i inventory.ini playbook-simple.yml
ansible-playbook -i inventory.ini playbook-deploy.yml \
  -e "openai_api_key=${OPENAI_API_KEY}"
```

---

## 🛠️ Infrastructure Components

### 🌐 Network Architecture
- **VPC**: `10.0.0.0/16` - Isolated network environment
- **Subnet**: `10.0.1.0/24` - Public subnet in `us-east-1a`
- **Internet Gateway**: Public internet access
- **Route Table**: Routes traffic to internet gateway

### 🖥️ Compute Resources
- **Instance Type**: `t3.medium` (2 vCPU, 4 GB RAM)
- **AMI**: Latest Amazon Linux 2023
- **Storage**: 30 GB GP3 encrypted EBS volume
- **Network**: Public IP with enhanced networking

### 🔒 Security Configuration
- **Security Group Rules**:
  ```
  SSH (22)        ← Deployment IP only
  HTTP (3000)     ← 0.0.0.0/0 (Frontend)
  API (5001)      ← 0.0.0.0/0 (GenAI)
  Services (8080-8083) ← 0.0.0.0/0 (APIs/PgAdmin)
  Monitoring (9090, 9093) ← 0.0.0.0/0 (Prometheus/Alertmanager)
  ```
- **SSH Key**: Generated and managed through GitHub secrets
- **Encrypted Storage**: All EBS volumes encrypted at rest

---

## 🐳 Application Deployment

### 📦 Docker Compose Stack
The application runs as a multi-container Docker Compose stack:

#### Frontend Service
```yaml
client:
  image: ghcr.io/aet-devops25/team-deployaas/client:latest
  ports:
    - "3000:80"
  environment:
    - FRONTEND_ORIGIN=http://${EC2_IP}:3000
```

#### Microservices
```yaml
auth-service:
  image: ghcr.io/aet-devops25/team-deployaas/auth-service:latest
  ports:
    - "8083:8083"
  environment:
    - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/initexample
    - JWT_SECRET=${JWT_SECRET}
```

#### Database
```yaml
postgres:
  image: postgres:15
  ports:
    - "5432:5432"
  environment:
    - POSTGRES_DB=initexample
    - POSTGRES_USER=initexample
    - POSTGRES_PASSWORD=initexample
  volumes:
    - postgres_data:/var/lib/postgresql/data
```

#### Monitoring Stack
```yaml
prometheus:
  image: prom/prometheus:latest
  ports:
    - "9090:9090"
  
grafana:
  image: grafana/grafana:latest
  ports:
    - "3001:3000"
```

### 🔄 Deployment Process
1. **Pull Images**: Latest images from GitHub Container Registry
2. **Environment Setup**: Configure `.env` file with secrets
3. **Service Startup**: Start all services with Docker Compose
4. **Health Verification**: Check service availability
5. **URL Display**: Show all accessible endpoints

---

## 📊 Monitoring & Services

### 🎯 Application URLs
After deployment, services are accessible at:

#### Core Application
- **Frontend**: `http://<EC2_IP>:3000`
- **Auth Service**: `http://<EC2_IP>:8083`
- **Quiz Service**: `http://<EC2_IP>:8081`
- **Flashcard Service**: `http://<EC2_IP>:8082`
- **GenAI Service**: `http://<EC2_IP>:5001`

#### Database & Admin
- **PgAdmin**: `http://<EC2_IP>:8080`
- **PostgreSQL**: `<EC2_IP>:5432` (internal access)

#### Monitoring & Observability
- **Prometheus**: `http://<EC2_IP>:9090`
- **Grafana**: `http://<EC2_IP>:3001`
- **Alertmanager**: `http://<EC2_IP>:9093`

### 📈 Monitoring Features
- **Metrics Collection**: Prometheus scrapes all services
- **Visualization**: Grafana dashboards for system and application metrics
- **Alerting**: Alertmanager for notification management
- **Health Checks**: Built-in service health monitoring

---

## 🔧 Troubleshooting

### Common Issues

#### 1. Terraform Apply Failures
```bash
# Check AWS credentials
aws sts get-caller-identity

# Verify region and availability zone
aws ec2 describe-availability-zones --region us-east-1

# Check for existing resources
terraform show
```

#### 2. EC2 Instance Not Accessible
```bash
# Verify security group rules
aws ec2 describe-security-groups --group-ids <sg-id>

# Check instance status
aws ec2 describe-instances --instance-ids <instance-id>

# Test SSH connectivity
ssh -i ~/.ssh/devops.pem ec2-user@<instance-ip>
```

#### 3. Ansible Connection Issues
```bash
# Test SSH connection manually
ssh -o ConnectTimeout=5 -o StrictHostKeyChecking=no \
    -i ~/.ssh/devops.pem ec2-user@<instance-ip> "echo ready"

# Check inventory file
cat infrastructure/ansible/inventory.ini

# Run ansible ping
ansible all -i infrastructure/ansible/inventory.ini -m ping
```

#### 4. Docker Compose Issues
```bash
# SSH to instance and check logs
ssh -i ~/.ssh/devops.pem ec2-user@<instance-ip>
cd team-deployaas
sudo docker-compose logs

# Check service status
sudo docker-compose ps

# Restart services
sudo docker-compose down
sudo docker-compose up -d
```

#### 5. GitHub Actions Failures

**Secret Issues:**
- Verify all required secrets are set in repository settings
- Check secret names match exactly (case-sensitive)
- Ensure AWS credentials have necessary permissions

**IP Access Issues:**
- Check if your IP changed during deployment
- Verify security group allows your current IP
- Consider adding multiple IP ranges if needed

### Useful Commands

#### Terraform Operations
```bash
# Show current state
terraform show

# List resources
terraform state list

# Get outputs
terraform output

# Destroy infrastructure
terraform destroy -auto-approve
```

#### Ansible Operations
```bash
# Test connectivity
ansible all -i inventory.ini -m ping

# Run specific tasks
ansible-playbook -i inventory.ini playbook-simple.yml --tags setup

# Check facts
ansible all -i inventory.ini -m setup
```

#### Docker Operations (on EC2)
```bash
# Container status
sudo docker ps -a

# Service logs
sudo docker-compose logs <service-name>

# Resource usage
sudo docker stats

# Clean up
sudo docker system prune -f
```

---

## 🛠️ Manual Deployment

### Prerequisites for Manual Deployment
```bash
# Install required tools
sudo apt-get update
sudo apt-get install -y terraform ansible awscli

# Configure AWS credentials
aws configure
# or
export AWS_ACCESS_KEY_ID="your-key"
export AWS_SECRET_ACCESS_KEY="your-secret"
export AWS_SESSION_TOKEN="your-token"
```

### Step-by-Step Manual Deployment

#### 1. Prepare SSH Keys
```bash
# Generate SSH key pair (if not exists)
ssh-keygen -t rsa -b 4096 -f ~/.ssh/devops -N ""

# Set proper permissions
chmod 600 ~/.ssh/devops
chmod 644 ~/.ssh/devops.pub
```

#### 2. Deploy Infrastructure with Terraform
```bash
cd infrastructure

# Initialize Terraform
terraform init

# Get your current IP
CURRENT_IP=$(curl -s ipv4.icanhazip.com)

# Plan deployment
terraform plan \
  -var="my_ip=${CURRENT_IP}/32" \
  -var="ssh_key=$HOME/.ssh/devops.pub" \
  -var="ssh_private_key=$HOME/.ssh/devops" \
  -out=tfplan

# Apply changes
terraform apply -auto-approve tfplan

# Get instance IP
INSTANCE_IP=$(terraform output -raw server-ip)
echo "Instance IP: $INSTANCE_IP"
```

#### 3. Configure Ansible Inventory
```bash
cd ansible

# Create inventory file
echo "$INSTANCE_IP ansible_user=ec2-user ansible_ssh_private_key_file=$HOME/.ssh/devops" > inventory.ini

# Test connection (wait for instance to be ready)
sleep 60
ansible all -i inventory.ini -m ping
```

#### 4. Run Ansible Playbooks
```bash
# Initial server setup
ansible-playbook -i inventory.ini playbook-simple.yml

# Deploy application
ansible-playbook -i inventory.ini playbook-deploy.yml \
  -e "openai_api_key=your-openai-api-key"
```

#### 5. Verify Deployment
```bash
# Check service health
curl http://$INSTANCE_IP:3000
curl http://$INSTANCE_IP:8083/api/auth/test
curl http://$INSTANCE_IP:8081/api/quiz/test
curl http://$INSTANCE_IP:8082/api/flashcard/test
curl http://$INSTANCE_IP:5001/health
```

### Environment-Specific Deployments

#### Development Environment
```bash
# Use development branch
terraform plan -var="env_prefix=dev" -out=tfplan
ansible-playbook -i inventory.ini playbook-deploy.yml \
  -e "github_branch=dev" \
  -e "openai_api_key=test-key"
```

#### Production Environment
```bash
# Use production settings
terraform plan \
  -var="env_prefix=prod" \
  -var="instance_type=t3.large" \
  -out=tfplan
ansible-playbook -i inventory.ini playbook-deploy.yml \
  -e "github_branch=main" \
  -e "openai_api_key=${PROD_OPENAI_KEY}"
```

---

## 🧹 Cleanup and Maintenance

### Infrastructure Cleanup
```bash
# Destroy all AWS resources
cd infrastructure
terraform destroy -auto-approve

# Clean Terraform state
rm -rf .terraform*
rm tfplan
```

### Application Updates
```bash
# SSH to instance
ssh -i ~/.ssh/devops ec2-user@<instance-ip>

# Pull latest changes
cd team-deployaas
git pull origin dev

# Rebuild and restart
sudo docker-compose down
sudo docker-compose build --no-cache
sudo docker-compose up -d
```

### Monitoring and Logs
```bash
# View application logs
sudo docker-compose logs -f

# Monitor resource usage
htop
sudo docker stats

# Check disk usage
df -h
sudo docker system df
```

---

## 📝 Configuration Reference

### Terraform Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `vpc_cidr_block` | CIDR block for VPC | `"10.0.0.0/16"` | No |
| `subnet_1_cidr_block` | CIDR block for subnet | `"10.0.1.0/24"` | No |
| `avail_zone` | Availability zone | `"us-east-1a"` | No |
| `env_prefix` | Environment prefix | `"devops"` | No |
| `instance_type` | EC2 instance type | `"t3.medium"` | No |
| `my_ip` | IP for SSH access | - | Yes |
| `ssh_key` | SSH public key path | - | Yes |
| `ssh_private_key` | SSH private key path | - | Yes |
| `local_ip` | Additional IP for SSH | - | No |

### Ansible Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `github_repo` | Repository URL | `"https://github.com/aet-devops25/team-deployaas.git"` |
| `app_directory` | Application directory | `"/home/ec2-user/team-deployaas"` |
| `deployment_user` | User for deployment | `"ec2-user"` |
| `openai_api_key` | OpenAI API key | - |

### GitHub Secrets Required

| Secret | Description | Example |
|--------|-------------|---------|
| `AWS_ACCESS_KEY_ID` | AWS access key | `AKIAIOSFODNN7EXAMPLE` |
| `AWS_SECRET_ACCESS_KEY` | AWS secret key | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY` |
| `AWS_SESSION_TOKEN` | AWS session token | `IQoJb3JpZ2luX2VjEH...` |
| `LOCAL_SSH_PRIVATE_KEY` | SSH private key | `-----BEGIN RSA PRIVATE KEY-----` |
| `LOCAL_IP` | Your local IP | `192.168.1.100/32` |
| `OPENAI_API_KEY` | OpenAI API key | `sk-...` |

---

## 🆘 Support

For deployment issues:
1. Check GitHub Actions workflow logs
2. Review Terraform plan output
3. Verify AWS credentials and permissions
4. Check Ansible connection and playbook execution
5. Monitor EC2 instance logs and Docker container status

**Common Error Solutions:**
- **"User initiated shutdown"**: Check instance type compatibility with AMI
- **"Connection timeout"**: Verify security group rules and instance status
- **"Permission denied"**: Check SSH key configuration and file permissions
- **"Service unavailable"**: Review Docker Compose logs and container health

**Repository**: `https://github.com/AET-DevOps25/team-deployaas`
**Team**: Team DeployAAS
