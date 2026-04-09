package com.example.violations;

import com.example.violations.fs.LocalFileWriter;
import com.example.violations.exec.ShellCommandRunner;

/**
 * Cloud-ready application demonstrating AWS S3 integration and platform-agnostic operations.
 * 
 * This application has been transformed to be cloud-native:
 * - Uses AWS S3 for persistent storage instead of local file system
 * - Uses platform-agnostic Java APIs instead of OS-specific commands
 * - Configurable via environment variables
 * - Follows 12-factor app principles
 * 
 * Environment Variables:
 * - AWS_REGION: AWS region (default: us-east-1)
 * - AWS_S3_BUCKET: S3 bucket name (default: app-data-bucket)
 * - WORK_DIR: Working directory (default: current directory)
 */
public class Application {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("Cloud-Ready Application Starting...");
        System.out.println("=================================================");
        
        // Display configuration
        displayConfiguration();
        
        try {
            // Demonstrate cloud-native file storage with AWS S3
            demonstrateCloudStorage();
            
            // Demonstrate platform-agnostic operations
            demonstratePlatformAgnosticOperations();
            
            System.out.println("\n=================================================");
            System.out.println("Application completed successfully!");
            System.out.println("=================================================");
            
        } catch (Exception e) {
            System.err.println("Error during application execution: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Display current configuration from environment variables.
     */
    private static void displayConfiguration() {
        System.out.println("\nConfiguration:");
        System.out.println("  AWS Region: " + System.getenv().getOrDefault("AWS_REGION", "us-east-1"));
        System.out.println("  S3 Bucket: " + System.getenv().getOrDefault("AWS_S3_BUCKET", "app-data-bucket"));
        System.out.println("  Work Directory: " + System.getenv().getOrDefault("WORK_DIR", "."));
        System.out.println("  Deployment Mode: " + System.getenv().getOrDefault("DEPLOYMENT_MODE", "production"));
        System.out.println();
    }
    
    /**
     * Demonstrate cloud-native storage using AWS S3.
     * Replaces local file system operations with cloud object storage.
     */
    private static void demonstrateCloudStorage() {
        System.out.println("\n--- Cloud Storage Demo (AWS S3) ---");
        System.out.println("Replacing local file system with cloud object storage...");
        
        LocalFileWriter writer = null;
        try {
            writer = new LocalFileWriter();
            
            // Write sample data to S3
            String sampleData = "Cloud-native application data - " + System.currentTimeMillis();
            writer.write("demo/data.txt", sampleData);
            
            System.out.println("✓ Successfully wrote data to cloud storage (S3)");
            System.out.println("✓ Data is now durable, scalable, and highly available");
            
        } catch (Exception e) {
            System.err.println("✗ Cloud storage operation failed: " + e.getMessage());
            System.err.println("  Ensure AWS credentials and S3 bucket are configured correctly");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    /**
     * Demonstrate platform-agnostic operations using Java NIO APIs.
     * Works consistently across Windows, Linux, and macOS.
     */
    private static void demonstratePlatformAgnosticOperations() {
        System.out.println("\n--- Platform-Agnostic Operations Demo ---");
        System.out.println("Using cross-platform Java APIs...");
        
        try {
            ShellCommandRunner runner = new ShellCommandRunner();
            
            // Display system information using platform-agnostic APIs
            System.out.println("\nSystem Information:");
            System.out.println(runner.getSystemInfo());
            
            // List directory contents using Java NIO (works on all platforms)
            System.out.println("\nDirectory Listing:");
            runner.run();
            
            System.out.println("\n✓ Platform-agnostic operations completed successfully");
            System.out.println("✓ Application works consistently across all platforms");
            
        } catch (Exception e) {
            System.err.println("✗ Platform operation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
