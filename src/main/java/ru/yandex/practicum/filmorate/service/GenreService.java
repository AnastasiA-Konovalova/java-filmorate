package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mapping.GenreMapper;
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
                .map(GenreMapper::toDto)
                .toList();
    }

    public GenreDto getGenreById(Long id) {
        return genreDbStorage.getGenreById(id)
                .map(GenreMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Неправильно введен id жанра");
                    return new NotFoundException("Жанр с id = " + id + " отсутствует");
                });
    }

    public List<GenreDto> getGenreByIds(List<Long> ids) {
        List<Genre> genreList = genreDbStorage.getGenreByIds(ids);
        if (genreList == null || genreList.isEmpty()) {
            log.warn("Неправильно передан список id жанров");
            throw new NotFoundException("Жанры с такими ids отсутствуют");
        }
        return genreList.stream()
                .map(GenreMapper::toDto)
                .toList();
    }
}