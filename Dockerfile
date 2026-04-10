# Multi-stage build for optimized cloud deployment
FROM maven:3.9-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage - minimal image
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 appuser && \
    adduser -D -u 1001 -G appuser appuser

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*-shaded.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port (configurable via environment variable)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD java -cp app.jar com.example.violations.Application || exit 1

# Run the application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
