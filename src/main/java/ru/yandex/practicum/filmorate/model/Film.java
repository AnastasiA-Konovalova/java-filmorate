package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.MinimumReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @JsonProperty("description")
    @Size(max = 200, message = "Длина описания не может быть больше 200 символов")
    private String description;

    @MinimumReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть позитивным числом")
    private Integer duration;
    private List<Genre> genres;
    private Mpa mpa;

    private Set<Long> likes = new HashSet<>();

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void deleteLike(Long usersId) {
        likes.remove(usersId);
    }
}