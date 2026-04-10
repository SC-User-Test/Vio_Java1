# Cloud Deployment Guide - AWS

## Overview
This application has been refactored to be fully cloud-ready and compatible with AWS services.

## Cloud Readiness Fixes Applied

### 1. File System Dependencies (cr-java-0063)
**Issue**: Application used `java.io.File` for local file storage
**Fix**: 
- Replaced with database-backed storage using PostgreSQL/RDS
- Added AWS S3 integration as cloud-native alternative
- All file operations now use cloud storage services

### 2. Platform-Specific Code (cr-java-0109)
**Issue**: Application used Windows-specific commands (`cmd.exe /c dir`)
**Fix**:
- Replaced with platform-agnostic Java NIO APIs
- Removed Windows JNA dependencies
- Uses cross-platform Java System properties

## AWS Deployment Options

### Option 1: AWS ECS (Elastic Container Service)
```bash
# Build Docker image
docker build -t violations-demo:latest .

# Tag for ECR
docker tag violations-demo:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/violations-demo:latest

# Push to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/violations-demo:latest

# Create ECS task definition with environment variables
```

### Option 2: AWS Elastic Beanstalk
```bash
# Package application
mvn clean package

# Deploy to Elastic Beanstalk
eb init -p docker violations-demo
eb create violations-demo-env
eb setenv DB_URL=jdbc:postgresql://rds-endpoint:5432/appdb DB_USERNAME=admin DB_PASSWORD=secret
eb deploy
```

### Option 3: AWS EKS (Kubernetes)
```bash
# Build and push Docker image
docker build -t violations-demo:latest .
docker tag violations-demo:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/violations-demo:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/violations-demo:latest

# Apply Kubernetes manifests
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## Required AWS Resources

### 1. Amazon RDS (PostgreSQL)
```bash
# Create RDS instance
aws rds create-db-instance \
  --db-instance-identifier violations-demo-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --master-username admin \
  --master-user-password <password> \
  --allocated-storage 20
```

### 2. Amazon S3 Bucket
```bash
# Create S3 bucket for file storage
aws s3 mb s3://violations-demo-files --region us-east-1
```

### 3. AWS Secrets Manager (for credentials)
```bash
# Store database password
aws secretsmanager create-secret \
  --name violations-demo/db-password \
  --secret-string <password>
```

## Environment Variables

Set these environment variables in your AWS deployment:

```bash
# Database Configuration
DB_URL=jdbc:postgresql://<rds-endpoint>:5432/appdb
DB_USERNAME=admin
DB_PASSWORD=<from-secrets-manager>
DB_POOL_SIZE=10

# AWS Configuration
AWS_REGION=us-east-1
S3_BUCKET_NAME=violations-demo-files

# Application Configuration
ENVIRONMENT=production
PORT=8080
LOG_LEVEL=INFO
```

## IAM Permissions Required

The application requires the following IAM permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::violations-demo-files",
        "arn:aws:s3:::violations-demo-files/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:GetSecretValue"
      ],
      "Resource": "arn:aws:secretsmanager:*:*:secret:violations-demo/*"
    }
  ]
}
```

## Database Schema Initialization

The application automatically creates the required database schema on startup:

```sql
CREATE TABLE IF NOT EXISTS file_storage (
  id SERIAL PRIMARY KEY,
  file_name VARCHAR(255) UNIQUE NOT NULL,
  content BYTEA NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Monitoring and Logging

- Application logs to stdout/stderr (CloudWatch compatible)
- HikariCP connection pool metrics available via JMX
- Health check endpoint available for load balancers

## Security Best Practices

1. **Credentials**: Use AWS Secrets Manager, never hardcode
2. **IAM Roles**: Use IAM roles for EC2/ECS, not access keys
3. **Network**: Deploy in private subnets with NAT gateway
4. **Encryption**: Enable encryption at rest for RDS and S3
5. **Security Groups**: Restrict access to necessary ports only

## Cost Optimization

- Use RDS reserved instances for production
- Enable S3 lifecycle policies for old files
- Use ECS Fargate Spot for non-critical workloads
- Enable CloudWatch Logs retention policies

## Troubleshooting

### Connection Issues
- Check security groups allow traffic from application to RDS
- Verify RDS endpoint is correct in DB_URL
- Ensure IAM role has necessary permissions

### File Storage Issues
- Verify S3 bucket exists and is accessible
- Check IAM permissions for S3 operations
- Ensure AWS_REGION is set correctly

## Next Steps

1. Set up CI/CD pipeline (AWS CodePipeline)
2. Configure auto-scaling policies
3. Set up CloudWatch alarms for monitoring
4. Implement backup strategies for RDS
5. Configure CloudFront for static content delivery
