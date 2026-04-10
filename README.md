# Cloud-Ready Java Application

## Overview
This application has been refactored to be fully cloud-ready and compatible with AWS cloud environments. All cloud readiness blockers have been resolved.

## Cloud Readiness Status
✅ **All Critical and High Severity Blockers Resolved**

### Fixed Issues

#### 1. File System Dependencies (Critical - cr-java-0063)
- **Before**: Used `java.io.File` for local file storage with hardcoded paths
- **After**: 
  - Database-backed storage using PostgreSQL with HikariCP connection pooling
  - AWS S3 integration for cloud-native file storage
  - Environment variable configuration

#### 2. Platform-Specific Code (High - cr-java-0109)
- **Before**: Windows-specific commands (`cmd.exe /c dir`) and JNA API calls
- **After**:
  - Platform-agnostic Java NIO APIs
  - Cross-platform file operations
  - Java System properties instead of native calls

## Architecture

### Cloud-Native Patterns Implemented
- ✅ **12-Factor App Compliance**: Configuration via environment variables
- ✅ **Stateless Design**: No local file system dependencies
- ✅ **Connection Pooling**: HikariCP for database connections
- ✅ **Cloud Storage**: S3 integration for file storage
- ✅ **Platform Agnostic**: Cross-platform Java APIs
- ✅ **Containerized**: Docker support with multi-stage builds
- ✅ **Externalized Config**: All configuration via environment variables

### Project Structure
```
CompTestVioJAva/
├── src/main/java/com/example/violations/
│   ├── Application.java                    # Main entry point
│   ├── config/
│   │   └── DatabaseConfig.java            # Database connection pooling
│   ├── fs/
│   │   ├── LocalFileWriter.java           # Database-backed file storage
│   │   └── S3FileStorage.java             # AWS S3 file storage
│   ├── exec/
│   │   └── ShellCommandRunner.java        # Platform-agnostic operations
│   └── os/
│       └── WindowsApiUsage.java           # Cross-platform system APIs
├── pom.xml                                 # Maven dependencies
├── Dockerfile                              # Container image definition
├── .env.example                            # Environment configuration template
├── AWS_DEPLOYMENT.md                       # AWS deployment guide
└── README.md                               # This file
```

## Dependencies

### Cloud-Ready Dependencies Added
- **HikariCP 5.0.1**: High-performance JDBC connection pooling
- **PostgreSQL 42.6.0**: Database driver (AWS RDS compatible)
- **AWS SDK S3**: Cloud-native file storage
- **AWS SDK Secrets Manager**: Secure credential management
- **SLF4J**: Structured logging for cloud monitoring

## Configuration

### Environment Variables
```bash
# Database (AWS RDS)
DB_URL=jdbc:postgresql://<rds-endpoint>:5432/appdb
DB_USERNAME=admin
DB_PASSWORD=<from-secrets-manager>
DB_POOL_SIZE=10

# AWS Services
AWS_REGION=us-east-1
S3_BUCKET_NAME=app-file-storage

# Application
ENVIRONMENT=production
PORT=8080
LOG_LEVEL=INFO
```

## Building

### Local Build
```bash
mvn clean package
```

### Docker Build
```bash
docker build -t violations-demo:latest .
```

## Running

### Local Execution
```bash
# Set environment variables
export DB_URL=jdbc:postgresql://localhost:5432/appdb
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

# Run application
java -jar target/violations-demo-1.0.0-shaded.jar
```

### Docker Execution
```bash
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/appdb \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  violations-demo:latest
```

## AWS Deployment

See [AWS_DEPLOYMENT.md](AWS_DEPLOYMENT.md) for detailed deployment instructions.

### Quick Deploy to AWS ECS
```bash
# Build and push to ECR
docker build -t violations-demo:latest .
docker tag violations-demo:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/violations-demo:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/violations-demo:latest

# Deploy to ECS (configure task definition with environment variables)
```

## Cloud Compatibility

### AWS Services Integration
- ✅ **Amazon RDS**: PostgreSQL database with connection pooling
- ✅ **Amazon S3**: Cloud-native file storage
- ✅ **AWS Secrets Manager**: Secure credential management
- ✅ **Amazon ECS/EKS**: Container orchestration
- ✅ **AWS CloudWatch**: Logging and monitoring
- ✅ **Amazon ECR**: Container registry

### Platform Support
- ✅ **Linux**: Primary target platform
- ✅ **Windows**: Cross-platform compatible
- ✅ **macOS**: Development environment support

## Security

### Best Practices Implemented
- ✅ Non-root user in Docker container
- ✅ No hardcoded credentials
- ✅ Environment variable configuration
- ✅ AWS IAM role-based authentication
- ✅ Secure connection pooling
- ✅ Minimal container image (Alpine-based)

## Monitoring

### Observability Features
- Structured logging to stdout/stderr (CloudWatch compatible)
- HikariCP connection pool metrics (JMX)
- Health check endpoint
- Application startup logging

## Testing

### Verify Cloud Readiness
```bash
# Check no hardcoded file paths
grep -r "C:/" src/  # Should return no results
grep -r "/tmp/" src/  # Should return no results

# Check no platform-specific code
grep -r "cmd.exe" src/  # Should return no results
grep -r "win32" src/  # Should return no results

# Verify environment variable usage
grep -r "System.getenv" src/  # Should show configuration reads
```

## Migration Notes

### Changes from Original Code

1. **LocalFileWriter.java**
   - Removed: `new File("C:/temp/data.txt")`
   - Added: Database-backed storage with DataSource injection
   - Added: S3 storage alternative

2. **ShellCommandRunner.java**
   - Removed: `Runtime.getRuntime().exec("cmd.exe /c dir")`
   - Added: Platform-agnostic Java NIO file operations
   - Added: Cross-platform ProcessBuilder with OS detection

3. **WindowsApiUsage.java**
   - Removed: JNA Windows API calls (`Advapi32.INSTANCE.GetUserName`)
   - Added: Java System properties for cross-platform compatibility

4. **pom.xml**
   - Added: HikariCP, PostgreSQL driver, AWS SDK dependencies
   - Added: Maven Shade plugin for executable JAR
   - Configured: Java 11 target

## Support

For issues or questions:
1. Check [AWS_DEPLOYMENT.md](AWS_DEPLOYMENT.md) for deployment guidance
2. Review environment variable configuration
3. Verify AWS IAM permissions
4. Check CloudWatch logs for runtime errors

## License

Copyright © 2024. All rights reserved.
