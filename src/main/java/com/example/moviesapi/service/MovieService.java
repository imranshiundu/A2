package com.example.moviesapi.service;

import com.example.moviesapi.exception.ResourceNotFoundException;
import com.example.moviesapi.exception.InvalidRequestException;
import com.example.moviesapi.model.Movie;
import com.example.moviesapi.model.Genre;
import com.example.moviesapi.model.Actor;
import com.example.moviesapi.repository.MovieRepository;
import com.example.moviesapi.repository.GenreRepository;
import com.example.moviesapi.repository.ActorRepository;
import com.example.moviesapi.cache.SimpleCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final SimpleCacheService cacheService;

    @Autowired
    public MovieService(MovieRepository movieRepository, 
                       GenreRepository genreRepository, 
                       ActorRepository actorRepository,
                       SimpleCacheService cacheService) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.cacheService = cacheService;
    }

    // CREATE
    public Movie createMovie(Movie movie) {
        // Validate movie doesn't already exist
        if (movieRepository.existsByTitleAndReleaseYear(movie.getTitle(), movie.getReleaseYear())) {
            throw new InvalidRequestException("Movie with title '" + movie.getTitle() + 
                "' and release year '" + movie.getReleaseYear() + "' already exists");
        }

        // Validate release year is reasonable
        if (movie.getReleaseYear() < 1888 || movie.getReleaseYear() > java.time.Year.now().getValue() + 1) {
            throw new InvalidRequestException("Release year must be between 1888 and " + 
                (java.time.Year.now().getValue() + 1));
        }

        // Clear cache when new movie is created
        cacheService.remove("all_movies");
        cacheService.remove("all_movies_cached");
        
        return movieRepository.save(movie);
    }

    public Movie createMovieWithRelations(Movie movie, List<Long> genreIds, List<Long> actorIds) {
        Movie savedMovie = createMovie(movie);
        
        // Add genres if provided
        if (genreIds != null && !genreIds.isEmpty()) {
            addGenresToMovie(savedMovie.getId(), genreIds);
        }
        
        // Add actors if provided
        if (actorIds != null && !actorIds.isEmpty()) {
            addActorsToMovie(savedMovie.getId(), actorIds);
        }
        
        return movieRepository.findById(savedMovie.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found after creation"));
    }

    // READ
    @Transactional(readOnly = true)
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Movie> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Actor> getActorsByMovieId(Long movieId) {
        Movie movie = getMovieById(movieId);
        return List.copyOf(movie.getActors());
    }

    @Transactional(readOnly = true)
    public List<Genre> getGenresByMovieId(Long movieId) {
        Movie movie = getMovieById(movieId);
        return List.copyOf(movie.getGenres());
    }

    // CACHED METHODS - NEW
    @Transactional(readOnly = true)
    public List<Movie> getAllMoviesCached() {
        String cacheKey = "all_movies_cached";
        
        // Try to get from cache first
        List<Movie> cachedMovies = (List<Movie>) cacheService.get(cacheKey);
        if (cachedMovies != null) {
            return cachedMovies;
        }
        
        // If not in cache, fetch from database and cache it
        List<Movie> movies = getAllMovies();
        cacheService.put(cacheKey, movies);
        
        return movies;
    }

    @Transactional(readOnly = true)
    public Movie getMovieByIdCached(Long id) {
        String cacheKey = "movie_" + id;
        
        Movie cachedMovie = (Movie) cacheService.get(cacheKey);
        if (cachedMovie != null) {
            return cachedMovie;
        }
        
        Movie movie = getMovieById(id);
        if (movie != null) {
            cacheService.put(cacheKey, movie);
        }
        
        return movie;
    }

    // FILTERING AND SEARCH
    @Transactional(readOnly = true)
    public Page<Movie> getMoviesByGenreId(Long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException("Genre not found with id: " + genreId);
        }
        return movieRepository.findByGenreId(genreId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> getMoviesByActorId(Long actorId, Pageable pageable) {
        if (!actorRepository.existsById(actorId)) {
            throw new ResourceNotFoundException("Actor not found with id: " + actorId);
        }
        return movieRepository.findByActorId(actorId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> getMoviesByReleaseYear(Integer releaseYear, Pageable pageable) {
        return movieRepository.findByReleaseYear(releaseYear, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> searchMoviesByTitle(String title, Pageable pageable) {
        return movieRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> advancedSearch(String title, Integer minYear, Integer maxYear, 
                                    Integer minDuration, Integer maxDuration, Pageable pageable) {
        return movieRepository.findByAdvancedSearch(title, minYear, maxYear, minDuration, maxDuration, pageable);
    }

    // UPDATE
    public Movie updateMovie(Long id, Movie movieDetails) {
        Movie movie = getMovieById(id);
        
        // Check for duplicate if title or year is being changed
        if ((movieDetails.getTitle() != null && !movie.getTitle().equals(movieDetails.getTitle())) ||
            (movieDetails.getReleaseYear() != null && !movie.getReleaseYear().equals(movieDetails.getReleaseYear()))) {
            
            String newTitle = movieDetails.getTitle() != null ? movieDetails.getTitle() : movie.getTitle();
            Integer newYear = movieDetails.getReleaseYear() != null ? 
                movieDetails.getReleaseYear() : movie.getReleaseYear();
            
            // Check if another movie exists with the same title and year
            if (movieRepository.existsByTitleAndReleaseYear(newTitle, newYear)) {
                throw new InvalidRequestException("Movie with title '" + newTitle + 
                    "' and release year '" + newYear + "' already exists");
            }
        }

        // Update fields if provided
        if (movieDetails.getTitle() != null) {
            movie.setTitle(movieDetails.getTitle());
        }
        if (movieDetails.getReleaseYear() != null) {
            movie.setReleaseYear(movieDetails.getReleaseYear());
        }
        if (movieDetails.getDuration() != null) {
            movie.setDuration(movieDetails.getDuration());
        }

        // Clear relevant cache entries
        cacheService.remove("all_movies");
        cacheService.remove("all_movies_cached");
        cacheService.remove("movie_" + id);
        
        return movieRepository.save(movie);
    }

    // RELATIONSHIP MANAGEMENT
    public Movie addGenresToMovie(Long movieId, List<Long> genreIds) {
        Movie movie = getMovieById(movieId);
        List<Genre> genres = genreRepository.findByIdIn(genreIds);
        
        if (genres.size() != genreIds.size()) {
            throw new ResourceNotFoundException("Some genres not found");
        }
        
        genres.forEach(movie::addGenre);
        
        // Clear cache for this movie
        cacheService.remove("movie_" + movieId);
        
        return movieRepository.save(movie);
    }

    public Movie removeGenresFromMovie(Long movieId, List<Long> genreIds) {
        Movie movie = getMovieById(movieId);
        List<Genre> genres = genreRepository.findByIdIn(genreIds);
        
        genres.forEach(movie::removeGenre);
        
        // Clear cache for this movie
        cacheService.remove("movie_" + movieId);
        
        return movieRepository.save(movie);
    }

    public Movie addActorsToMovie(Long movieId, List<Long> actorIds) {
        Movie movie = getMovieById(movieId);
        List<Actor> actors = actorRepository.findByIdIn(actorIds);
        
        if (actors.size() != actorIds.size()) {
            throw new ResourceNotFoundException("Some actors not found");
        }
        
        actors.forEach(movie::addActor);
        
        // Clear cache for this movie
        cacheService.remove("movie_" + movieId);
        
        return movieRepository.save(movie);
    }

    public Movie removeActorsFromMovie(Long movieId, List<Long> actorIds) {
        Movie movie = getMovieById(movieId);
        List<Actor> actors = actorRepository.findByIdIn(actorIds);
        
        actors.forEach(movie::removeActor);
        
        // Clear cache for this movie
        cacheService.remove("movie_" + movieId);
        
        return movieRepository.save(movie);
    }

    public Movie updateMovieRelations(Long movieId, List<Long> genreIds, List<Long> actorIds) {
        Movie movie = getMovieById(movieId);
        
        // Update genres
        if (genreIds != null) {
            movie.getGenres().clear();
            List<Genre> genres = genreRepository.findByIdIn(genreIds);
            if (genres.size() != genreIds.size()) {
                throw new ResourceNotFoundException("Some genres not found");
            }
            genres.forEach(movie::addGenre);
        }
        
        // Update actors
        if (actorIds != null) {
            movie.getActors().clear();
            List<Actor> actors = actorRepository.findByIdIn(actorIds);
            if (actors.size() != actorIds.size()) {
                throw new ResourceNotFoundException("Some actors not found");
            }
            actors.forEach(movie::addActor);
        }
        
        // Clear cache for this movie
        cacheService.remove("movie_" + movieId);
        
        return movieRepository.save(movie);
    }

    // DELETE
    public void deleteMovie(Long id, boolean force) {
        Movie movie = getMovieById(id);
        
        // For movies, we typically allow deletion even with relationships
      
        if (!force && (!movie.getGenres().isEmpty() || !movie.getActors().isEmpty())) {
            int genreCount = movie.getGenres().size();
            int actorCount = movie.getActors().size();
            throw new InvalidRequestException(
                "Cannot delete movie '" + movie.getTitle() + 
                "' because it has " + genreCount + " genre" + (genreCount > 1 ? "s" : "") +
                " and " + actorCount + " actor" + (actorCount > 1 ? "s" : "") + 
                " associated. Use force=true to delete anyway."
            );
        }

        // If force=true, remove relationships before deletion
        if (force) {
            // Remove from genres
            List<Genre> genres = List.copyOf(movie.getGenres());
            for (Genre genre : genres) {
                genre.removeMovie(movie);
            }
            
            // Remove from actors
            List<Actor> actors = List.copyOf(movie.getActors());
            for (Actor actor : actors) {
                actor.removeMovie(movie);
            }
        }

        // Clear relevant cache entries
        cacheService.remove("all_movies");
        cacheService.remove("all_movies_cached");
        cacheService.remove("movie_" + id);
        
        movieRepository.delete(movie);
    }

    public void deleteMovie(Long id) {
        deleteMovie(id, false);
    }

    // STATISTICS AND UTILITY
    @Transactional(readOnly = true)
    public Page<Object[]> getAllMoviesWithActorCount(Pageable pageable) {
        return movieRepository.findAllWithActorCount(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> getLatestMovies(Pageable pageable) {
        return movieRepository.findByOrderByReleaseYearDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<Movie> getMoviesByDurationRange(Integer minDuration, Integer maxDuration) {
        return movieRepository.findByDurationBetween(minDuration, maxDuration);
    }

    @Transactional(readOnly = true)
    public List<Movie> getMoviesWithNoGenres() {
        return movieRepository.findMoviesWithNoGenres();
    }

    @Transactional(readOnly = true)
    public List<Movie> getMoviesWithNoActors() {
        return movieRepository.findMoviesWithNoActors();
    }

    // VALIDATION
    @Transactional(readOnly = true)
    public boolean movieExists(Long id) {
        return movieRepository.existsById(id);
    }
}