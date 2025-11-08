package com.example.moviesapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.moviesapi.cache.SimpleCacheService;
import com.example.moviesapi.exception.InvalidRequestException;
import com.example.moviesapi.exception.ResourceNotFoundException;
import com.example.moviesapi.model.Actor;
import com.example.moviesapi.model.Genre;
import com.example.moviesapi.model.Movie;
import com.example.moviesapi.repository.ActorRepository;
import com.example.moviesapi.repository.GenreRepository;
import com.example.moviesapi.repository.MovieRepository;

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
        if (movieRepository.existsByTitleAndReleaseYear(movie.getTitle(), movie.getReleaseYear())) {
            throw new InvalidRequestException("Movie with title '" + movie.getTitle() + 
                "' and release year '" + movie.getReleaseYear() + "' already exists");
        }

        if (movie.getReleaseYear() < 1888 || movie.getReleaseYear() > java.time.Year.now().getValue() + 1) {
            throw new InvalidRequestException("Release year must be between 1888 and " + 
                (java.time.Year.now().getValue() + 1));
        }

        // For SQLite, use manual ID generation
        Long nextId = findNextAvailableId();
        movie.setId(nextId);

        cacheService.remove("all_movies");
        cacheService.remove("all_movies_cached");
        
        return movieRepository.save(movie);
    }

    // Find next available ID for SQLite
    private Long findNextAvailableId() {
        Movie lastMovie = movieRepository.findTopByOrderByIdDesc();
        if (lastMovie != null && lastMovie.getId() != null) {
            return lastMovie.getId() + 1;
        }
        return 1L;
    }

    public Movie createMovieWithRelations(Movie movie, List<Long> genreIds, List<Long> actorIds) {
        Movie savedMovie = createMovie(movie);
        
        if (genreIds != null && !genreIds.isEmpty()) {
            addGenresToMovie(savedMovie.getId(), genreIds);
        }
        
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

    // CACHED METHODS
    @Transactional(readOnly = true)
    public List<Movie> getAllMoviesCached() {
        String cacheKey = "all_movies_cached";
        
        List<Movie> cachedMovies = (List<Movie>) cacheService.get(cacheKey);
        if (cachedMovies != null) {
            return cachedMovies;
        }
        
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

    // FILTERING AND SEARCH - FIXED METHOD NAMES
    @Transactional(readOnly = true)
    public Page<Movie> getMoviesByGenreId(Long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException("Genre not found with id: " + genreId);
        }
        return movieRepository.findByGenresId(genreId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> getMoviesByActorId(Long actorId, Pageable pageable) {
        if (!actorRepository.existsById(actorId)) {
            throw new ResourceNotFoundException("Actor not found with id: " + actorId);
        }
        return movieRepository.findByActorsId(actorId, pageable);
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
        
        if ((movieDetails.getTitle() != null && !movie.getTitle().equals(movieDetails.getTitle())) ||
            (movieDetails.getReleaseYear() != null && !movie.getReleaseYear().equals(movieDetails.getReleaseYear()))) {
            
            String newTitle = movieDetails.getTitle() != null ? movieDetails.getTitle() : movie.getTitle();
            Integer newYear = movieDetails.getReleaseYear() != null ? 
                movieDetails.getReleaseYear() : movie.getReleaseYear();
            
            if (movieRepository.existsByTitleAndReleaseYear(newTitle, newYear)) {
                throw new InvalidRequestException("Movie with title '" + newTitle + 
                    "' and release year '" + newYear + "' already exists");
            }
        }

        if (movieDetails.getTitle() != null) {
            movie.setTitle(movieDetails.getTitle());
        }
        if (movieDetails.getReleaseYear() != null) {
            movie.setReleaseYear(movieDetails.getReleaseYear());
        }
        if (movieDetails.getDuration() != null) {
            movie.setDuration(movieDetails.getDuration());
        }

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
        
        for (Genre genre : genres) {
            movie.addGenre(genre);
        }
        
        cacheService.remove("movie_" + movieId);
        
        return movieRepository.save(movie);
    }

    public Movie removeGenresFromMovie(Long movieId, List<Long> genreIds) {
        Movie movie = getMovieById(movieId);
        List<Genre> genres = genreRepository.findByIdIn(genreIds);
        
        for (Genre genre : genres) {
            movie.removeGenre(genre);
        }
        
        cacheService.remove("movie_" + movieId);
        
        return movieRepository.save(movie);
    }

    public Movie addActorsToMovie(Long movieId, List<Long> actorIds) {
        Movie movie = getMovieById(movieId);
        List<Actor> actors = actorRepository.findByIdIn(actorIds);
        
        if (actors.size() != actorIds.size()) {
            throw new ResourceNotFoundException("Some actors not found");
        }
        
        for (Actor actor : actors) {
            movie.addActor(actor);
        }
        
        cacheService.remove("movie_" + movieId);
        
        return movieRepository.save(movie);
    }

    public Movie removeActorsFromMovie(Long movieId, List<Long> actorIds) {
        Movie movie = getMovieById(movieId);
        List<Actor> actors = actorRepository.findByIdIn(actorIds);
        
        for (Actor actor : actors) {
            movie.removeActor(actor);
        }
        
        cacheService.remove("movie_" + movieId);
        
        return movieRepository.save(movie);
    }

    public Movie updateMovieRelations(Long movieId, List<Long> genreIds, List<Long> actorIds) {
        Movie movie = getMovieById(movieId);
        
        if (genreIds != null) {
            movie.getGenres().clear();
            List<Genre> genres = genreRepository.findByIdIn(genreIds);
            if (genres.size() != genreIds.size()) {
                throw new ResourceNotFoundException("Some genres not found");
            }
            for (Genre genre : genres) {
                movie.addGenre(genre);
            }
        }
        
        if (actorIds != null) {
            movie.getActors().clear();
            List<Actor> actors = actorRepository.findByIdIn(actorIds);
            if (actors.size() != actorIds.size()) {
                throw new ResourceNotFoundException("Some actors not found");
            }
            for (Actor actor : actors) {
                movie.addActor(actor);
            }
        }
        
        cacheService.remove("movie_" + movieId);
        
        return movieRepository.save(movie);
    }

    // DELETE
    public void deleteMovie(Long id, boolean force) {
        Movie movie = getMovieById(id);
        
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

        if (force) {
            List<Genre> genres = List.copyOf(movie.getGenres());
            for (Genre genre : genres) {
                movie.removeGenre(genre);
            }
            
            List<Actor> actors = List.copyOf(movie.getActors());
            for (Actor actor : actors) {
                movie.removeActor(actor);
            }
        }

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