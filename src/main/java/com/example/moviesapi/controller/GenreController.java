package com.example.moviesapi.controller;

import com.example.moviesapi.model.Genre;
import com.example.moviesapi.model.Movie;
import com.example.moviesapi.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/genres")
@CrossOrigin(origins = "*")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Genre> createGenre(@Valid @RequestBody Genre genre) {
        Genre createdGenre = genreService.createGenre(genre);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
    }

    // READ - All genres
    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    // READ - All genres with pagination
    @GetMapping("/paged")
    public ResponseEntity<Page<Genre>> getAllGenresPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Genre> genres = genreService.getAllGenres(pageable);
        return ResponseEntity.ok(genres);
    }

    // READ - Genre by ID
    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable Long id) {
        Genre genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    // READ - Movies by genre ID
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<Movie>> getMoviesByGenreId(@PathVariable Long id) {
        List<Movie> movies = genreService.getMoviesByGenreId(id);
        return ResponseEntity.ok(movies);
    }

    // READ - Search genres by name
    @GetMapping("/search")
    public ResponseEntity<Page<Genre>> searchGenresByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Genre> genres = genreService.searchGenresByName(name, pageable);
        return ResponseEntity.ok(genres);
    }

    // READ - Genres with movie count
    @GetMapping("/stats/with-movie-count")
    public ResponseEntity<Page<Object[]>> getAllGenresWithMovieCount(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> genresWithCount = genreService.getAllGenresWithMovieCount(pageable);
        return ResponseEntity.ok(genresWithCount);
    }

    // READ - Top genres by movie count
    @GetMapping("/stats/top-by-movies")
    public ResponseEntity<Page<Object[]>> getTopGenresByMovieCount(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> topGenres = genreService.getTopGenresByMovieCount(pageable);
        return ResponseEntity.ok(topGenres);
    }

    // READ - Genres with no movies
    @GetMapping("/stats/no-movies")
    public ResponseEntity<List<Genre>> getGenresWithNoMovies() {
        List<Genre> genres = genreService.getGenresWithNoMovies();
        return ResponseEntity.ok(genres);
    }

    // UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(
            @PathVariable Long id,
            @Valid @RequestBody Genre genreDetails) {
        Genre updatedGenre = genreService.updateGenre(id, genreDetails);
        return ResponseEntity.ok(updatedGenre);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force) {
        genreService.deleteGenre(id, force);
        return ResponseEntity.noContent().build();
    }

    // BULK CREATE
    @PostMapping("/bulk")
    public ResponseEntity<List<Genre>> createGenres(@Valid @RequestBody List<Genre> genres) {
        List<Genre> createdGenres = genreService.createGenres(genres);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenres);
    }

    // CHECK EXISTENCE
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> genreExists(@PathVariable Long id) {
        boolean exists = genreService.genreExists(id);
        return ResponseEntity.ok(exists);
    }

    // FIND BY NAME
    @GetMapping("/by-name/{name}")
    public ResponseEntity<Genre> getGenreByName(@PathVariable String name) {
        Optional<Genre> genre = genreService.getGenreByName(name);
        return genre.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}