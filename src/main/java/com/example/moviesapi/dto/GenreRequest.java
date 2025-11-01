package com.example.moviesapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class GenreRequest {
    
    @NotBlank(message = "Genre name is required")
    @Size(max = 100, message = "Genre name must not exceed 100 characters")
    private String name;

    private List<Long> movieIds = new ArrayList<>();

    // Constructors
    public GenreRequest() {}

    public GenreRequest(String name) {
        this.name = name;
    }

    public GenreRequest(String name, List<Long> movieIds) {
        this.name = name;
        this.movieIds = movieIds != null ? movieIds : new ArrayList<>();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getMovieIds() {
        return movieIds;
    }

    public void setMovieIds(List<Long> movieIds) {
        this.movieIds = movieIds != null ? movieIds : new ArrayList<>();
    }

    // Validation methods
    public boolean hasMovieIds() {
        return movieIds != null && !movieIds.isEmpty();
    }

    // Helper methods for name validation
    public String getNormalizedName() {
        if (name == null) return null;
        return name.trim();
    }

    public void validateName() {
        if (name != null) {
            String normalized = getNormalizedName();
            if (normalized.isEmpty()) {
                throw new IllegalArgumentException("Genre name cannot be empty or whitespace only");
            }
            if (normalized.length() > 100) {
                throw new IllegalArgumentException("Genre name must not exceed 100 characters");
            }
        }
    }

    // Static validation helper
    public static boolean isValidGenreName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() <= 100;
    }

    @Override
    public String toString() {
        return "GenreRequest{" +
                "name='" + name + '\'' +
                ", movieIds=" + movieIds +
                '}';
    }
}