package ru.yandex.practicum.filmorate.service.mapping;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;

@Service
public class FilmMapperToDto {

    public FilmDto toDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setRatingId(filmDto.getRatingId());

        return filmDto;
    }

//    public Film toModel(FilmDto filmDto) {
//        return new Film(filmDto.getId(),
//                filmDto.getName(),
//                filmDto.getDescription(),
//                filmDto.getReleaseDate(),
//                filmDto.getDuration(),
//                //filmDto.getLikes(),
//                new ArrayList<>(),
//                null);
//    }
}

