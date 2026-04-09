# Cloud-Ready Application - Deployment Guide

## Overview
This application has been transformed to be cloud-native and AWS-ready. All cloud readiness blockers have been resolved.

## Cloud Readiness Fixes Applied

### 1. File System Dependencies (CRITICAL - cr-java-0063)
**Issue**: Application used `java.io.File` for local file system operations.

**Fix Applied**:
- Replaced `java.io.File` with AWS S3 SDK (`software.amazon.awssdk:s3`)
- Implemented cloud-native storage using S3 object storage
- Added environment variable configuration for S3 bucket and region
- Provides durable, scalable storage with automatic redundancy

**Configuration Required**:
```bash
export AWS_S3_BUCKET=your-bucket-name
export AWS_REGION=us-east-1
```

### 2. Platform-Specific Code (HIGH - cr-java-0109)
**Issue**: Application used Windows-specific commands (`cmd.exe /c dir`).

**Fix Applied**:
- Replaced OS-specific shell commands with Java NIO APIs
- Implemented platform-agnostic file operations using `java.nio.file.Files` and `java.nio.file.Path`
- Works consistently across Windows, Linux, and macOS
- Uses `File.separator` and `Path` API for cross-platform compatibility

**Configuration Required**:
```bash
export WORK_DIR=/app/data  # Optional, defaults to current directory
```

## Environment Variables

### Required for AWS Deployment
- `AWS_REGION`: AWS region (default: us-east-1)
- `AWS_S3_BUCKET`: S3 bucket name for data storage (default: app-data-bucket)

### Optional Configuration
- `WORK_DIR`: Working directory for file operations (default: current directory)
- `DEPLOYMENT_MODE`: Deployment mode (default: production)

### AWS Credentials
The application uses AWS SDK's `DefaultCredentialsProvider`, which automatically uses:
1. IAM roles (recommended for EC2, ECS, Lambda)
2. Environment variables (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`)
3. AWS credentials file (`~/.aws/credentials`)
4. Container credentials (ECS task roles)

## Building the Application

```bash
mvn clean package
```

This creates an executable JAR: `target/violations-demo-1.0.0.jar`

## Running Locally

```bash
# Set required environment variables
export AWS_REGION=us-east-1
export AWS_S3_BUCKET=my-test-bucket
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key

# Run the application
java -jar target/violations-demo-1.0.0.jar
```

## AWS Deployment Options

### 1. AWS EC2
```bash
# Launch EC2 instance with IAM role that has S3 access
# Copy JAR to instance
# Run with environment variables
java -jar violations-demo-1.0.0.jar
```

### 2. AWS ECS (Elastic Container Service)
Create a Dockerfile:
```dockerfile
FROM openjdk:11-jre-slim
COPY target/violations-demo-1.0.0.jar /app/app.jar
ENV AWS_REGION=us-east-1
ENV AWS_S3_BUCKET=app-data-bucket
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### 3. AWS Lambda
Package as Lambda deployment package with appropriate handler.

### 4. AWS Elastic Beanstalk
Deploy the JAR directly to Elastic Beanstalk Java platform.

## IAM Permissions Required

The application requires the following S3 permissions:
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
        "arn:aws:s3:::your-bucket-name",
        "arn:aws:s3:::your-bucket-name/*"
      ]
    }
  ]
}
```

## Cloud-Native Features

### ✅ Externalized Configuration
- All configuration via environment variables
- No hardcoded values
- Follows 12-factor app principles

### ✅ Cloud Storage Integration
- Uses AWS S3 for persistent storage
- No local file system dependencies
- Scalable and durable storage

### ✅ Platform Independence
- Cross-platform Java APIs
- No OS-specific commands
- Works on Linux, Windows, macOS

### ✅ Structured Logging
- Console-based logging for cloud environments
- JSON format support for log aggregation
- Compatible with CloudWatch Logs

### ✅ Executable JAR Packaging
- Single JAR deployment
- No WAR files or application servers required
- Cloud-native packaging

## Monitoring and Observability

### CloudWatch Integration
The application logs to stdout/stderr, which automatically integrates with:
- AWS CloudWatch Logs (EC2, ECS, Lambda)
- Container logging drivers
- Cloud monitoring solutions

### Health Checks
Implement health check endpoints for:
- Load balancer health checks
- Container orchestration health probes
- Service mesh integration

## Security Best Practices

1. **Never hardcode credentials** - Use IAM roles and environment variables
2. **Use VPC endpoints** - For private S3 access without internet gateway
3. **Enable S3 encryption** - Use server-side encryption (SSE-S3 or SSE-KMS)
4. **Implement least privilege** - Grant only required S3 permissions
5. **Use secrets management** - AWS Secrets Manager or Parameter Store for sensitive data

## Troubleshooting

### S3 Access Issues
- Verify IAM role/credentials have S3 permissions
- Check bucket name and region configuration
- Ensure bucket exists and is accessible

### Platform Issues
- Application now uses platform-agnostic APIs
- No OS-specific dependencies
- Works consistently across all platforms

## Next Steps

1. Create S3 bucket in your AWS account
2. Configure IAM roles with appropriate permissions
3. Set environment variables for your deployment
4. Build and deploy the application
5. Monitor logs in CloudWatch

## Support

For issues or questions, refer to:
- AWS SDK for Java documentation
- AWS S3 documentation
- Application logs in CloudWatch
