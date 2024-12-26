package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friends {

    private Long id;
    private Long userId;
    private Long friendId;
    private String status;
}
