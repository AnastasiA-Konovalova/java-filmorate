package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.util.Collection;

@Repository
//для обозначения класса как компонента, отвечающего за доступ к данным (DAO или Repository).
// Это часть Spring Framework, и она помогает разработчикам создать и поддерживать слой доступа
// к данным, работая с базами данных или другими хранилищами.
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String FIND_BY_ID_QUERY =
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, " +
                    "f.DURATION, f.RATING_ID, r.NAME, fg.GENRE_ID, g.NAME " +
                    "FROM FILMS f \n" +
                    "LEFT JOIN FILM_GENRE fg ON f.id = fg.FILM_ID \n" +
                    "LEFT JOIN GENRE g ON g.id = fg.genre_id" +
                    "LEFT JOIN RATING r ON f.rating_id = r.id" +
                    "WHERE f.id = ?";

//            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, " +
//                    "f.DURATION, f.RATING_ID, r.NAME AS RATING_NAME, " +
//                    "g.ID AS GENRE_ID, g.NAME AS GENRE_NAME "

    private static final String FIND_ALL_QUERY = "SELECT id, name, description, release_date, " +
            "duration, rating_id FROM films";
    private static final String INSERT_QUERY = "INSERT INTO films(id, name, description, release_date, duration)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, " +
            "release_date = ?, duration = ? WHERE id = ?";
    private static final String DELETE_ALL = "DELETE FROM films";

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query(FIND_ALL_QUERY, filmRowMapper);
    }

    @Override
    public Film getFilmById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, filmRowMapper, id);
    }

    @Override
    public Film createFilm(Film film) { // тут нужен filmRowMapper?
        jdbcTemplate.update(INSERT_QUERY,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) { // тут нужен filmRowMapper?
        jdbcTemplate.update(UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getId());
        return newFilm;
    }

    @Override
    public void deleteFilms() {
        jdbcTemplate.update(DELETE_ALL);
    } // тут нужен filmRowMapper?
}