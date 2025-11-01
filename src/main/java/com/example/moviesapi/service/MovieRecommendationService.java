package com.example.moviesapi.service;

import com.example.moviesapi.model.Movie;
import com.example.moviesapi.model.Genre;
import com.example.moviesapi.model.Actor;
import com.example.moviesapi.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieRecommendationService {

    @Autowired
    private MovieRepository movieRepository;

    /**
     * Get movie recommendations based on a favorite movie
     * Finds movies with similar genres, actors, or release years
     */
    public List<Movie> getRecommendationsByMovie(Long movieId, int limit) {
        Optional<Movie> favoriteMovie = movieRepository.findById(movieId);
        if (favoriteMovie.isEmpty()) {
            return Collections.emptyList();
        }

        Movie movie = favoriteMovie.get();
        Set<String> favoriteGenres = movie.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());
        
        Set<String> favoriteActors = movie.getActors().stream()
                .map(Actor::getName)
                .collect(Collectors.toSet());
        
        Integer favoriteYear = movie.getReleaseYear();

        // Find movies with similar characteristics
        List<Movie> allMovies = movieRepository.findAll();
        
        return allMovies.stream()
                .filter(m -> !m.getId().equals(movieId)) // Exclude the favorite movie itself
                .sorted((m1, m2) -> {
                    // Calculate similarity score
                    int score1 = calculateSimilarityScore(m1, favoriteGenres, favoriteActors, favoriteYear);
                    int score2 = calculateSimilarityScore(m2, favoriteGenres, favoriteActors, favoriteYear);
                    return Integer.compare(score2, score1); // Sort by score descending
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    
    private int calculateSimilarityScore(Movie movie, Set<String> favoriteGenres, 
                                       Set<String> favoriteActors, Integer favoriteYear) {
        int score = 0;
        
        // Genre similarity (2 points per matching genre)
        Set<String> movieGenres = movie.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());
        for (String genre : movieGenres) {
            if (favoriteGenres.contains(genre)) {
                score += 2;
            }
        }
        
        // Actor similarity (3 points per matching actor)
        Set<String> movieActors = movie.getActors().stream()
                .map(Actor::getName)
                .collect(Collectors.toSet());
        for (String actor : movieActors) {
            if (favoriteActors.contains(actor)) {
                score += 3;
            }
        }
        
        // Release year similarity (1 point for same decade)
        if (favoriteYear != null && movie.getReleaseYear() != null) {
            int decade1 = favoriteYear / 10;
            int decade2 = movie.getReleaseYear() / 10;
            if (decade1 == decade2) {
                score += 1;
            }
        }
        
        return score;
    }

    /**
     * Get trending movies (most actors + recent)
     */
    public List<Movie> getTrendingMovies(int limit) {
        List<Movie> allMovies = movieRepository.findAll();
        
        return allMovies.stream()
                .sorted((m1, m2) -> {
                    // Score based on actor count and recency
                    int score1 = m1.getActors().size() + (m1.getReleaseYear() - 2000) / 10;
                    int score2 = m2.getActors().size() + (m2.getReleaseYear() - 2000) / 10;
                    return Integer.compare(score2, score1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get movies by mood/category
     */
    public List<Movie> getMoviesByMood(String mood, int limit) {
        Map<String, List<String>> moodToGenres = Map.of(
            "action", Arrays.asList("Action", "Adventure", "Thriller"),
            "comedy", Arrays.asList("Comedy", "Romance"),
            "drama", Arrays.asList("Drama", "Romance"),
            "sci-fi", Arrays.asList("Sci-Fi", "Fantasy"),
            "horror", Arrays.asList("Horror", "Thriller"),
            "family", Arrays.asList("Adventure", "Fantasy", "Comedy")
        );

        List<String> targetGenres = moodToGenres.getOrDefault(mood.toLowerCase(), 
            Arrays.asList("Drama")); // Default to Drama if mood not found

        return movieRepository.findAll().stream()
                .filter(movie -> movie.getGenres().stream()
                        .anyMatch(genre -> targetGenres.contains(genre.getName())))
                .sorted((m1, m2) -> Integer.compare(m2.getReleaseYear(), m1.getReleaseYear())) // Recent first
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get director's filmography style recommendations
     * (Simulating director-based recommendations)
     */
    public List<Movie> getSimilarDirectorsStyle(Long movieId, int limit) {
       
        Map<String, List<String>> movieToSimilarTitles = Map.of(
            "The Matrix", Arrays.asList("Inception", "The Dark Knight", "Interstellar"),
            "Forrest Gump", Arrays.asList("The Shawshank Redemption", "Pulp Fiction", "Goodfellas"),
            "The Avengers", Arrays.asList("Black Panther", "The Dark Knight", "Inception"),
            "Inception", Arrays.asList("The Matrix", "Interstellar", "The Dark Knight")
        );

        Optional<Movie> movie = movieRepository.findById(movieId);
        if (movie.isEmpty()) {
            return Collections.emptyList();
        }

        String title = movie.get().getTitle();
        List<String> similarTitles = movieToSimilarTitles.getOrDefault(title, 
            Arrays.asList("The Shawshank Redemption", "Pulp Fiction", "The Dark Knight"));

        return movieRepository.findByTitleIn(similarTitles);
    }
}