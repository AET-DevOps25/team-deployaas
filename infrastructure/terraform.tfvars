vpc_cidr_block      = "10.0.0.0/16"
subnet_1_cidr_block = "10.0.1.0/24"
avail_zone          = "us-east-1a"
env_prefix          = "devops"
instance_type       = "t2.micro"

# Replace with your actual IP (with /32 suffix)
my_ip = "138.246.3.248/32"

# Update to your actual key paths

ssh_key             = "/Users/manuel.tamayo-moreno/.ssh/devops.pub"
ssh_private_key     = "/Users/manuel.tamayo-moreno/.ssh/devops.pem"
