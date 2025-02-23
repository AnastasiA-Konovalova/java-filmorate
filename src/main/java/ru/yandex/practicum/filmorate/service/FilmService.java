package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mapping.FilmMapperToDto;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    public Integer addLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new NotFoundException("Неверно указан id фильма " + filmId + " или пользователя" + userId);
        }
        Optional.ofNullable(getFilm(filmId))
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));

        log.info("Добавление лайка фильму");

        return filmStorage.addLike(filmId, userId);
    }

    public Integer deleteLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new NotFoundException("Неверно указан id фильма " + filmId + " или пользователя" + userId);
        }
        log.info("Удаление лайка фильму");
        return filmStorage.deleteLike(filmId, userId);
    }

    public List<FilmDto> getPopularFilms(long count) {
        return filmStorage.getPopularFilms(count).stream().map(FilmMapperToDto::toDto).toList();
    }

    public FilmDto createFilm(FilmDto filmDto) {
        if (filmDto == null) {
            log.warn("Фильм пуст");
            throw new NotFoundException("Фильм пуст");
        }

        Film film = new Film();
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        film.setLikes(new HashSet<>());

        List<GenreDto> genreDtos = filmDto.getGenres();

        Map<Long, Genre> idToGenre = new HashMap<>();
        genreDbStorage.getGenres().forEach(g -> idToGenre.put(g.getId(), g));

        film.setGenres(
                Optional.ofNullable(genreDtos)
                        .orElse(Collections.emptyList())
                        .stream()
                        .peek(g -> {
                            if (!idToGenre.containsKey(g.getId())) {
                                throw new NotFoundException("Жанр не найден: id = " + g.getId());
                            }
                        })
                        .map(g -> idToGenre.get(g.getId()))
                        .filter(Objects::nonNull)
                        .toList()
        );
        MpaDto mpa = filmDto.getMpa();
        if (mpa != null) {
            film.setMpa(mpaDbStorage.getMpaById(mpa.getId()));
        }

        return FilmMapperToDto.toDto(filmStorage.createFilm(film));
    }

    public Collection<FilmDto> getFilms() {
        Collection<Film> films = filmStorage.getFilms();

        if (films.isEmpty()) {
            log.warn("Список фильмов пуст");
            throw new NotFoundException("Список фильмов пуст");
        }

        return films.stream()
                .map(FilmMapperToDto::toDto)
                .toList();
    }

    public FilmDto getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.warn("Неправильно введен id фильма");
            throw new NotFoundException("Фильм с таким id отсутствует");
        }
        return FilmMapperToDto.toDto(film);
    }

    public FilmDto updateFilm(FilmDto filmDto) {
        Film film = filmStorage.getFilmById(filmDto.getId());
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmDto.getId() + " не найден");
        }

        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setDuration(filmDto.getDuration());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setMpa(mpaDbStorage.getMpaById(filmDto.getMpa().getId()));

        return FilmMapperToDto.toDto(filmStorage.updateFilm(film));
    }

    public void deleteFilms() {
        filmStorage.deleteFilms();
    }

    private Film getFilm(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.warn("Неправильно введен id фильма");
            throw new NotFoundException("Фильм с таким id отсутствует");
        }
        return film;
    }
}