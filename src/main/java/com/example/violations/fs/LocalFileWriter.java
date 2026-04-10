package com.example.violations.fs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Cloud-ready file storage using database persistence instead of local file system.
 * This approach ensures data durability and scalability in cloud environments.
 */
public class LocalFileWriter {
    
    private final DataSource dataSource;
    
    public LocalFileWriter(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * Writes data to database storage instead of local file system.
     * Uses BLOB column for binary data or TEXT column for text data.
     * 
     * @param fileName The logical file name
     * @param content The content to store
     * @throws SQLException if database operation fails
     */
    public void write(String fileName, byte[] content) throws SQLException {
        String sql = "INSERT INTO file_storage (file_name, content, created_at) VALUES (?, ?, CURRENT_TIMESTAMP) " +
                     "ON CONFLICT (file_name) DO UPDATE SET content = EXCLUDED.content, updated_at = CURRENT_TIMESTAMP";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fileName);
            stmt.setBytes(2, content);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Writes text data to database storage.
     * 
     * @param fileName The logical file name
     * @param content The text content to store
     * @throws SQLException if database operation fails
     */
    public void write(String fileName, String content) throws SQLException {
        write(fileName, content.getBytes());
    }
}
