provider "aws" {
  region = "us-east-1"
}

# ======== Variables ========
variable "vpc_cidr_block" {}
variable "subnet_1_cidr_block" {}
variable "avail_zone" {}
variable "env_prefix" {}
variable "instance_type" {}
variable "ssh_key" {}           # path to public key (.pub)
variable "ssh_private_key" {}   # path to private key (.pem)
variable "my_ip" {}

# ======== Get latest Amazon Linux 2023 AMI ========
data "aws_ami" "amazon-linux-image" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

output "ami_id" {
  value = data.aws_ami.amazon-linux-image.id
}

# ======== Networking ========
resource "aws_vpc" "myapp-vpc" {
  cidr_block = var.vpc_cidr_block

  tags = {
    Name = "${var.env_prefix}-vpc"
  }
}

resource "aws_subnet" "myapp-subnet-1" {
  vpc_id            = aws_vpc.myapp-vpc.id
  cidr_block        = var.subnet_1_cidr_block
  availability_zone = var.avail_zone

  tags = {
    Name = "${var.env_prefix}-subnet-1"
  }
}

resource "aws_internet_gateway" "myapp-igw" {
  vpc_id = aws_vpc.myapp-vpc.id

  tags = {
    Name = "${var.env_prefix}-igw"
  }
}

resource "aws_route_table" "myapp-route-table" {
  vpc_id = aws_vpc.myapp-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.myapp-igw.id
  }

  tags = {
    Name = "${var.env_prefix}-route-table"
  }
}

resource "aws_route_table_association" "a-rtb-subnet" {
  subnet_id      = aws_subnet.myapp-subnet-1.id
  route_table_id = aws_route_table.myapp-route-table.id
}

# ======== Security Group ========
resource "aws_security_group" "myapp-sg" {
  name   = "myapp-sg"
  vpc_id = aws_vpc.myapp-vpc.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.my_ip]
  }

  # Frontend (React app)
  ingress {
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Quiz Service
  ingress {
    from_port   = 8081
    to_port     = 8081
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Flashcard Service  
  ingress {
    from_port   = 8082
    to_port     = 8082
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Auth Service
  ingress {
    from_port   = 8083
    to_port     = 8083
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Database Admin (pgAdmin)
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.env_prefix}-sg"
  }
}

# ======== Key Pair ========
resource "aws_key_pair" "ssh-key" {
  key_name   = "devops-key"
  public_key = file(var.ssh_key)
}

# ======== EC2 Instance ========
resource "aws_instance" "myapp-server" {
  ami                         = data.aws_ami.amazon-linux-image.id
  instance_type               = var.instance_type
  key_name                    = aws_key_pair.ssh-key.key_name
  associate_public_ip_address = true
  subnet_id                   = aws_subnet.myapp-subnet-1.id
  vpc_security_group_ids      = [aws_security_group.myapp-sg.id]
  availability_zone           = var.avail_zone

  tags = {
    Name = "${var.env_prefix}-server"
  }
}

output "server-ip" {
  value = aws_instance.myapp-server.public_ip
}

# ======== Ansible Local Exec ========
# Temporarily commented out to debug the hanging issue
# resource "null_resource" "configure_server" {
#   triggers = {
#     server_ip = aws_instance.myapp-server.public_ip
#   }
#
#   provisioner "local-exec" {
#     working_dir = "${path.module}/ansible"
#     interpreter = ["/bin/bash", "-c"]
#     command     = <<EOT
#       echo "Waiting for SSH to be ready on ${self.triggers.server_ip}..."
#       for i in {1..15}; do
#         ssh -o StrictHostKeyChecking=no -i ${var.ssh_private_key} ec2-user@${self.triggers.server_ip} "echo SSH is up" >/dev/null 2>&1 && break
#         echo "SSH not ready yet... retrying in 10s"
#         sleep 10
#       done
#
#       echo "Running Ansible playbook"
#       ansible-playbook --inventory ${self.triggers.server_ip}, --private-key ${var.ssh_private_key} --user ec2-user playbook.yml
#     EOT
#   }
# }
