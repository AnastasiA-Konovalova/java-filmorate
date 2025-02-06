package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.Collection;
import java.util.List;

@Repository
@Qualifier("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage {
    private static final String FIND_ALL_GENRES = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_BY_IDS = "SELECT * FROM users WHERE id IN (%s)";

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

//сделать интерфейс?
    public Collection<Genre> getGenres() {
        return jdbcTemplate.query(FIND_ALL_GENRES, genreRowMapper);
    }

    public Genre getGenreById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, genreRowMapper, id);
    }

    public List<Long> getGenreById(Film film) {
        return null;
    }
}
