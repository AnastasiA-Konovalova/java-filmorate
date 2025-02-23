package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreStorage {
    Collection<Genre> getGenres();

    Genre getGenreById(Long id);

    List<Genre> getGenreByIds(List<Long> ids);
}