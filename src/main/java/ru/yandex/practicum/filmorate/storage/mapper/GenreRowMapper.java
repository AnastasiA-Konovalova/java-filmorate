package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<Genre> {
    private static final String ID = "id";
    private static final String NAME = "name";

    @Override
    public Genre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getLong(ID));
        genre.setName(resultSet.getString(NAME));

        return genre;
    }
}