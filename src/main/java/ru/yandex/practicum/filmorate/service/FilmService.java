package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mapping.FilmMapper;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreStorage genreStorage;

    public Integer addLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new NotFoundException("Неверно указан id фильма " + filmId + " или пользователя" + userId);
        }
        Optional.of(getFilm(filmId))
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        log.info("Добавление лайка фильму");

        return filmStorage.addLike(filmId, userId)
                .orElseThrow(() -> {
                    log.warn("Ошибка в добавлении лайка");
                    return new ValidationException("Ошибка в добавлении лайка  фильму " + filmId + ".");
                });
    }

    public Integer deleteLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new NotFoundException("Неверно указан id фильма " + filmId + " или пользователя" + userId);
        }
        log.info("Удаление лайка фильму");

        return filmStorage.deleteLike(filmId, userId)
                .orElseThrow(() -> {
                    log.warn("Ошибка в удалении лайка");
                    return new ValidationException("Ошибка в удалении лайка  фильму " + filmId + ".");
                });
    }

    public List<FilmDto> getPopularFilms(long count) {
        return filmStorage.getPopularFilms(count).stream().map(FilmMapper::toDto).toList();
    }

    public FilmDto createFilm(FilmDto filmDto) {
        if (filmDto == null) {
            log.warn("Фильм пуст");
            throw new NotFoundException("Фильм пуст");
        }
        Film film = FilmMapper.toEntity(new Film(), filmDto);

        Set<Long> uniqueGenreIds = Optional.ofNullable(filmDto.getGenres())
                .orElse(Collections.emptyList())
                .stream()
                .map(GenreDto::getId)
                .collect(Collectors.toSet());

        List<Genre> genres = genreStorage.getGenreByIds(new ArrayList<>(uniqueGenreIds));
        if (genres.size() != uniqueGenreIds.size()) {
            throw new NotFoundException("Один или несколько жанров не найдены.");
        }

        mpaDbStorage.getMpaById(filmDto.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Mpa с id = " + filmDto.getMpa().getId() + " не найдено"));

        return FilmMapper.toDto(filmStorage.createFilm(film));
    }

    public Collection<FilmDto> getFilms() {
        Collection<Film> films = filmStorage.getFilms();
        if (films.isEmpty()) {
            log.warn("Список фильмов пуст");
            throw new NotFoundException("Список фильмов пуст");
        }

        return films.stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    public FilmDto getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId).orElseThrow(() -> {
            log.warn("Неправильно введен id фильма");
            return new NotFoundException("Ошибка в получении фильма " + filmId + ".");
        });
        return FilmMapper.toDto(film);
    }

    public FilmDto updateFilm(FilmDto filmDto) {
        Film film = filmStorage.getFilmById(filmDto.getId()).orElseThrow(() ->
                new NotFoundException("Фильма с таким id" + filmDto.getId() +
                        " не существует в базе"));

        Film filmUp = FilmMapper.toEntity(film, filmDto);
        return FilmMapper.toDto(filmStorage.updateFilm(filmUp));
    }

    public void deleteFilms() {
        filmStorage.deleteFilms();
    }

    private Film getFilm(Long filmId) {
        return filmStorage.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Фильма с таким id не существует в базе"));
    }
}