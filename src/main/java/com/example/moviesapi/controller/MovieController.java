package com.example.moviesapi.controller;

import com.example.moviesapi.model.Actor;
import com.example.moviesapi.model.Genre;
import com.example.moviesapi.model.Movie;
import com.example.moviesapi.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    // CREATE with relationships
    @PostMapping("/with-relationships")
    public ResponseEntity<Movie> createMovieWithRelations(
            @Valid @RequestBody Movie movie,
            @RequestParam(required = false) List<Long> genreIds,
            @RequestParam(required = false) List<Long> actorIds) {
        Movie createdMovie = movieService.createMovieWithRelations(movie, genreIds, actorIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    // READ - All movies
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    // READ - All movies with pagination (required functionality)
    @GetMapping("/paged")
    public ResponseEntity<Page<Movie>> getAllMoviesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(movies);
    }

    // READ - Movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    // READ - Actors by movie ID (required functionality)
    @GetMapping("/{id}/actors")
    public ResponseEntity<List<Actor>> getActorsByMovieId(@PathVariable Long id) {
        List<Actor> actors = movieService.getActorsByMovieId(id);
        return ResponseEntity.ok(actors);
    }

    // READ - Genres by movie ID
    @GetMapping("/{id}/genres")
    public ResponseEntity<List<Genre>> getGenresByMovieId(@PathVariable Long id) {
        List<Genre> genres = movieService.getGenresByMovieId(id);
        return ResponseEntity.ok(genres);
    }

    // FILTERING - Movies by genre ID (required functionality)
    @GetMapping("/by-genre/{genreId}")
    public ResponseEntity<Page<Movie>> getMoviesByGenreId(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieService.getMoviesByGenreId(genreId, pageable);
        return ResponseEntity.ok(movies);
    }

    // FILTERING - Movies by actor ID (required functionality)
    @GetMapping("/by-actor/{actorId}")
    public ResponseEntity<Page<Movie>> getMoviesByActorId(
            @PathVariable Long actorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieService.getMoviesByActorId(actorId, pageable);
        return ResponseEntity.ok(movies);
    }

    // FILTERING - Movies by release year (required functionality)
    @GetMapping("/by-year/{releaseYear}")
    public ResponseEntity<Page<Movie>> getMoviesByReleaseYear(
            @PathVariable Integer releaseYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieService.getMoviesByReleaseYear(releaseYear, pageable);
        return ResponseEntity.ok(movies);
    }

    // SEARCH - Movies by title (required functionality)
    @GetMapping("/search")
    public ResponseEntity<Page<Movie>> searchMoviesByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieService.searchMoviesByTitle(title, pageable);
        return ResponseEntity.ok(movies);
    }

    // ADVANCED SEARCH
    @GetMapping("/advanced-search")
    public ResponseEntity<Page<Movie>> advancedSearchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieService.advancedSearch(title, minYear, maxYear, minDuration, maxDuration, pageable);
        return ResponseEntity.ok(movies);
    }

    // UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody Movie movieDetails) {
        Movie updatedMovie = movieService.updateMovie(id, movieDetails);
        return ResponseEntity.ok(updatedMovie);
    }

    // UPDATE relationships
    @PatchMapping("/{id}/relationships")
    public ResponseEntity<Movie> updateMovieRelations(
            @PathVariable Long id,
            @RequestParam(required = false) List<Long> genreIds,
            @RequestParam(required = false) List<Long> actorIds) {
        Movie updatedMovie = movieService.updateMovieRelations(id, genreIds, actorIds);
        return ResponseEntity.ok(updatedMovie);
    }

    // ADD genres to movie
    @PostMapping("/{id}/genres")
    public ResponseEntity<Movie> addGenresToMovie(
            @PathVariable Long id,
            @RequestBody List<Long> genreIds) {
        Movie updatedMovie = movieService.addGenresToMovie(id, genreIds);
        return ResponseEntity.ok(updatedMovie);
    }

    // REMOVE genres from movie
    @DeleteMapping("/{id}/genres")
    public ResponseEntity<Movie> removeGenresFromMovie(
            @PathVariable Long id,
            @RequestBody List<Long> genreIds) {
        Movie updatedMovie = movieService.removeGenresFromMovie(id, genreIds);
        return ResponseEntity.ok(updatedMovie);
    }

    // ADD actors to movie
    @PostMapping("/{id}/actors")
    public ResponseEntity<Movie> addActorsToMovie(
            @PathVariable Long id,
            @RequestBody List<Long> actorIds) {
        Movie updatedMovie = movieService.addActorsToMovie(id, actorIds);
        return ResponseEntity.ok(updatedMovie);
    }

    // REMOVE actors from movie
    @DeleteMapping("/{id}/actors")
    public ResponseEntity<Movie> removeActorsFromMovie(
            @PathVariable Long id,
            @RequestBody List<Long> actorIds) {
        Movie updatedMovie = movieService.removeActorsFromMovie(id, actorIds);
        return ResponseEntity.ok(updatedMovie);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force) {
        movieService.deleteMovie(id, force);
        return ResponseEntity.noContent().build();
    }

    // STATISTICS - Movies with actor count
    @GetMapping("/stats/with-actor-count")
    public ResponseEntity<Page<Object[]>> getAllMoviesWithActorCount(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> moviesWithCount = movieService.getAllMoviesWithActorCount(pageable);
        return ResponseEntity.ok(moviesWithCount);
    }

    // STATISTICS - Latest movies
    @GetMapping("/stats/latest")
    public ResponseEntity<Page<Movie>> getLatestMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> latestMovies = movieService.getLatestMovies(pageable);
        return ResponseEntity.ok(latestMovies);
    }

    // STATISTICS - Movies by duration range
    @GetMapping("/stats/by-duration")
    public ResponseEntity<List<Movie>> getMoviesByDurationRange(
            @RequestParam Integer minDuration,
            @RequestParam Integer maxDuration) {
        List<Movie> movies = movieService.getMoviesByDurationRange(minDuration, maxDuration);
        return ResponseEntity.ok(movies);
    }

    // STATISTICS - Movies with no genres
    @GetMapping("/stats/no-genres")
    public ResponseEntity<List<Movie>> getMoviesWithNoGenres() {
        List<Movie> movies = movieService.getMoviesWithNoGenres();
        return ResponseEntity.ok(movies);
    }

    // STATISTICS - Movies with no actors
    @GetMapping("/stats/no-actors")
    public ResponseEntity<List<Movie>> getMoviesWithNoActors() {
        List<Movie> movies = movieService.getMoviesWithNoActors();
        return ResponseEntity.ok(movies);
    }

    // CHECK EXISTENCE
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> movieExists(@PathVariable Long id) {
        boolean exists = movieService.movieExists(id);
        return ResponseEntity.ok(exists);
    }
}