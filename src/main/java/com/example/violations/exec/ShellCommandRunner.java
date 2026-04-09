package com.example.violations.exec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Platform-agnostic command runner using Java APIs.
 * Replaces OS-specific shell commands with cross-platform Java operations.
 * 
 * This implementation uses Java's standard APIs that work consistently
 * across Windows, Linux, and macOS environments.
 */
public class ShellCommandRunner {
    
    /**
     * Lists directory contents using platform-agnostic Java NIO APIs.
     * Replaces OS-specific commands like "cmd.exe /c dir" or "ls".
     * 
     * @throws Exception if directory listing fails
     */
    public void run() throws Exception {
        // Get directory path from environment variable or use current directory
        String directoryPath = System.getenv().getOrDefault("WORK_DIR", ".");
        listDirectory(directoryPath);
    }
    
    /**
     * Lists contents of specified directory using Java NIO.
     * Works consistently across all platforms (Windows, Linux, macOS).
     * 
     * @param directoryPath Path to directory to list
     * @throws Exception if directory access fails
     */
    public void listDirectory(String directoryPath) throws Exception {
        Path path = Paths.get(directoryPath);
        
        if (!Files.exists(path)) {
            System.err.println("Directory does not exist: " + directoryPath);
            return;
        }
        
        if (!Files.isDirectory(path)) {
            System.err.println("Path is not a directory: " + directoryPath);
            return;
        }
        
        System.out.println("Listing directory: " + path.toAbsolutePath());
        System.out.println("----------------------------------------");
        
        try (Stream<Path> stream = Files.list(path)) {
            List<Path> entries = stream.collect(Collectors.toList());
            
            if (entries.isEmpty()) {
                System.out.println("(empty directory)");
            } else {
                for (Path entry : entries) {
                    String type = Files.isDirectory(entry) ? "[DIR]" : "[FILE]";
                    long size = Files.isDirectory(entry) ? 0 : Files.size(entry);
                    System.out.printf("%s %10d bytes  %s%n", type, size, entry.getFileName());
                }
            }
        }
    }
    
    /**
     * Gets system information using platform-agnostic Java System properties.
     * Replaces OS-specific system commands.
     * 
     * @return System information string
     */
    public String getSystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Operating System: ").append(System.getProperty("os.name")).append("\n");
        info.append("OS Version: ").append(System.getProperty("os.version")).append("\n");
        info.append("OS Architecture: ").append(System.getProperty("os.arch")).append("\n");
        info.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        info.append("Java Vendor: ").append(System.getProperty("java.vendor")).append("\n");
        info.append("User Home: ").append(System.getProperty("user.home")).append("\n");
        info.append("Working Directory: ").append(System.getProperty("user.dir")).append("\n");
        info.append("File Separator: ").append(System.getProperty("file.separator")).append("\n");
        info.append("Path Separator: ").append(System.getProperty("path.separator")).append("\n");
        return info.toString();
    }
    
    /**
     * Checks if a file or directory exists using platform-agnostic APIs.
     * 
     * @param path Path to check
     * @return true if exists, false otherwise
     */
    public boolean exists(String path) {
        return Files.exists(Paths.get(path));
    }
    
    /**
     * Creates a directory using platform-agnostic APIs.
     * 
     * @param path Directory path to create
     * @throws Exception if directory creation fails
     */
    public void createDirectory(String path) throws Exception {
        Path dirPath = Paths.get(path);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            System.out.println("Created directory: " + dirPath.toAbsolutePath());
        } else {
            System.out.println("Directory already exists: " + dirPath.toAbsolutePath());
        }
    }
}
