vpc_cidr_block      = "10.0.0.0/16"
subnet_1_cidr_block = "10.0.1.0/24"
avail_zone          = "us-east-1a"
env_prefix          = "devops"
instance_type       = "t3.medium"

# Note: my_ip, ssh_key, and ssh_private_key will be provided via command line in GitHub Actions
