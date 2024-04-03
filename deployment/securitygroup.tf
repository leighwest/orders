data "aws_vpc" "default" {
  default = true
}

resource "aws_security_group" "default" {
  vpc_id      = data.aws_vpc.default.id
  description = "default VPC security group"

  // SSH access from a specific IP address
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.PERSONAL_IP_ADDRESS]
  }

  // HTTP access from anywhere
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  // Custom TCP access on port 8080 from anywhere
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
