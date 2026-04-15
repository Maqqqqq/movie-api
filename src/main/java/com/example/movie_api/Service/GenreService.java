package com.example.movie_api.Service;

import com.example.movie_api.dto.GenreDTO;
import com.example.movie_api.Entity.Genre;
import com.example.movie_api.Entity.Movie;
import com.example.movie_api.exception.ResourceAlreadyExistsException;
import com.example.movie_api.exception.ResourceNotFoundException;
import com.example.movie_api.Repository.GenreRepository;
import com.example.movie_api.Repository.MovieRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    public GenreService(GenreRepository genreRepository, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
    }
   
    public Page<GenreDTO> getAllGenres(Pageable pageable) {
        validatePagination(pageable);
        return genreRepository.findAll(pageable)
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

    public GenreDTO getGenreById(Long id) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Genre not found with id: " + id));
        return mapToDTO(genre);
    }

    public GenreDTO createGenre(GenreDTO genreDTO) {
        if (genreDTO.getName() == null || genreDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be empty or blank.");
        }
        Genre genre = mapToEntity(genreDTO);
        genre = genreRepository.save(genre);
        System.out.println(genre + " has been added to genres");
        return mapToDTO(genre);
    }

    public GenreDTO updateGenre(Long id, GenreDTO genreDTO) {
        if (genreDTO.getName() == null || genreDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be empty or blank.");
        }
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Genre not found with id: " + id));
        genre.setName(genreDTO.getName());
        genre = genreRepository.save(genre);
        return mapToDTO(genre);
    }

    public void deleteGenre(Long id, boolean force) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Genre not found with id: " + id));

        if (!force && !genre.getMovies().isEmpty()) {
            throw new ResourceAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    "Cannot delete genre '" + genre.getName() + "' because it is associated with " + genre.getMovies().size() + " movie(s).");
        }

        if (force) {
            for (Movie movie : genre.getMovies()) {
                movie.getGenres().remove(genre);
                movieRepository.save(movie);
            }
        }
        System.out.println(genre + " has been deleted from genres");
        genreRepository.delete(genre);
    }

    private GenreDTO mapToDTO(Genre genre) {
        GenreDTO dto = new GenreDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    private Genre mapToEntity(GenreDTO dto) {
        Genre genre = new Genre();
        genre.setName(dto.getName());
        return genre;
    }

}
