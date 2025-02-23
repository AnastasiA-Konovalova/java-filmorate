package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getFilms();

    Collection<Film> getPopularFilms(Long count);

    Integer addLike(Long filmId, Long userId);

    Integer deleteLike(Long filmId, Long userId);

    Film getFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    void deleteFilms();
}