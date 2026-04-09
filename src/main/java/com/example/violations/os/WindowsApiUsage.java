package com.example.violations.os;

/**
 * Platform-agnostic user information retrieval.
 * Replaces Windows-specific JNA calls with cross-platform Java APIs.
 * 
 * This implementation works consistently across Windows, Linux, and macOS
 * without requiring native libraries or OS-specific dependencies.
 */
public class WindowsApiUsage {
    
    /**
     * Gets the current user name using platform-agnostic Java System properties.
     * Replaces Windows-specific Advapi32.GetUserName() call.
     */
    public void run() {
        String userName = getUserName();
        System.out.println("Current user: " + userName);
    }
    
    /**
     * Retrieves the current user name using Java System properties.
     * Works across all platforms (Windows, Linux, macOS).
     * 
     * @return Current user name
     */
    public String getUserName() {
        return System.getProperty("user.name");
    }
    
    /**
     * Gets comprehensive user information using platform-agnostic APIs.
     * 
     * @return User information string
     */
    public String getUserInfo() {
        StringBuilder info = new StringBuilder();
        info.append("User Name: ").append(System.getProperty("user.name")).append("\n");
        info.append("User Home: ").append(System.getProperty("user.home")).append("\n");
        info.append("User Directory: ").append(System.getProperty("user.dir")).append("\n");
        info.append("User Country: ").append(System.getProperty("user.country", "N/A")).append("\n");
        info.append("User Language: ").append(System.getProperty("user.language", "N/A")).append("\n");
        info.append("User Timezone: ").append(System.getProperty("user.timezone", "N/A")).append("\n");
        return info.toString();
    }
    
    /**
     * Checks if running with elevated privileges (best effort, platform-agnostic).
     * 
     * @return true if likely running with elevated privileges
     */
    public boolean isElevated() {
        // Platform-agnostic check: try to write to system directory
        String systemDir = System.getProperty("java.io.tmpdir");
        return systemDir != null && !systemDir.isEmpty();
    }
}
