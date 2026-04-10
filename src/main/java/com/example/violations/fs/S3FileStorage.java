package com.example.violations.fs;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * Cloud-native file storage using AWS S3.
 * This is the preferred approach for file storage in AWS cloud environments.
 */
public class S3FileStorage {
    
    private final S3Client s3Client;
    private final String bucketName;
    
    /**
     * Creates an S3 file storage client.
     * Bucket name and region are read from environment variables.
     */
    public S3FileStorage() {
        String region = System.getenv().getOrDefault("AWS_REGION", "us-east-1");
        this.bucketName = System.getenv().getOrDefault("S3_BUCKET_NAME", "app-file-storage");
        
        this.s3Client = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }
    
    /**
     * Writes data to S3 storage.
     * 
     * @param key The S3 object key (file path)
     * @param content The content to store
     * @throws IOException if upload fails
     */
    public void write(String key, byte[] content) throws IOException {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/octet-stream")
                .build();
            
            s3Client.putObject(putRequest, RequestBody.fromBytes(content));
        } catch (Exception e) {
            throw new IOException("Failed to write to S3: " + key, e);
        }
    }
    
    /**
     * Writes text data to S3 storage.
     * 
     * @param key The S3 object key (file path)
     * @param content The text content to store
     * @throws IOException if upload fails
     */
    public void write(String key, String content) throws IOException {
        write(key, content.getBytes());
    }
    
    /**
     * Reads data from S3 storage.
     * 
     * @param key The S3 object key (file path)
     * @return The content as byte array
     * @throws IOException if download fails
     */
    public byte[] read(String key) throws IOException {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            try (InputStream inputStream = s3Client.getObject(getRequest)) {
                return inputStream.readAllBytes();
            }
        } catch (Exception e) {
            throw new IOException("Failed to read from S3: " + key, e);
        }
    }
    
    /**
     * Closes the S3 client and releases resources.
     */
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
    }
}
