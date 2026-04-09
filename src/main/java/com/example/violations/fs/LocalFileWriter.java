package com.example.violations.fs;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

/**
 * Cloud-native file writer using AWS S3 for persistent data storage.
 * Replaces local file system operations with cloud object storage.
 * 
 * Configuration via environment variables:
 * - AWS_S3_BUCKET: S3 bucket name for data storage
 * - AWS_REGION: AWS region (defaults to us-east-1)
 */
public class LocalFileWriter {
    
    private final S3Client s3Client;
    private final String bucketName;
    
    public LocalFileWriter() {
        // Initialize S3 client with default credentials provider (uses IAM roles in cloud)
        String region = System.getenv().getOrDefault("AWS_REGION", "us-east-1");
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        
        // Get bucket name from environment variable
        this.bucketName = System.getenv().getOrDefault("AWS_S3_BUCKET", "app-data-bucket");
    }
    
    /**
     * Writes data to S3 instead of local file system.
     * Uses cloud-native storage for durability and scalability.
     */
    public void write() {
        write("data.txt", "Sample data content");
    }
    
    /**
     * Writes data to S3 with specified key and content.
     * 
     * @param key S3 object key (file path in bucket)
     * @param content Content to write
     */
    public void write(String key, String content) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("text/plain")
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
            System.out.println("Successfully wrote data to S3: s3://" + bucketName + "/" + key);
        } catch (Exception e) {
            System.err.println("Error writing to S3: " + e.getMessage());
            throw new RuntimeException("Failed to write to cloud storage", e);
        }
    }
    
    /**
     * Cleanup S3 client resources.
     */
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
    }
}
