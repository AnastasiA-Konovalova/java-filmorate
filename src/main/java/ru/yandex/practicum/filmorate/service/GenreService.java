package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mapping.GenreMapperToDto;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;
    private final GenreMapperToDto genreMapperToDto;

    public Collection<GenreDto> getGenres() {
        List<Genre> genres = (List<Genre>) genreDbStorage.getGenres();

        return genres.stream()
                .map(genre -> genreMapperToDto.toDto(genre))
                .toList();
    }

    public GenreDto getGenreById(Long id) {
        Genre genre = genreDbStorage.getGenreById(id);
        if (genre == null) {
            log.warn("Неправильно введен id жанра");
            throw new NotFoundException("User с таким id отсутствует");
        }
        return genreMapperToDto.toDto(genre);
    }

    /*public List<Long> getGenreByIds(Film film) {
        List<Genre> genreList = genreDbStorage.getGenreById()
        return null;
    }*/
}
