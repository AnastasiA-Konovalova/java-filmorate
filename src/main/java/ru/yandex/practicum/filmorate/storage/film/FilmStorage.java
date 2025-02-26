package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getFilms();

    Collection<Film> getPopularFilms(Long count);

    Optional<Integer> addLike(Long filmId, Long userId);

    Optional<Integer> deleteLike(Long filmId, Long userId);

    Optional<Film> getFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    void deleteFilms();
}