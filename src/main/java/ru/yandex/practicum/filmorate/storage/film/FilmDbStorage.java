package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
//для обозначения класса как компонента, отвечающего за доступ к данным (DAO или Repository).
// Это часть Spring Framework, и она помогает разработчикам создать и поддерживать слой доступа
// к данным, работая с базами данных или другими хранилищами.
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM film";
    private static final String INSERT_QUERY = "INSERT INTO film(name, description, release_date, duration)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, " +
            "release_date = ?, duration = ? WHERE id = ?";
    private static final String DELETE_ALL = "SELECT * FROM film";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper, Film.class);
    }

    public Optional<Film> findById(Long filmId) {
        return findById(FIND_BY_ID_QUERY, filmId);
    }

    public List<Film> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    public Film saveFilm(Film film) {
        Long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.from(Instant.from(film.getReleaseDate())),
                film.getDuration()
        );
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.from(Instant.from(film.getReleaseDate())),
                film.getDuration()
        );
        return film;
    }

    public boolean delete(Long filmId) {
        return delete(DELETE_ALL, filmId);
    }
}