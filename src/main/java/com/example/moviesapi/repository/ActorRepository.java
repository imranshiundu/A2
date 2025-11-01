package com.example.moviesapi.repository;

import com.example.moviesapi.model.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {

    // Find actor by exact name match
    Optional<Actor> findByName(String name);

    // Case-insensitive search by name containing (for basic search functionality)
    Page<Actor> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Find actors by birth date
    List<Actor> findByBirthDate(LocalDate birthDate);

    // Find actors born after a specific date
    List<Actor> findByBirthDateAfter(LocalDate birthDate);

    // Find actors born before a specific date
    List<Actor> findByBirthDateBefore(LocalDate birthDate);

    // Find actors born between dates
    List<Actor> findByBirthDateBetween(LocalDate startDate, LocalDate endDate);

    // Find actors by name pattern with SQL LIKE (case-insensitive)
    @Query("SELECT a FROM Actor a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Actor> findByNamePattern(@Param("name") String name);

    // Find actors with movie count
    @Query("SELECT a, COUNT(m) as movieCount FROM Actor a LEFT JOIN a.movies m GROUP BY a ORDER BY a.name")
    Page<Object[]> findAllWithMovieCount(Pageable pageable);

    // Find actors in a specific movie
    @Query("SELECT a FROM Actor a JOIN a.movies m WHERE m.id = :movieId")
    List<Actor> findByMovieId(@Param("movieId") Long movieId);

    // Find actors with no movies
    @Query("SELECT a FROM Actor a WHERE a.movies IS EMPTY")
    List<Actor> findActorsWithNoMovies();

    // Find top actors by movie count
    @Query("SELECT a, COUNT(m) as movieCount FROM Actor a LEFT JOIN a.movies m GROUP BY a ORDER BY movieCount DESC")
    Page<Object[]> findTopActorsByMovieCount(Pageable pageable);

    // Find actors by minimum movie count
    @Query("SELECT a FROM Actor a WHERE SIZE(a.movies) >= :minMovies")
    List<Actor> findActorsWithMinimumMovies(@Param("minMovies") int minMovies);

    // Search actors by multiple criteria
    @Query("SELECT a FROM Actor a WHERE " +
           "(:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:minBirthDate IS NULL OR a.birthDate >= :minBirthDate) AND " +
           "(:maxBirthDate IS NULL OR a.birthDate <= :maxBirthDate)")
    Page<Actor> findBySearchCriteria(
        @Param("name") String name,
        @Param("minBirthDate") LocalDate minBirthDate,
        @Param("maxBirthDate") LocalDate maxBirthDate,
        Pageable pageable);

    // Check if actor exists by name and birth date
    boolean existsByNameAndBirthDate(String name, LocalDate birthDate);

    //Bulk find actors by IDs
    List<Actor> findByIdIn(List<Long> ids);
}