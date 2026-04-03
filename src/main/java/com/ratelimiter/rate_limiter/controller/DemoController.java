package com.ratelimiter.rate_limiter.controller;

import com.ratelimiter.rate_limiter.annotation.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Demo API", description = "Endpoints protected by rate limiter")
public class DemoController {

    @GetMapping("/search")
    @RateLimit(limit = 5, window = 60)
    @Operation(
            summary = "Search endpoint",
            description = "Rate limited to 5 requests per 60 seconds per client"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests")
    })
    public Map<String, String> search(
            @Parameter(description = "Search query")
            @RequestParam String q) {
        Map<String, String> response = new HashMap<>();
        response.put("query", q);
        response.put("results", "Found 10 results for: " + q);
        response.put("time", Instant.now().toString());
        return response;
    }

    @PostMapping("/login")
    @RateLimit(limit = 3, window = 60)
    @Operation(
            summary = "Login endpoint",
            description = "Rate limited to 3 requests per 60 seconds per client"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests")
    })
    public Map<String, String> login(
            @RequestBody Map<String, String> body) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful");
        String user = body.get("username");
        response.put("user", user != null ? user : "unknown");
        return response;
    }

    @GetMapping("/data")
    @RateLimit(limit = 10, window = 30)
    @Operation(
            summary = "Data endpoint",
            description = "Rate limited to 10 requests per 30 seconds per client"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data returned"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests")
    })
    public Map<String, String> getData() {
        Map<String, String> response = new HashMap<>();
        response.put("data", "Here is your data!");
        response.put("time", Instant.now().toString());
        return response;
    }

    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "No rate limit — always available"
    )
    @ApiResponse(responseCode = "200", description = "Service is UP")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "rate-limiter");
        return response;
    }
}
