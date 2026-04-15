package com.example.movie_api.Service;

import com.example.movie_api.Entity.Actor;
import com.example.movie_api.Entity.Genre;
import com.example.movie_api.Entity.Movie;
import com.example.movie_api.dto.ActorDTO;
import com.example.movie_api.dto.MovieDTO;
import com.example.movie_api.exception.ResourceAlreadyExistsException;
import com.example.movie_api.exception.ResourceNotFoundException;
import com.example.movie_api.Repository.ActorRepository;
import com.example.movie_api.Repository.GenreRepository;
import com.example.movie_api.Repository.MovieRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final GenreRepository genreRepository;

    public MovieService(MovieRepository movieRepository, ActorRepository actorRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.genreRepository = genreRepository;
    }
    @Transactional
    public Movie createMovieWithActors(MovieDTO movieDTO) {

        Movie movie = mapToEntity(movieDTO);

        List<Actor> actors = actorRepository.findAllById(movieDTO.getActorIds());
        movie.setActors(actors);

        List<Genre> genres = genreRepository.findAllById(movieDTO.getGenreIds());
        movie.setGenres(genres);

        // Check for if already exists
        if (movieRepository.findByTitleContainingIgnoreCase(movie.getTitle()).stream()
                .anyMatch(existingMovie -> existingMovie.getReleaseYear().equals(movie.getReleaseYear()) 
                        && existingMovie.getDuration().equals(movie.getDuration()))) {
            throw new ResourceAlreadyExistsException(HttpStatus.BAD_REQUEST, "Movie with exact same details already exists.");
        }
        System.out.println(movie + "has been added to the database");
        return movieRepository.save(movie);
    }

    @Transactional(readOnly = true)
    public Movie getMovieById(Long id) {

        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Movie not found"));
    }

    @Transactional(readOnly = true)
    public Page<MovieDTO> getAllMovies(Pageable pageable) {
        validatePagination(pageable);
                return movieRepository.findAll(pageable)
                                      .map(this::mapToDTO);
            }
        
            private void validatePagination(Pageable pageable) {
          
                if (pageable.getPageNumber() < 0) {
                    throw new IllegalArgumentException("Invalid page parameters: page number can't be < 0");
                }
                if (pageable.getPageSize() > 100) {
                    throw new IllegalArgumentException("Invalid pagination parameters: page size must be <= 100");
                }
                if (pageable.getPageSize() < 1){
                    throw new IllegalArgumentException("Invalid pagination parameters: Page size must be 1 to 100");
                }
            }
        
    @Transactional(readOnly = true)
    public List<MovieDTO> getMoviesByGenre(Long genreId) {

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Genre not found"));
        return genre.getMovies().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> getMoviesByReleaseYear(int releaseYear) {

        return movieRepository.findByReleaseYear(releaseYear).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActorDTO> getActorsByMovieId(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Movie not found"));

        return movie.getActors().stream()
                .map(this::mapActorToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> getMoviesByActorId(Long actorId) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Actor not found with id: " + actorId));

        return actor.getMovies().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ActorDTO mapActorToDTO(Actor actor) {
        ActorDTO dto = new ActorDTO();
        dto.setId(actor.getId());
        dto.setName(actor.getName());
        dto.setBirthDate(LocalDate.parse(actor.getBirthDate()));
        return dto;
    }
    
    @Transactional
    public void deleteMovieById(Long id, boolean force) {

        Movie movie = getMovieById(id);
        
        List<Actor> actors = movie.getActors();
        List<Genre> genres = movie.getGenres();

        // Handling Deletions with Relationships
        if (!force && (!actors.isEmpty() || !genres.isEmpty())) {
            throw new ResourceAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    "You can't delete '" + movie.getTitle() + "' it has connections to "
                            + actors.size() + " actor(s) and " + genres.size() + " genre(s).");
        }
    
        if (force) {

            for (Actor actor : actors) {
                actor.getMovies().remove(movie);
                actorRepository.save(actor);
            }

            for (Genre genre : genres) {
                genre.getMovies().remove(movie);
                genreRepository.save(genre);
            }
        }
        System.out.println(movie + "has been deleted from database");
        movieRepository.deleteById(id);
    }
    
    public Movie mapToEntity(MovieDTO movieDTO) {
        Movie movie = new Movie();
        movie.setId(movieDTO.getId());
        movie.setTitle(movieDTO.getTitle());
        movie.setReleaseYear(movieDTO.getReleaseYear());
        movie.setDuration(movieDTO.getDuration());
        return movie;
    }
    
    @Transactional
    public Movie updateMovie(Long id, MovieDTO movieDTO) {

        Movie existingMovie = getMovieById(id);

        System.out.println("Updating Movie with ID: " + id);

        if (movieDTO == null) {
            throw new IllegalArgumentException("MovieDTO cannot be null");
        }

        if (movieDTO.getTitle() != null) {
            existingMovie.setTitle(movieDTO.getTitle());
        }
        if (movieDTO.getReleaseYear() != null) {
            existingMovie.setReleaseYear(movieDTO.getReleaseYear());
        }
        if (movieDTO.getDuration() != null) {
            existingMovie.setDuration(movieDTO.getDuration());
        }

        if (movieDTO.getActorIds() != null) {
            if (!movieDTO.getActorIds().isEmpty()) {
                List<Actor> actors = actorRepository.findAllById(movieDTO.getActorIds());
                existingMovie.setActors(actors);
            } else {
                existingMovie.setActors(new ArrayList<>());
            }
        }

        if (movieDTO.getGenreIds() != null) {
            if (!movieDTO.getGenreIds().isEmpty()) {
                List<Genre> genres = genreRepository.findAllById(movieDTO.getGenreIds());
                existingMovie.setGenres(genres);
            } else {
                existingMovie.setGenres(new ArrayList<>());
            }
        }

        System.out.println("Updated Movie: " + existingMovie);

        if (existingMovie.getTitle() == null || existingMovie.getReleaseYear() == null || existingMovie.getDuration() == null) {
            throw new IllegalArgumentException("Movie fields cannot be null before saving.");
        }

        try {
            return movieRepository.save(existingMovie);
        } catch (Exception e) {
            System.err.println("Error saving updated movie: " + e.getMessage());
            throw e; // Rethrow the exception after logging
        }
    }

    
    public MovieDTO mapToDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setDuration(movie.getDuration());
    
        if (!movie.getActors().isEmpty()) {
            List<String> actorNames = movie.getActors().stream()
                    .map(Actor::getName)
                    .collect(Collectors.toList());
            dto.setActors(Optional.of(actorNames));
    
            List<Long> actorIds = movie.getActors().stream()
                    .map(Actor::getId)
                    .collect(Collectors.toList());
            dto.setActorIds(actorIds);
        } else {
            dto.setActors(Optional.empty());
            dto.setActorIds(null);
        }

        if (!movie.getGenres().isEmpty()) {
            List<String> genreNames = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.toList());
            dto.setGenres(Optional.of(genreNames));
    
            List<Long> genreIds = movie.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());
            dto.setGenreIds(genreIds);
        } else {
            dto.setGenres(Optional.empty());
            dto.setGenreIds(null);
        }
    
        return dto;
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> searchMoviesByTitle(String title) {
        // Fetch movies with titles containing the search term, ignoring case
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

 
}
