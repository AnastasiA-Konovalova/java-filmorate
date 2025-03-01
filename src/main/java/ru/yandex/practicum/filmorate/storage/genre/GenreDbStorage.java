package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final String FIND_ALL_GENRES = "SELECT * FROM genre ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_BY_IDS = "SELECT * FROM genre WHERE id IN (:ids)";

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    public Collection<Genre> getGenres() {
        return jdbcTemplate.query(FIND_ALL_GENRES, genreRowMapper);
    }

    public Optional<Genre> getGenreById(Long id) {
        try {
            Genre genre = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, genreRowMapper, id);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Genre> getGenreByIds(List<Long> ids) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("ids", ids);

        return namedParameterJdbcTemplate.query(FIND_BY_IDS, parameters, genreRowMapper);
    }
}