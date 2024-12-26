package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        Timestamp releaseDate = resultSet.getTimestamp("releaseDate");
        film.setReleaseDate(LocalDate.from(releaseDate.toInstant()));
        film.setDuration(resultSet.getInt("duration"));

        return film;
    }
}
