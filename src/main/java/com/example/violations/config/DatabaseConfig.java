package com.example.violations.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Cloud-ready database configuration using connection pooling.
 * Reads configuration from environment variables for cloud deployment.
 */
public class DatabaseConfig {
    
    /**
     * Creates a HikariCP DataSource configured for cloud environments.
     * All configuration is read from environment variables.
     * 
     * @return Configured DataSource with connection pooling
     */
    public static DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        
        // Read database configuration from environment variables
        String dbUrl = System.getenv().getOrDefault("DB_URL", 
            "jdbc:postgresql://localhost:5432/appdb");
        String dbUsername = System.getenv().getOrDefault("DB_USERNAME", "postgres");
        String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "postgres");
        
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        
        // Cloud-optimized connection pool settings
        config.setMaximumPoolSize(Integer.parseInt(
            System.getenv().getOrDefault("DB_POOL_SIZE", "10")));
        config.setMinimumIdle(Integer.parseInt(
            System.getenv().getOrDefault("DB_MIN_IDLE", "2")));
        config.setConnectionTimeout(Long.parseLong(
            System.getenv().getOrDefault("DB_CONN_TIMEOUT", "30000")));
        config.setIdleTimeout(Long.parseLong(
            System.getenv().getOrDefault("DB_IDLE_TIMEOUT", "600000")));
        config.setMaxLifetime(Long.parseLong(
            System.getenv().getOrDefault("DB_MAX_LIFETIME", "1800000")));
        
        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        
        // Pool name for monitoring
        config.setPoolName("CloudAppPool");
        
        // Enable metrics for cloud monitoring
        config.setRegisterMbeans(true);
        
        return new HikariDataSource(config);
    }
    
    /**
     * Creates the file_storage table if it doesn't exist.
     * This supports the database-backed file storage pattern.
     * 
     * @param dataSource The DataSource to use
     */
    public static void initializeSchema(DataSource dataSource) {
        String createTableSql = 
            "CREATE TABLE IF NOT EXISTS file_storage (" +
            "  id SERIAL PRIMARY KEY," +
            "  file_name VARCHAR(255) UNIQUE NOT NULL," +
            "  content BYTEA NOT NULL," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }
}
