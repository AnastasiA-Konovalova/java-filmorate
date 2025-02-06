package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Mpa {

    private Long id;
    @NotBlank(message = "Класс рейтинга не может быть пустым")
    private String name;
}
