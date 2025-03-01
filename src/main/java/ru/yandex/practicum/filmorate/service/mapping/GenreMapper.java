package ru.yandex.practicum.filmorate.service.mapping;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

public class GenreMapper {

    public static GenreDto toDto(Genre genre) {
        GenreDto genreDto = new GenreDto();
        genreDto.setId(genre.getId());
        genreDto.setName(genre.getName());

        return genreDto;
    }

    public static Genre toEntity(GenreDto genreDto) {
        Genre genre = new Genre();
        genre.setId(genreDto.getId());
        genre.setName(genreDto.getName());

        return genre;
    }
}