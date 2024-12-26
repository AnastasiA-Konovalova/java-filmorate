package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Likes {
    private Long id;
    private Long filmId;
    private Long userId;
}