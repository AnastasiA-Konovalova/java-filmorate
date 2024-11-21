package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        films.values().stream()
                .filter(filmCheck -> filmCheck.getName().equalsIgnoreCase(film.getName()))
                .findFirst()
                .ifPresent(duplicate -> {
                    log.warn("Ошибка. Создаваемый фильм уже есть в списке");
                    throw new ValidationException("Фильм с таким названием уже есть в списке");
                });
        log.info("Добавление фильма в список");
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен");
        return film;
    }

    private long generateId() {
        return films.keySet().stream().max(Long::compareTo).map(i -> i + 1).orElse(1L);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Обновление характеристик фильма");
        if (newFilm.getId() == null) {
            log.warn("Ошибка. Не указан id");
            throw new ValidationException("Id должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Ошибка. Фильм с таким ID не найден");
            throw new ResponseStatusException(NOT_FOUND, "Фильм с таким id не найден");
        }
        films.values().stream()
                .filter(filmCheck -> filmCheck.getName().equalsIgnoreCase(newFilm.getName()))
                .findFirst()
                .ifPresent(duplicate -> {
                    log.warn("Ошибка. Попытка внести в список уже существующий фильм");
                    throw new ValidationException("Фильм с таким названием уже есть в списке");
                });
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм обновлен");
        return newFilm;
    }

    public void deleteFilms() {
        films.clear();
    }
}