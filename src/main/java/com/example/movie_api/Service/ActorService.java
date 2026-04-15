package com.example.movie_api.Service;

import com.example.movie_api.dto.ActorDTO;
import com.example.movie_api.Entity.Actor;
import com.example.movie_api.exception.ResourceAlreadyExistsException;
import com.example.movie_api.exception.ResourceNotFoundException;
import com.example.movie_api.Repository.ActorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActorService {
    private final ActorRepository actorRepository;
    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }
    @Transactional(readOnly = true)
    public Page<ActorDTO> getAllActors(Pageable pageable) {
        validatePagination(pageable);
        return actorRepository.findAll(pageable)
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

    public ActorDTO getActorById(Long id) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Actor not found with id: " + id));

        return mapToDTO(actor);
    }

    public List<ActorDTO> getActorsByName(String name) {
        System.out.println("Searching for actors with name containing: " + name);
        List<ActorDTO> actors = actorRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        System.out.println("Found actors: " + actors);
        return actors;
    }

    public ActorDTO createActor(ActorDTO actorDTO) {

        if (actorDTO.getName() == null || actorDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("The actor's name cannot be null or blank.");
        }

        if (actorDTO.getBirthDate() == null) {
            throw new IllegalArgumentException("The actor's birth date cannot be null.");
        }

        if (actorRepository.findByNameAndBirthDate(actorDTO.getName(), actorDTO.getBirthDate().toString()).isPresent()) {
            throw new ResourceAlreadyExistsException(HttpStatus.BAD_REQUEST, "An actor with the same name and birthdate already exists");
        }

        Actor actor = mapToEntity(actorDTO);
        actor = actorRepository.save(actor);

        System.out.println("'" + actor + "' has been added to actors");
        return mapToDTO(actor);
    }

    public ActorDTO updateActor(Long id, ActorDTO actorDTO) {

        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Actor not found with id: " + id));
        
        if (actorDTO.getName() != null && !actorDTO.getName().trim().isEmpty()) {
            actor.setName(actorDTO.getName());
        }
        if (actorDTO.getBirthDate() != null) {
            actor.setBirthDate(actorDTO.getBirthDate().format(DateTimeFormatter.ISO_DATE));
        }

        actor = actorRepository.save(actor);

        return mapToDTO(actor);
    }

    public void deleteActor(Long id, boolean force) {

        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Actor not found with id: " + id));
        
        if (!force) {
            if (!actor.getMovies().isEmpty()) {
                throw new ResourceAlreadyExistsException(HttpStatus.BAD_REQUEST, "Actor is associated with movies and cannot be deleted.");
            }
        } else {
            actor.getMovies().forEach(movie -> movie.getActors().remove(actor));
        }

        System.out.println("'" + actor + "' has been deleted from actors");
        actorRepository.delete(actor);
    }

    private ActorDTO mapToDTO(Actor actor) {
        ActorDTO dto = new ActorDTO();
        
        dto.setId(actor.getId());
        dto.setName(actor.getName());
        dto.setBirthDate(LocalDate.parse(actor.getBirthDate(), DateTimeFormatter.ISO_DATE));
        
        return dto;
    }
    private Actor mapToEntity(ActorDTO actorDTO) {

        Actor actor = new Actor();

        actor.setId(actorDTO.getId());
        actor.setName(actorDTO.getName());
        actor.setBirthDate(actorDTO.getBirthDate().format(DateTimeFormatter.ISO_DATE));

        return actor;
    }
}
