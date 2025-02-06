package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    private MpaService mpaService;
    private GenreService genreService;
    private final String id = "id";
    private final String description = "description";
    private final String name = "name";
    private final String duration = "duration";
    private final String release_date = "release_date";
    private final String rating_id = "rating_id";
    private final String genre_id = "genre_id";



    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong(id));
        film.setName(resultSet.getString(name));
        film.setDescription(resultSet.getString(description));
        film.setDuration(resultSet.getInt(duration));
        Timestamp releaseDate = resultSet.getTimestamp(release_date);
        film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());

        long ratingId = resultSet.getLong(rating_id);
        if (!resultSet.wasNull()) {
            Mpa mpa = new Mpa();
            mpa.setId(ratingId);
            mpa.setName(resultSet.getString("r.name")); // Получаем название рейтинга
            film.setRating(mpa);
        }

//        Mpa mpa = new Mpa();
//        mpa.setId(resultSet.getLong(rating_id));

        Set<Genre> genres = new HashSet<>();
        while (!resultSet.isAfterLast()) { // Проверяем, не вышли ли мы за пределы набора данных
            long genreId = resultSet.getLong(genre_id);
            if (!resultSet.wasNull()) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(resultSet.getString("g.name")); // Название жанра
                genres.add(genre);
            }

            if (!resultSet.next()) break; // Переход к следующей строке или выход из цикла
        }
        film.setGenres(List.copyOf(genres));

        return film;
    }
}
