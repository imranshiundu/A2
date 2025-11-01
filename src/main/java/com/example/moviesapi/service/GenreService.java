package com.example.moviesapi.service;

import com.example.moviesapi.exception.ResourceNotFoundException;
import com.example.moviesapi.exception.InvalidRequestException;
import com.example.moviesapi.model.Genre;
import com.example.moviesapi.model.Movie;
import com.example.moviesapi.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    // CREATE
    public Genre createGenre(Genre genre) {
        // Check if genre with same name already exists (case-insensitive)
        if (genreRepository.existsByNameIgnoreCase(genre.getName())) {
            throw new InvalidRequestException("Genre with name '" + genre.getName() + "' already exists");
        }
        return genreRepository.save(genre);
    }

    // READ
    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Genre> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Genre> getGenreByName(String name) {
        return genreRepository.findByNameIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Movie> getMoviesByGenreId(Long genreId) {
        Genre genre = getGenreById(genreId);
        return List.copyOf(genre.getMovies()); // Return immutable copy
    }

    @Transactional(readOnly = true)
    public Page<Genre> searchGenresByName(String name, Pageable pageable) {
        return genreRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Object[]> getAllGenresWithMovieCount(Pageable pageable) {
        return genreRepository.findAllWithMovieCount(pageable);
    }

    @Transactional(readOnly = true)
    public List<Genre> getGenresWithNoMovies() {
        return genreRepository.findGenresWithNoMovies();
    }

    // UPDATE
    public Genre updateGenre(Long id, Genre genreDetails) {
        Genre genre = getGenreById(id);
        
        // Check if new name already exists (excluding current genre)
        if (genreDetails.getName() != null && 
            !genre.getName().equalsIgnoreCase(genreDetails.getName()) &&
            genreRepository.existsByNameIgnoreCase(genreDetails.getName())) {
            throw new InvalidRequestException("Genre with name '" + genreDetails.getName() + "' already exists");
        }

        // Update fields if provided
        if (genreDetails.getName() != null) {
            genre.setName(genreDetails.getName());
        }

        return genreRepository.save(genre);
    }

    // DELETE
    public void deleteGenre(Long id, boolean force) {
        Genre genre = getGenreById(id);
        
        if (!force && !genre.getMovies().isEmpty()) {
            int movieCount = genre.getMovies().size();
            throw new InvalidRequestException(
                "Cannot delete genre '" + genre.getName() + 
                "' because it has " + movieCount + " associated movie" + 
                (movieCount > 1 ? "s" : "")
            );
        }

        // If force=true, remove relationships before deletion
        if (force) {
            // Create a copy to avoid ConcurrentModificationException
            List<Movie> movies = List.copyOf(genre.getMovies());
            for (Movie movie : movies) {
                movie.removeGenre(genre);
            }
        }

        genreRepository.delete(genre);
    }

    public void deleteGenre(Long id) {
        deleteGenre(id, false);
    }

    // BULK OPERATIONS
    public List<Genre> createGenres(List<Genre> genres) {
        // Check for duplicates in the request
        long distinctNames = genres.stream()
                .map(genre -> genre.getName().toLowerCase())
                .distinct()
                .count();
        
        if (distinctNames != genres.size()) {
            throw new InvalidRequestException("Duplicate genre names in the request");
        }

        // Check for existing genres
        List<String> genreNames = genres.stream()
                .map(Genre::getName)
                .toList();
        
        List<Genre> existingGenres = genreRepository.findByNames(genreNames);
        if (!existingGenres.isEmpty()) {
            throw new InvalidRequestException("Some genres already exist: " + 
                existingGenres.stream().map(Genre::getName).toList());
        }

        return genreRepository.saveAll(genres);
    }

    // VALIDATION
    @Transactional(readOnly = true)
    public boolean genreExists(Long id) {
        return genreRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean genreExistsByName(String name) {
        return genreRepository.existsByNameIgnoreCase(name);
    }

    // STATISTICS
    @Transactional(readOnly = true)
    public Page<Object[]> getTopGenresByMovieCount(Pageable pageable) {
        return genreRepository.findTopGenresByMovieCount(pageable);
    }

    @Transactional(readOnly = true)
    public List<Genre> getGenresWithMinimumMovies(int minMovies) {
        return genreRepository.findGenresWithMinimumMovies(minMovies);
    }
}