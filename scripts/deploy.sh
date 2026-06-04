#!/bin/bash
set -e

REGION="ap-southeast-4"
REGISTRY=$(aws sts get-caller-identity --query Account --output text).dkr.ecr.$REGION.amazonaws.com

# Pull deploy artefacts from S3
aws s3 cp s3://orders-deploy-artefacts/docker-compose.prod.yaml /home/ec2-user/docker-compose.prod.yaml
mkdir -p /home/ec2-user/nginx
aws s3 cp s3://orders-deploy-artefacts/nginx/nginx.conf /home/ec2-user/nginx/nginx.conf

# Authenticate with ECR
aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $REGISTRY

# Fetch secrets from SSM
export POSTGRES_PASSWORD=$(aws ssm get-parameter --name orders_postgres_password --with-decryption --query Parameter.Value --output text --region $REGION)
export MAIL_USERNAME=$(aws ssm get-parameter --name orders_ses_smtp_username --with-decryption --query Parameter.Value --output text --region $REGION)
export MAIL_PASSWORD=$(aws ssm get-parameter --name orders_ses_smtp_password --with-decryption --query Parameter.Value --output text --region $REGION)
export ORDERS_IMAGE=$REGISTRY/orders:latest

# Deploy
docker-compose -f /home/ec2-user/docker-compose.prod.yaml pull
docker-compose -f /home/ec2-user/docker-compose.prod.yaml up -d
docker image prune -f