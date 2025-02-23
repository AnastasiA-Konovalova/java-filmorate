package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mapping.GenreMapperToDto;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public Collection<GenreDto> getGenres() {
        List<Genre> genres = (List<Genre>) genreDbStorage.getGenres();

        return genres.stream()
                .map(GenreMapperToDto::toDto)
                .toList();
    }

    public GenreDto getGenreById(Long id) {
        Genre genre = genreDbStorage.getGenreById(id);
        if (genre == null) {
            log.warn("Неправильно введен id жанра");
            throw new NotFoundException("Жанр с таким id отсутствует");
        }
        return GenreMapperToDto.toDto(genre);
    }

    public List<GenreDto> getGenreByIds(List<Long> ids) {
        List<Genre> genreList = genreDbStorage.getGenreByIds(ids);
        if (genreList == null || genreList.isEmpty()) {
            log.warn("Неправильно передан список id жанров");
            throw new NotFoundException("Жанры с такими ids отсутствуют");
        }
        return genreList.stream()
                .map(GenreMapperToDto::toDto)
                .toList();
    }
}
