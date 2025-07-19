variable "vpc_cidr_block" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "subnet_1_cidr_block" {
  description = "CIDR block for subnet 1"
  type        = string
  default     = "10.0.1.0/24"
}

variable "avail_zone" {
  description = "Availability zone"
  type        = string
  default     = "us-east-1a"
}

variable "env_prefix" {
  description = "Environment prefix"
  type        = string
  default     = "devops"
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.medium"
}

variable "ssh_key" {
  description = "Path to SSH public key file"
  type        = string
}

variable "ssh_private_key" {
  description = "Path to SSH private key file"
  type        = string
}

variable "my_ip" {
  description = "Your IP address with CIDR notation"
  type        = string
}