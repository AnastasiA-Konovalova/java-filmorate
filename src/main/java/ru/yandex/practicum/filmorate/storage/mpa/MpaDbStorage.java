package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private static final String FIND_ALL_MPA = "SELECT * FROM rating";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating WHERE id = ?";
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaRowMapper;

    public Collection<Mpa> getMpas() {
        return jdbcTemplate.query(FIND_ALL_MPA, mpaRowMapper);
    }

    public Optional<Mpa> getMpaById(Long id) {
        try {
            Mpa mpa = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, mpaRowMapper, id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}