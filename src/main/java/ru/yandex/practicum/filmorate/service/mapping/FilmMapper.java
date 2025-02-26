package ru.yandex.practicum.filmorate.service.mapping;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

public class FilmMapper {

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
                    .map(GenreMapper::toDto).toList()
            );
        }

        if (film.getLikes() != null) {
            filmDto.setLikes(film.getLikes());
        }
        return filmDto;
    }

    public static Film toEntity(Film film, FilmDto filmDto) {
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());

        MpaDto mpaDto = filmDto.getMpa();
        Mpa mpa = new Mpa();
        if (mpaDto != null) {
            mpa.setId(mpaDto.getId());
            mpa.setName(mpaDto.getName());
        }

        film.setMpa(mpa);

        if (filmDto.getGenres() != null) {
            film.setGenres(filmDto.getGenres().stream()
                    .map(GenreMapper::toEntity)
                    .toList());

        }

        return film;
    }
}