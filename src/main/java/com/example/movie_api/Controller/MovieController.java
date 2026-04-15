package com.example.movie_api.Controller;

import com.example.movie_api.Entity.Movie;
import com.example.movie_api.Service.MovieService;
import com.example.movie_api.dto.ActorDTO;
import com.example.movie_api.dto.MovieDTO;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Movie> createMovie(@RequestBody @Valid MovieDTO movieDTO) {
        Movie createdMovie = movieService.createMovieWithActors(movieDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping
    public Page<MovieDTO> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieService.getAllMovies(pageable);
    }

    @GetMapping(params = "genre")
public ResponseEntity<List<MovieDTO>> getMoviesByGenre(@RequestParam Long genre) {
    List<MovieDTO> movies = movieService.getMoviesByGenre(genre);
    return ResponseEntity.ok(movies);
}

@GetMapping("/{movieId}/actors")
public ResponseEntity<List<ActorDTO>> getActorsByMovieId(@PathVariable Long movieId) {
    List<ActorDTO> actors = movieService.getActorsByMovieId(movieId);
    return ResponseEntity.ok(actors);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean force) {
        movieService.deleteMovieById(id, force);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody MovieDTO movieDTO) {
        Movie updatedMovie = movieService.updateMovie(id, movieDTO);
        return ResponseEntity.ok(updatedMovie);
    }

    @GetMapping(params = "year")
    public ResponseEntity<List<MovieDTO>> getMoviesByReleaseYear(@RequestParam int year) {
        List<MovieDTO> movies = movieService.getMoviesByReleaseYear(year);
        return ResponseEntity.ok(movies);
    }

    @GetMapping(params = "actor")
    public ResponseEntity<List<MovieDTO>> getMoviesByActor(@RequestParam Long actor) {
        List<MovieDTO> movies = movieService.getMoviesByActorId(actor);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/search")
    public List<MovieDTO> searchMovies(@RequestParam String title) {
        return movieService.searchMoviesByTitle(title);
    }
}
