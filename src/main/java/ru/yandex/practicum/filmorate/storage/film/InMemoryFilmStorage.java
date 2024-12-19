package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        log.info("Запрошен список фильмов");
        return films.values();
    }

    @Override
    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            log.warn("Ошибка. Фильма с таким id нет в списке");
            throw new NotFoundException("Фильма с таким id нет в списке");
        }
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
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

    @Override
    public Film updateFilm(Film newFilm) {
        log.info("Обновление характеристик фильма");
        if (newFilm.getId() == null) {
            log.warn("Ошибка. Не указан id");
            throw new ValidationException("Id должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.info("Ошибка. Фильм с таким ID не найден");
            throw new NotFoundException("Фильма с таким id нет в списке");
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

    @Override
    public void deleteFilms() {
        films.clear();
    }

    private long generateId() {
        return films.keySet().stream().max(Long::compareTo).map(i -> i + 1).orElse(1L);
    }
}