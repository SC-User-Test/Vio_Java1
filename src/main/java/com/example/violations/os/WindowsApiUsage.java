package com.example.violations.os;

/**
 * Cloud-ready system operations using platform-agnostic Java APIs.
 * Replaces Windows-specific JNA calls with cross-platform alternatives.
 */
public class WindowsApiUsage {
    
    /**
     * Gets the current user name using platform-agnostic Java System properties.
     * This replaces Windows-specific Advapi32.GetUserName() calls.
     * 
     * @return The current user name
     */
    public String getUserName() {
        return System.getProperty("user.name");
    }
    
    /**
     * Gets the user's home directory using platform-agnostic Java System properties.
     * 
     * @return The user's home directory path
     */
    public String getUserHome() {
        return System.getProperty("user.home");
    }
    
    /**
     * Gets the operating system name.
     * 
     * @return The OS name
     */
    public String getOsName() {
        return System.getProperty("os.name");
    }
    
    /**
     * Gets the operating system version.
     * 
     * @return The OS version
     */
    public String getOsVersion() {
        return System.getProperty("os.version");
    }
    
    /**
     * Gets the system architecture.
     * 
     * @return The system architecture (e.g., amd64, x86)
     */
    public String getOsArch() {
        return System.getProperty("os.arch");
    }
    
    /**
     * Checks if the current OS is Windows.
     * 
     * @return true if running on Windows, false otherwise
     */
    public boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }
    
    /**
     * Checks if the current OS is Linux.
     * 
     * @return true if running on Linux, false otherwise
     */
    public boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("nux") || os.contains("nix");
    }
    
    /**
     * Gets comprehensive system information.
     * 
     * @return System information string
     */
    public String getSystemInfo() {
        return String.format(
            "User: %s, Home: %s, OS: %s %s (%s), Java: %s",
            getUserName(),
            getUserHome(),
            getOsName(),
            getOsVersion(),
            getOsArch(),
            System.getProperty("java.version")
        );
    }
}
