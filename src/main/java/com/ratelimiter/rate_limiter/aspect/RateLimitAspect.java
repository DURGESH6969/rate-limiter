package com.ratelimiter.rate_limiter.aspect;

import com.ratelimiter.rate_limiter.algorithm.SlidingWindowCounter;
import com.ratelimiter.rate_limiter.annotation.RateLimit;
import com.ratelimiter.rate_limiter.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RateLimitAspect {

    private final SlidingWindowCounter slidingWindowCounter;

    public RateLimitAspect(SlidingWindowCounter slidingWindowCounter) {
        this.slidingWindowCounter = slidingWindowCounter;
    }

    @Around("@annotation(rateLimit)")
    public Object applyRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        String clientKey = extractClientKey(request);
        String endpoint = request.getRequestURI();

        boolean allowed = slidingWindowCounter.isAllowed(clientKey, endpoint, rateLimit.limit(), rateLimit.window());

        if (!allowed) {

            throw new RateLimitExceededException("Rate limit exceeded. Max " + rateLimit.limit() + " requests per " + rateLimit.window() + " seconds.");
        }


        long remaining = slidingWindowCounter.getRemainingRequests(clientKey, endpoint, rateLimit.limit(), rateLimit.window());
        response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Window", rateLimit.window() + "s");

        return joinPoint.proceed();
    }

    private String extractClientKey(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return apiKey;
        }
        return request.getRemoteAddr();
    }
}