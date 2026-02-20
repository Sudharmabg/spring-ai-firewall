package com.sudharma.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {
    
    @GetMapping("/public/hello")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "Hello from public endpoint");
    }
    
    @PostMapping("/auth/login")
    public Map<String, String> sensitiveEndpoint() {
        return Map.of("message", "Login successful", "token", "demo-token-123");
    }
    
    @GetMapping("/data")
    public Map<String, Object> dataEndpoint() {
        return Map.of(
            "status", "success",
            "data", Map.of("id", 1, "name", "Sample Data")
        );
    }
}
