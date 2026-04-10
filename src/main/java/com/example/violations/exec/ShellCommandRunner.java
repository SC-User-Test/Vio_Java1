package com.example.violations.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cloud-ready command execution using platform-agnostic Java APIs.
 * Replaces OS-specific shell commands with cross-platform alternatives.
 */
public class ShellCommandRunner {
    
    /**
     * Lists directory contents using platform-agnostic Java NIO APIs.
     * This replaces OS-specific commands like "cmd.exe /c dir" (Windows) or "ls" (Linux).
     * 
     * @param directoryPath The directory to list
     * @return List of file/directory names
     * @throws IOException if directory access fails
     */
    public List<String> listDirectory(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        
        if (!Files.exists(path)) {
            throw new IOException("Directory does not exist: " + directoryPath);
        }
        
        if (!Files.isDirectory(path)) {
            throw new IOException("Path is not a directory: " + directoryPath);
        }
        
        try (Stream<Path> paths = Files.list(path)) {
            return paths
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Executes a platform-agnostic command using ProcessBuilder.
     * Automatically detects the operating system and uses appropriate shell.
     * 
     * @param command The command to execute
     * @return Command output as a list of lines
     * @throws IOException if command execution fails
     * @throws InterruptedException if command is interrupted
     */
    public List<String> executeCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        
        // Detect OS and use appropriate shell
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("sh", "-c", command);
        }
        
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        List<String> output = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        }
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command failed with exit code: " + exitCode);
        }
        
        return output;
    }
    
    /**
     * Gets system information using platform-agnostic Java System properties.
     * 
     * @return System information string
     */
    public String getSystemInfo() {
        return String.format("OS: %s %s, Java: %s, User: %s, Working Dir: %s",
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("java.version"),
            System.getProperty("user.name"),
            System.getProperty("user.dir")
        );
    }
}
