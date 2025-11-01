// src/main/java/com/example/moviesapi/controller/HomeController.java
package com.example.moviesapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
            "message", "🎬 Movies API is running successfully!",
            "status", "OK",
            "version", "1.0.0",
            "endpoints", Map.of(
                "movies", "/api/movies",
                "actors", "/api/actors", 
                "genres", "/api/genres",
                "recommendations", "/api/recommendations",
                "h2-console", "/h2-console"
            ),
            "documentation", "Check README.md for complete API documentation"
        ));
    }
}