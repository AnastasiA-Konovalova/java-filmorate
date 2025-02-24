package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FriendDto {
    @NotBlank(message = "Id друга не может быть пустым")
    private Long id;
    private String status;
}