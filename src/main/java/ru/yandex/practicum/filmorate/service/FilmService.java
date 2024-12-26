package ru.yandex.practicum.filmorate.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mapping.FilmMapperToDto;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmMapperToDto filmMapperToDto;

    public Integer addLike(Long filmId, Long userId) {
        //getFilmById(userId);
        Film film = getFilmById(filmId);
        log.info("Добавление лайка фильму");
        film.addLike(userId);
        return film.getLikes().size();
    }

    public Integer deleteLike(Long filmId, Long userId) {
        //getFilmById(userId);
        Film film = getFilmById(filmId);
        log.info("Удаление лайка фильму");
        film.deleteLike(userId);
        return film.getLikes().size();
    }

    public List<FilmDto> getTheMostPopularFilms(int count) {
        List<Film> films = new ArrayList<>(filmStorage.getFilms());
        log.info("Запрошен список полярных фильмов (топ: {})", count);
        return films.stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .map(film -> filmMapperToDto.toDto(film))
                .limit(count)
                .toList();
    }

    private Film getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.warn("Неправильно введен id фильма");
            throw new NotFoundException("Фильм с таким id отсутствует");
        }
        return film;
    }
}