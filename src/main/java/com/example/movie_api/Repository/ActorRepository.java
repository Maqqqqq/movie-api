package com.example.movie_api.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.movie_api.Entity.Actor;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {

    List<Actor> findByNameContainingIgnoreCase(String name);
    Optional<Actor> findByNameAndBirthDate(String name, String birthDate);

}
