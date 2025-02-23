package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String FIND_BY_ID_QUERY = """
            SELECT f.id, f.name, f.description, f.release_date, f.duration,
                   m.id AS mpa_id, m.name AS mpa_name,
                   g.id AS genre_id, g.name AS genre_name,
                   GROUP_CONCAT(l.user_id) AS like_users,
                   COUNT(l.user_id) AS like_count
            FROM films f
            LEFT JOIN rating m ON f.rating_id = m.id
            LEFT JOIN film_genre fg ON f.id = fg.film_id
            LEFT JOIN genre g ON fg.genre_id = g.id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE f.id = ?
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration,
                     m.id, m.name, g.id, g.name
            """;

    private static final String FIND_ALL_QUERY = """
            SELECT f.id, f.name, f.description, f.release_date, f.duration,
                   m.id AS mpa_id, m.name AS mpa_name,
                   g.id AS genre_id, g.name AS genre_name,
                   GROUP_CONCAT(l.user_id) AS like_users,
                   COUNT(l.user_id) AS like_count
            FROM films f
            LEFT JOIN rating m ON f.rating_id = m.id
            LEFT JOIN film_genre fg ON f.id = fg.film_id
            LEFT JOIN genre g ON fg.genre_id = g.id
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration,
                     m.id, m.name, g.id, g.name
            """;

    private static final String FIND_POPULAR_QUERY = """
            SELECT f.id, f.name, f.description, f.release_date, f.duration,
                   m.id AS mpa_id, m.name AS mpa_name,
                   g.id AS genre_id, g.name AS genre_name,
                   GROUP_CONCAT(l.user_id) AS like_users,
                   COUNT(l.user_id) AS like_count
            FROM films f
            LEFT JOIN rating m ON f.rating_id = m.id
            LEFT JOIN film_genre fg ON f.id = fg.film_id
            LEFT JOIN genre g ON fg.genre_id = g.id
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration,
                     m.id, m.name, g.id, g.name
            ORDER BY like_count DESC
            LIMIT ?
            """;

    private static final String INSERT_INTO_FILMS = "INSERT INTO films (name, description, release_date, duration, " +
            "rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_INTO_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

    private static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, " +
            "release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String DELETE_ALL = "DELETE FROM films";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query(FIND_ALL_QUERY, new FilmRowMapper());
    }

    @Override
    public Integer addLike(Long filmId, Long userId) {
        return jdbcTemplate.update(INSERT_LIKE, filmId, userId);
    }

    @Override
    public Integer deleteLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        Optional.ofNullable(film)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id %d не найден.", filmId)));

        if (!film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь с id " + userId + " не ставил лайк фильму с id "
                    + filmId + ". Т.е. удалить лайк не получится.");
        }
        film.deleteLike(filmId);
        return jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return jdbcTemplate.query(FIND_POPULAR_QUERY, new FilmRowMapper(), count);
    }

    @Override
    public Film getFilmById(Long id) {
        List<Film> films = jdbcTemplate.query(FIND_BY_ID_QUERY, new FilmRowMapper(), id);

        if (films == null || films.isEmpty()) {
            throw new NotFoundException("Фильм с id = " + id + " не найден.");
        }

        return films.getFirst();
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_INTO_FILMS,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            film.setId(keyHolder.getKey().longValue());
        } else {
            throw new RuntimeException("Ошибка при сохранении фильма: не удалось получить ID");
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(INSERT_INTO_FILM_GENRE, film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int updatedRows = jdbcTemplate.update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        log.info("Обновлено строк: {}", updatedRows);

        if (updatedRows == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не обновлён.");
        }
        return getFilmById(film.getId());
    }

    @Override
    public void deleteFilms() {
        jdbcTemplate.update(DELETE_ALL);
    }
}