package ru.yandex.practicum.filmorate.service.mapping;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

public class FilmMapperToDto {

    public static FilmDto toDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());

        Mpa mpa = film.getMpa();
        MpaDto mpaDto = new MpaDto();
        if (mpa != null) {
            mpaDto.setId(mpa.getId());
            mpaDto.setName(mpa.getName());
        }

        filmDto.setMpa(mpaDto);

        if (film.getGenres() != null) {
            filmDto.setGenres(film.getGenres().stream()
                    .map(GenreMapperToDto::toDto).toList()
            );
        }

        if (film.getLikes() != null) {
            filmDto.setLikes(film.getLikes());
        }
        return filmDto;
    }
}