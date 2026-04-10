package com.example.violations;

import com.example.violations.config.DatabaseConfig;
import com.example.violations.fs.LocalFileWriter;
import com.example.violations.fs.S3FileStorage;
import com.example.violations.exec.ShellCommandRunner;

import javax.sql.DataSource;
import java.util.List;

/**
 * Cloud-ready application entry point.
 * Demonstrates cloud-native patterns and configurations.
 */
public class Application {
    
    public static void main(String[] args) {
        System.out.println("=== Cloud-Ready Application Starting ===");
        
        // Read configuration from environment variables
        String environment = System.getenv().getOrDefault("ENVIRONMENT", "development");
        String appPort = System.getenv().getOrDefault("PORT", "8080");
        
        System.out.println("Environment: " + environment);
        System.out.println("Port: " + appPort);
        
        try {
            // Initialize cloud-ready database connection pool
            System.out.println("\n--- Initializing Database Connection Pool ---");
            DataSource dataSource = DatabaseConfig.createDataSource();
            DatabaseConfig.initializeSchema(dataSource);
            System.out.println("Database connection pool initialized successfully");
            
            // Demonstrate database-backed file storage (replaces local file system)
            System.out.println("\n--- Testing Database-Backed File Storage ---");
            LocalFileWriter fileWriter = new LocalFileWriter(dataSource);
            fileWriter.write("test-file.txt", "Cloud-ready content stored in database");
            System.out.println("File stored in database successfully");
            
            // Demonstrate S3 storage (cloud-native alternative)
            if ("production".equals(environment)) {
                System.out.println("\n--- Testing S3 Cloud Storage ---");
                try (S3FileStorage s3Storage = new S3FileStorage()) {
                    s3Storage.write("test-file.txt", "Cloud-native content stored in S3");
                    System.out.println("File stored in S3 successfully");
                }
            }
            
            // Demonstrate platform-agnostic command execution
            System.out.println("\n--- Testing Platform-Agnostic Operations ---");
            ShellCommandRunner commandRunner = new ShellCommandRunner();
            String systemInfo = commandRunner.getSystemInfo();
            System.out.println("System Info: " + systemInfo);
            
            // List current directory using platform-agnostic API
            String workingDir = System.getProperty("user.dir");
            List<String> files = commandRunner.listDirectory(workingDir);
            System.out.println("Files in working directory: " + files.size());
            
            System.out.println("\n=== Application Started Successfully ===");
            System.out.println("Application is cloud-ready and running on AWS");
            
        } catch (Exception e) {
            System.err.println("Application startup failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
