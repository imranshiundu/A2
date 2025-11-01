package com.example.moviesapi.controller;

import com.example.moviesapi.model.Actor;
import com.example.moviesapi.model.Movie;
import com.example.moviesapi.service.ActorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/actors")
@CrossOrigin(origins = "*")
public class ActorController {

    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Actor> createActor(@Valid @RequestBody Actor actor) {
        Actor createdActor = actorService.createActor(actor);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActor);
    }

    // READ - All actors
    @GetMapping
    public ResponseEntity<List<Actor>> getAllActors() {
        List<Actor> actors = actorService.getAllActors();
        return ResponseEntity.ok(actors);
    }

    // READ - All actors with pagination
    @GetMapping("/paged")
    public ResponseEntity<Page<Actor>> getAllActorsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Actor> actors = actorService.getAllActors(pageable);
        return ResponseEntity.ok(actors);
    }

    // READ - Actor by ID
    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable Long id) {
        Actor actor = actorService.getActorById(id);
        return ResponseEntity.ok(actor);
    }

    // READ - Movies by actor ID
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<Movie>> getMoviesByActorId(@PathVariable Long id) {
        List<Movie> movies = actorService.getMoviesByActorId(id);
        return ResponseEntity.ok(movies);
    }

    // READ - Search actors by name (required functionality)
    @GetMapping("/search")
    public ResponseEntity<Page<Actor>> searchActorsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Actor> actors = actorService.searchActorsByName(name, pageable);
        return ResponseEntity.ok(actors);
    }

    // READ - Advanced search with multiple criteria
    @GetMapping("/advanced-search")
    public ResponseEntity<Page<Actor>> advancedSearchActors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate minBirthDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate maxBirthDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Actor> actors = actorService.searchActors(name, minBirthDate, maxBirthDate, pageable);
        return ResponseEntity.ok(actors);
    }

    // READ - Actors by birth date range
    @GetMapping("/by-birthdate")
    public ResponseEntity<List<Actor>> getActorsByBirthDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Actor> actors = actorService.getActorsByBirthDateRange(startDate, endDate);
        return ResponseEntity.ok(actors);
    }

    // READ - Actors with movie count
    @GetMapping("/stats/with-movie-count")
    public ResponseEntity<Page<Object[]>> getAllActorsWithMovieCount(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> actorsWithCount = actorService.getAllActorsWithMovieCount(pageable);
        return ResponseEntity.ok(actorsWithCount);
    }

    // READ - Top actors by movie count
    @GetMapping("/stats/top-by-movies")
    public ResponseEntity<Page<Object[]>> getTopActorsByMovieCount(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> topActors = actorService.getTopActorsByMovieCount(pageable);
        return ResponseEntity.ok(topActors);
    }

    // READ - Actors with no movies
    @GetMapping("/stats/no-movies")
    public ResponseEntity<List<Actor>> getActorsWithNoMovies() {
        List<Actor> actors = actorService.getActorsWithNoMovies();
        return ResponseEntity.ok(actors);
    }

    // UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<Actor> updateActor(
            @PathVariable Long id,
            @Valid @RequestBody Actor actorDetails) {
        Actor updatedActor = actorService.updateActor(id, actorDetails);
        return ResponseEntity.ok(updatedActor);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force) {
        actorService.deleteActor(id, force);
        return ResponseEntity.noContent().build();
    }

    // BULK GET actors by IDs
    @PostMapping("/bulk")
    public ResponseEntity<List<Actor>> getActorsByIds(@RequestBody List<Long> actorIds) {
        List<Actor> actors = actorService.getActorsByIds(actorIds);
        return ResponseEntity.ok(actors);
    }

    // CHECK EXISTENCE
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> actorExists(@PathVariable Long id) {
        boolean exists = actorService.actorExists(id);
        return ResponseEntity.ok(exists);
    }
}