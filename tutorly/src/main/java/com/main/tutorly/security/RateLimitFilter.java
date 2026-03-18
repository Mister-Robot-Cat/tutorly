package com.main.tutorly.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastResetTime = new ConcurrentHashMap<>();
    
    private static final int AUTH_RATE_LIMIT = 5; // 5 requests per minute
    private static final int GENERAL_RATE_LIMIT = 100; // 100 requests per minute
    private static final int BOOKING_RATE_LIMIT = 20; // 20 requests per minute
    private static final long TIME_WINDOW_MS = 60 * 1000; // 1 minute

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIp(request);
        String path = request.getRequestURI();
        
        int rateLimit;
        String key;
        
        // Select rate limit based on endpoint
        if (path.contains("/api/auth/")) {
            rateLimit = AUTH_RATE_LIMIT;
            key = "auth:" + clientIp;
        } else if (path.contains("/api/bookings")) {
            rateLimit = BOOKING_RATE_LIMIT;
            key = "booking:" + clientIp;
        } else {
            rateLimit = GENERAL_RATE_LIMIT;
            key = "general:" + clientIp;
        }

        // Skip rate limiting for actuator endpoints
        if (path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isRateLimited(key, rateLimit)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
            return;
        }

        // Add rate limit headers
        AtomicInteger count = requestCounts.get(key);
        if (count != null) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(Math.max(0, rateLimit - count.get())));
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(String key, int rateLimit) {
        long currentTime = System.currentTimeMillis();
        Long lastReset = lastResetTime.get(key);
        
        // Reset counter if time window has passed
        if (lastReset == null || (currentTime - lastReset) > TIME_WINDOW_MS) {
            requestCounts.put(key, new AtomicInteger(1));
            lastResetTime.put(key, currentTime);
            return false;
        }
        
        AtomicInteger count = requestCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
        int currentCount = count.incrementAndGet();
        
        return currentCount > rateLimit;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
