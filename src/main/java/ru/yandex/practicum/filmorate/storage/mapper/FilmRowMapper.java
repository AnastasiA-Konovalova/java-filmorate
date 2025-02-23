package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class FilmRowMapper implements ResultSetExtractor<List<Film>> {
    private static final String ID = "id";
    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String DURATION = "duration";
    private static final String RELEASE_DATE = "release_date";
    private static final String MPA_ID = "mpa_id";
    private static final String MPA_NAME = "mpa_name";
    private static final String GENRE_ID = "genre_id";
    private static final String GENRE_NAME = "genre_name";
    private static final String LIKE_USERS = "like_users";

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {
        Map<Long, Film> filmsMap = new LinkedHashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong(ID);

            Film film = filmsMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString(NAME));
                film.setDescription(rs.getString(DESCRIPTION));
                film.setDuration(rs.getInt(DURATION));
                film.setReleaseDate(rs.getTimestamp(RELEASE_DATE).toLocalDateTime().toLocalDate());

                long ratingId = rs.getLong(MPA_ID);
                if (!rs.wasNull()) {
                    Mpa mpa = new Mpa();
                    mpa.setId(ratingId);
                    mpa.setName(rs.getString(MPA_NAME));
                    film.setMpa(mpa);
                }

                film.setGenres(new ArrayList<>());
                filmsMap.put(filmId, film);
            }

            long genreId = rs.getLong(GENRE_ID);
            if (!rs.wasNull()) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString(GENRE_NAME));

                if (!film.getGenres().contains(genre)) {
                    film.getGenres().add(genre);
                }
            }

            String userLike = rs.getString(LIKE_USERS);
            Set<Long> likes = new HashSet<>();

            if (userLike != null) {
                for (String userId : userLike.split(",")) {
                    likes.add(Long.valueOf(userId));
                }
            }
            film.setLikes(likes);
        }

        return new ArrayList<>(filmsMap.values());
    }
}