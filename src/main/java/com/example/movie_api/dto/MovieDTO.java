package com.example.movie_api.dto;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    public class MovieDTO {
    private Long id;
    @NotNull(message = "Title can not be null")
    @NotBlank(message = "Title can not be blank")
    private String title;
    @NotNull(message = "Release year can not be null")
    @Min(value = 1880, message = "Release year cannot be earlier than 1880")
    @Max(value = 2030, message = "Release year cannot be later than 2030")
    private Integer releaseYear;
    @Min(60)
    @NotNull(message = "Duration cannot be null")
    private Integer duration;
    private List<Long> actorIds = new ArrayList<>();
    private List<Long> genreIds = new ArrayList<>();
    private Optional<List<String>> actors = Optional.empty();
    private Optional<List<String>> genres = Optional.empty();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<Long> getActorIds() {
        return actorIds;
    }

    public void setActorIds(List<Long> actorIds) {
        this.actorIds = actorIds;
    }

    public List<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public Optional<List<String>> getActors() {
        return actors;
    }

    public void setActors(Optional<List<String>> actors) {
        this.actors = actors;
    }

    public Optional<List<String>> getGenres() {
        return genres;
    }

    public void setGenres(Optional<List<String>> genres) {
        this.genres = genres;
    }

    
}
