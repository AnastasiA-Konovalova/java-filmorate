package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.MinimumReleaseDate;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class FilmDto {

    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Длина описания не может быть больше 200 символов")
    private String description;
    @MinimumReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть позитивным числом")
    private Integer duration;
    private Set<Long> likes = new HashSet<>();
    private List<Long> genres;
    private MpaDto ratingId;
}