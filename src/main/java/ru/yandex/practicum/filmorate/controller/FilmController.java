package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    public Map<Long, Film> films = new HashMap<>();
    private LocalDate releaseDateMin = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        checkFilmConditions(film);
        boolean filmAlreadyExists = films.values().stream()
                .anyMatch(filmCheck -> filmCheck.getName().equalsIgnoreCase(film.getName()));
        if (filmAlreadyExists) {
            log.warn("Ошибка. Создаваемый фильм уже есть в списке");
            throw new ValidationException("Фильм с таким названием уже есть в списке");
        }

        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    private long generateId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;

    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Ошибка. Не указан id");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.get(newFilm.getId()) == null) {
            log.warn("Ошибка. Фильм с таким ID не найден");
            throw new ValidationException("Фильм с таким ID не найден");
        }

        boolean filmAlreadyAdded = films.values().stream()
                .anyMatch(filmCheck -> filmCheck.getName().equalsIgnoreCase(newFilm.getName()));
        if (filmAlreadyAdded) {
            log.warn("Ошибка. Попытка внести в список уже существующий фильм");
            throw new ValidationException("Фильм с таким названием уже есть в списке");
        }
        checkFilmConditions(newFilm);

        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDuration(newFilm.getDuration());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());

        return newFilm;
    }

    private void checkFilmConditions(Film film) {
        Optional<String> filmName = Optional.ofNullable(film.getName());
        filmName.filter(name -> !name.trim().isEmpty())
                .orElseThrow(() -> {
                    log.warn("Ошибка. Название фильма не введено");
                    return new ValidationException("Название не может быть пустым");
                });

        if (film.getDescription().getBytes().length > 200) {
            log.warn("Ошибка. Максимальная длина описания превышена");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(releaseDateMin)) {
            log.warn("Ошибка. Дата релиза указана до 28.12.1895");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка. Продолжительность фильма должна быть положительным числом и больше 0");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом и больше 0");
        }
    }
}