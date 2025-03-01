package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmGenre {

    private Long id;
    private Long filmId;
    private Long userId;
}