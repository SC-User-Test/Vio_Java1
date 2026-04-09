package com.example.violations.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Cloud-native session store with thread-safety and TTL support.
 * Replaces static HashMap with ConcurrentHashMap for thread-safety.
 * 
 * IMPORTANT: For production cloud deployments, use external session stores:
 * - Redis for distributed session management
 * - DynamoDB for persistent sessions
 * - Spring Session with Redis/JDBC
 * - JWT tokens for stateless authentication
 * 
 * This implementation is suitable for:
 * - Development/testing environments
 * - Single-instance deployments
 * - Migration path to distributed sessions
 */
public class InMemorySessionStore {
    
    // Thread-safe map for concurrent access
    private static final Map<String, SessionData> SESSIONS = new ConcurrentHashMap<>();
    
    // Session timeout in seconds (configurable via environment variable)
    private static final long SESSION_TIMEOUT_SECONDS = 
        Long.parseLong(System.getenv().getOrDefault("SESSION_TIMEOUT_SECONDS", "1800")); // 30 minutes default
    
    // Cleanup scheduler for expired sessions
    private static final ScheduledExecutorService cleanupScheduler = 
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "session-cleanup");
            t.setDaemon(true);
            return t;
        });
    
    static {
        // Schedule periodic cleanup of expired sessions
        cleanupScheduler.scheduleAtFixedRate(
            InMemorySessionStore::cleanupExpiredSessions,
            1, 1, TimeUnit.MINUTES
        );
    }
    
    /**
     * Session data with expiration timestamp.
     */
    private static class SessionData {
        private final String value;
        private final long expirationTime;
        
        public SessionData(String value, long timeoutSeconds) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + (timeoutSeconds * 1000);
        }
        
        public String getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
    
    /**
     * Stores a session with automatic expiration.
     * 
     * @param sessionId Session identifier
     * @param value Session data
     */
    public static void put(String sessionId, String value) {
        if (sessionId == null || value == null) {
            throw new IllegalArgumentException("Session ID and value cannot be null");
        }
        SESSIONS.put(sessionId, new SessionData(value, SESSION_TIMEOUT_SECONDS));
    }
    
    /**
     * Retrieves a session value if it exists and hasn't expired.
     * 
     * @param sessionId Session identifier
     * @return Session value or null if not found or expired
     */
    public static String get(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        
        SessionData data = SESSIONS.get(sessionId);
        if (data == null) {
            return null;
        }
        
        if (data.isExpired()) {
            SESSIONS.remove(sessionId);
            return null;
        }
        
        return data.getValue();
    }
    
    /**
     * Removes a session.
     * 
     * @param sessionId Session identifier
     */
    public static void remove(String sessionId) {
        if (sessionId != null) {
            SESSIONS.remove(sessionId);
        }
    }
    
    /**
     * Checks if a session exists and is valid.
     * 
     * @param sessionId Session identifier
     * @return true if session exists and hasn't expired
     */
    public static boolean exists(String sessionId) {
        return get(sessionId) != null;
    }
    
    /**
     * Gets the number of active sessions.
     * 
     * @return Number of active sessions
     */
    public static int size() {
        cleanupExpiredSessions();
        return SESSIONS.size();
    }
    
    /**
     * Clears all sessions.
     */
    public static void clear() {
        SESSIONS.clear();
    }
    
    /**
     * Removes expired sessions from the store.
     */
    private static void cleanupExpiredSessions() {
        SESSIONS.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Shuts down the cleanup scheduler.
     * Call this when the application is shutting down.
     */
    public static void shutdown() {
        cleanupScheduler.shutdown();
        try {
            if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

/**
 * CLOUD-NATIVE MIGRATION PATH:
 * 
 * For production cloud deployments, migrate to:
 * 
 * 1. Spring Session with Redis:
 * 
 * @Configuration
 * @EnableRedisHttpSession
 * public class SessionConfig {
 *     @Bean
 *     public LettuceConnectionFactory connectionFactory() {
 *         return new LettuceConnectionFactory(
 *             System.getenv("REDIS_HOST"),
 *             Integer.parseInt(System.getenv("REDIS_PORT"))
 *         );
 *     }
 * }
 * 
 * 2. AWS DynamoDB Sessions:
 * 
 * @Service
 * public class DynamoDBSessionStore {
 *     @Autowired
 *     private DynamoDbClient dynamoDb;
 *     
 *     public void saveSession(String sessionId, String data) {
 *         // Store in DynamoDB with TTL
 *     }
 * }
 * 
 * 3. Stateless JWT Tokens:
 * 
 * @Service
 * public class JwtTokenService {
 *     public String createToken(Map<String, Object> claims) {
 *         return Jwts.builder()
 *             .setClaims(claims)
 *             .signWith(key)
 *             .compact();
 *     }
 * }
 */
