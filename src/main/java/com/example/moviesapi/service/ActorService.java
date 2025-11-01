package com.example.moviesapi.service;

import com.example.moviesapi.exception.ResourceNotFoundException;
import com.example.moviesapi.exception.InvalidRequestException;
import com.example.moviesapi.model.Actor;
import com.example.moviesapi.model.Movie;
import com.example.moviesapi.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActorService {

    private final ActorRepository actorRepository;

    @Autowired
    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    // CREATE
    public Actor createActor(Actor actor) {
        // Validate birth date is not in the future
        if (actor.getBirthDate().isAfter(LocalDate.now())) {
            throw new InvalidRequestException("Birth date cannot be in the future");
        }

        // Check if actor with same name and birth date already exists
        if (actorRepository.existsByNameAndBirthDate(actor.getName(), actor.getBirthDate())) {
            throw new InvalidRequestException("Actor with name '" + actor.getName() + 
                "' and birth date '" + actor.getBirthDate() + "' already exists");
        }

        return actorRepository.save(actor);
    }

    // READ
    @Transactional(readOnly = true)
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Actor> getAllActors(Pageable pageable) {
        return actorRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Actor getActorById(Long id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Movie> getMoviesByActorId(Long actorId) {
        Actor actor = getActorById(actorId);
        return List.copyOf(actor.getMovies()); // Return immutable copy
    }

    @Transactional(readOnly = true)
    public Page<Actor> searchActorsByName(String name, Pageable pageable) {
        return actorRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Actor> searchActors(String name, LocalDate minBirthDate, LocalDate maxBirthDate, Pageable pageable) {
        return actorRepository.findBySearchCriteria(name, minBirthDate, maxBirthDate, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Object[]> getAllActorsWithMovieCount(Pageable pageable) {
        return actorRepository.findAllWithMovieCount(pageable);
    }

    @Transactional(readOnly = true)
    public List<Actor> getActorsByBirthDateRange(LocalDate startDate, LocalDate endDate) {
        return actorRepository.findByBirthDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Actor> getActorsWithNoMovies() {
        return actorRepository.findActorsWithNoMovies();
    }

    // UPDATE
    public Actor updateActor(Long id, Actor actorDetails) {
        Actor actor = getActorById(id);
        
        // Validate birth date if provided
        if (actorDetails.getBirthDate() != null && 
            actorDetails.getBirthDate().isAfter(LocalDate.now())) {
            throw new InvalidRequestException("Birth date cannot be in the future");
        }

        // Check for duplicate if name or birth date is being changed
        if ((actorDetails.getName() != null && !actor.getName().equals(actorDetails.getName())) ||
            (actorDetails.getBirthDate() != null && !actor.getBirthDate().equals(actorDetails.getBirthDate()))) {
            
            String newName = actorDetails.getName() != null ? actorDetails.getName() : actor.getName();
            LocalDate newBirthDate = actorDetails.getBirthDate() != null ? 
                actorDetails.getBirthDate() : actor.getBirthDate();
            
            if (actorRepository.existsByNameAndBirthDate(newName, newBirthDate)) {
                throw new InvalidRequestException("Actor with name '" + newName + 
                    "' and birth date '" + newBirthDate + "' already exists");
            }
        }

        // Update fields if provided
        if (actorDetails.getName() != null) {
            actor.setName(actorDetails.getName());
        }
        if (actorDetails.getBirthDate() != null) {
            actor.setBirthDate(actorDetails.getBirthDate());
        }

        return actorRepository.save(actor);
    }

    // DELETE
    public void deleteActor(Long id, boolean force) {
        Actor actor = getActorById(id);
        
        if (!force && !actor.getMovies().isEmpty()) {
            int movieCount = actor.getMovies().size();
            throw new InvalidRequestException(
                "Cannot delete actor '" + actor.getName() + 
                "' because they are associated with " + movieCount + " movie" + 
                (movieCount > 1 ? "s" : "")
            );
        }

        // If force=true, remove relationships before deletion
        if (force) {
            // Create a copy to avoid ConcurrentModificationException
            List<Movie> movies = List.copyOf(actor.getMovies());
            for (Movie movie : movies) {
                movie.removeActor(actor);
            }
        }

        actorRepository.delete(actor);
    }

    public void deleteActor(Long id) {
        deleteActor(id, false);
    }

    // BULK OPERATIONS
    public List<Actor> getActorsByIds(List<Long> actorIds) {
        List<Actor> actors = actorRepository.findByIdIn(actorIds);
        if (actors.size() != actorIds.size()) {
            // Find which IDs are missing
            List<Long> foundIds = actors.stream().map(Actor::getId).toList();
            List<Long> missingIds = actorIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Actors not found with ids: " + missingIds);
        }
        return actors;
    }

    // VALIDATION
    @Transactional(readOnly = true)
    public boolean actorExists(Long id) {
        return actorRepository.existsById(id);
    }

    // STATISTICS
    @Transactional(readOnly = true)
    public Page<Object[]> getTopActorsByMovieCount(Pageable pageable) {
        return actorRepository.findTopActorsByMovieCount(pageable);
    }

    @Transactional(readOnly = true)
    public List<Actor> getActorsWithMinimumMovies(int minMovies) {
        return actorRepository.findActorsWithMinimumMovies(minMovies);
    }

    // UTILITY METHODS
    @Transactional(readOnly = true)
    public List<Actor> findActorsByMovieId(Long movieId) {
        return actorRepository.findByMovieId(movieId);
    }
}