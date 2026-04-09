# Quick Start Guide - Cloud-Ready Application

## Prerequisites
- Java 11 or higher
- Maven 3.6+
- AWS Account with S3 access
- Docker (optional, for containerization)

## Quick Setup

### 1. Configure AWS Credentials

**Option A: Using IAM Roles (Recommended for EC2/ECS)**
```bash
# No configuration needed - IAM role provides credentials automatically
```

**Option B: Using Environment Variables**
```bash
export AWS_ACCESS_KEY_ID=your-access-key-id
export AWS_SECRET_ACCESS_KEY=your-secret-access-key
export AWS_REGION=us-east-1
```

**Option C: Using AWS CLI Configuration**
```bash
aws configure
# Enter your credentials when prompted
```

### 2. Create S3 Bucket

```bash
# Create bucket
aws s3 mb s3://my-app-bucket --region us-east-1

# Verify bucket created
aws s3 ls
```

### 3. Set Environment Variables

```bash
export AWS_REGION=us-east-1
export AWS_S3_BUCKET=my-app-bucket
```

### 4. Build the Application

```bash
mvn clean package
```

### 5. Run the Application

```bash
java -jar target/violations-demo-1.0.0.jar
```

## Expected Output

```
=================================================
Cloud-Ready Application Starting...
=================================================

Configuration:
  AWS Region: us-east-1
  S3 Bucket: my-app-bucket
  Work Directory: .
  Deployment Mode: production

--- Cloud Storage Demo (AWS S3) ---
Replacing local file system with cloud object storage...
Successfully wrote data to S3: s3://my-app-bucket/demo/data.txt
✓ Successfully wrote data to cloud storage (S3)
✓ Data is now durable, scalable, and highly available

--- Platform-Agnostic Operations Demo ---
Using cross-platform Java APIs...

System Information:
Operating System: Linux
OS Version: 5.10.0
OS Architecture: amd64
Java Version: 11.0.16
...

Directory Listing:
----------------------------------------
[DIR]          0 bytes  src
[FILE]      3732 bytes  pom.xml
[FILE]      1188 bytes  Dockerfile
...

✓ Platform-agnostic operations completed successfully
✓ Application works consistently across all platforms

=================================================
Application completed successfully!
=================================================
```

## Docker Quick Start

### Build Image
```bash
docker build -t violations-demo:1.0.0 .
```

### Run Container
```bash
docker run \
  -e AWS_REGION=us-east-1 \
  -e AWS_S3_BUCKET=my-app-bucket \
  -e AWS_ACCESS_KEY_ID=your-key \
  -e AWS_SECRET_ACCESS_KEY=your-secret \
  violations-demo:1.0.0
```

## Troubleshooting

### Issue: "Unable to load credentials from any provider"
**Solution:** Set AWS credentials via environment variables or IAM role

### Issue: "The specified bucket does not exist"
**Solution:** Create the S3 bucket or update AWS_S3_BUCKET environment variable

### Issue: "Access Denied" when writing to S3
**Solution:** Verify IAM permissions include `s3:PutObject` for the bucket

### Issue: Application fails on Windows
**Solution:** Application is now platform-agnostic and should work on Windows. Verify Java 11+ is installed.

## Verification Checklist

- [ ] Java 11+ installed: `java -version`
- [ ] Maven installed: `mvn -version`
- [ ] AWS credentials configured
- [ ] S3 bucket created
- [ ] Environment variables set
- [ ] Application builds successfully: `mvn clean package`
- [ ] Application runs successfully: `java -jar target/violations-demo-1.0.0.jar`
- [ ] Data written to S3: `aws s3 ls s3://your-bucket/demo/`

## Next Steps

1. ✅ Application is cloud-ready
2. Deploy to AWS (EC2, ECS, Elastic Beanstalk)
3. Set up monitoring with CloudWatch
4. Configure auto-scaling
5. Implement CI/CD pipeline

## Documentation

- Full documentation: `README.md`
- Cloud readiness report: `CLOUD_READINESS_REPORT.md`
- IAM policy template: `iam-policy.json`
- AWS deployment config: `aws-eb-config.yml`

## Support

For issues or questions:
1. Check `CLOUD_READINESS_REPORT.md` for detailed information
2. Review AWS SDK documentation
3. Verify environment variables are set correctly
4. Check CloudWatch Logs for application logs
