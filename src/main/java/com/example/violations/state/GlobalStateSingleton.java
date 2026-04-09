package com.example.violations.state;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cloud-native stateless counter implementation.
 * Replaces static mutable state with thread-safe, stateless patterns.
 * 
 * For true cloud-native applications, state should be:
 * - Externalized to distributed caches (Redis, Memcached)
 * - Stored in databases for persistence
 * - Managed by stateless services with external state stores
 * 
 * This implementation provides thread-safety for single-instance scenarios,
 * but for multi-instance cloud deployments, use external state management.
 */
public class GlobalStateSingleton {
    
    // Use AtomicInteger for thread-safe operations (better than static int)
    private static final AtomicInteger counter = new AtomicInteger(0);
    
    /**
     * Increments the counter in a thread-safe manner.
     * 
     * @return The new counter value
     */
    public static int increment() {
        return counter.incrementAndGet();
    }
    
    /**
     * Gets the current counter value.
     * 
     * @return Current counter value
     */
    public static int getCounter() {
        return counter.get();
    }
    
    /**
     * Resets the counter to zero.
     */
    public static void reset() {
        counter.set(0);
    }
    
    /**
     * Sets the counter to a specific value.
     * 
     * @param value New counter value
     */
    public static void setCounter(int value) {
        counter.set(value);
    }
    
    /**
     * Decrements the counter in a thread-safe manner.
     * 
     * @return The new counter value
     */
    public static int decrement() {
        return counter.decrementAndGet();
    }
}

/**
 * CLOUD-NATIVE RECOMMENDATION:
 * 
 * For production cloud deployments, replace this with:
 * 
 * 1. Redis/Memcached for distributed caching:
 *    - Use Spring Data Redis or Jedis client
 *    - Provides shared state across multiple instances
 *    - Supports TTL and automatic expiration
 * 
 * 2. DynamoDB for persistent state:
 *    - Use AWS SDK for DynamoDB
 *    - Provides durable, scalable state storage
 *    - Supports atomic operations
 * 
 * 3. Stateless design with request-scoped data:
 *    - Pass state in requests/responses
 *    - Use JWT tokens for session data
 *    - Store state in databases, not memory
 * 
 * Example with Redis:
 * 
 * @Service
 * public class CounterService {
 *     @Autowired
 *     private StringRedisTemplate redisTemplate;
 *     
 *     public Long increment(String key) {
 *         return redisTemplate.opsForValue().increment(key);
 *     }
 * }
 */
