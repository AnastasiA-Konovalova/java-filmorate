package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.mapping.FilmMapperToDto;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final FilmMapperToDto filmMapperToDto;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    public Integer addLike(Long filmId, Long userId) {
        //getFilmById(userId);
        Film film = getFilm(filmId);
        log.info("Добавление лайка фильму");
        film.addLike(userId);
        return film.getLikes().size();
    }

    public Integer deleteLike(Long filmId, Long userId) {
        //getFilmById(userId);
        Film film = getFilm(filmId);
        log.info("Удаление лайка фильму");
        film.deleteLike(userId);
        return film.getLikes().size();
    }

    public List<FilmDto> getTheMostPopularFilms(int count) {
        List<Film> films = new ArrayList<>(filmStorage.getFilms());
        log.info("Запрошен список популярных фильмов (топ: {})", count);
        return films.stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .map(film -> filmMapperToDto.toDto(film))
                .limit(count)
                .toList();
    }

    public FilmDto createFilm(FilmDto filmDto) {
        if (filmDto == null) {
            log.warn("Фильм пуст");
            throw new NotFoundException("Фильм пуст");
        }

        List<Long> genreIds = filmDto.getGenres();
        MpaDto ratingId = filmDto.getRatingId();

        Film film = new Film();
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        film.setLikes(new HashSet<>());
        film.setGenres(List.copyOf(genreDbStorage.getGenres()));
        film.setRating(mpaDbStorage.getMpaById(ratingId.getId()));


        return filmMapperToDto.toDto(filmStorage.createFilm(film));
    }

    public Collection<FilmDto> getFilms() {
        List<Film> films = (List<Film>) filmStorage.getFilms();
        if (films.isEmpty()) {
            log.warn("Список фильмов пуст");
            throw new NotFoundException("Список фильмов пуст");
        }
        return films.stream()
                .map(film -> filmMapperToDto.toDto(film))
                .toList();
    }

    public FilmDto getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.warn("Неправильно введен id фильма");
            throw new NotFoundException("Фильм с таким id отсутствует");
        }
        return filmMapperToDto.toDto(film);
    }

    public FilmDto updateFilm(Film newFilm) {
        if (newFilm == null) {
            log.warn("Обновляющий фильм пуст");
            throw new NotFoundException("Обновляющий фильм пуст");
        }
        return filmMapperToDto.toDto(filmStorage.updateFilm(newFilm));
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