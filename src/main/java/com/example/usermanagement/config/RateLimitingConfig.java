package com.example.usermanagement.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitingInterceptor());
    }

    private class RateLimitingInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if ("/api/auth/login".equals(request.getRequestURI())) {
                String clientIp = request.getRemoteAddr();
                Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());

                if (bucket.tryConsume(1)) {
                    return true;
                } else {
                    response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
                    response.getWriter().write("Too many requests. Please try again later.");
                    return false;
                }
            }
            return true;
        }

        private Bucket createNewBucket() {
            Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
            return Bucket.builder().addLimit(limit).build();
        }
    }
}