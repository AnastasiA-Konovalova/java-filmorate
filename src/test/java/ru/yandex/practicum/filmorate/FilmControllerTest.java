package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {

    private static FilmController filmController;
    private static Film film1;
    private static Film film2;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        filmController.films.clear();

        film1 = new Film();
        film1.setName("NameFilm_1");
        film1.setDescription("Awesome film_1");
        LocalDate releaseDateFilm1 = LocalDate.of(2020, 11, 10);
        film1.setReleaseDate(releaseDateFilm1);
        film1.setDuration(120);

        film2 = new Film();
        film2.setName("NameFilm_2");
        film2.setDescription("Awesome film_2");
        LocalDate releaseDateFilm2 = LocalDate.of(2004, 9, 27);
        film2.setReleaseDate(releaseDateFilm2);
        film2.setDuration(140);
    }

    @Test
    void getFilmsTest() {
        filmController.addFilm(film1);
        filmController.addFilm(film2);

        Collection<Film> films = filmController.getFilms();

        assertEquals(2, films.size());
    }

    @Test
    void addFilmTest() {
        Film film = filmController.addFilm(film1);

        assertEquals(film.getName(), film1.getName());
        assertEquals(film.getDescription(), film1.getDescription());
        assertEquals(film.getReleaseDate(), film1.getReleaseDate());
        assertEquals(film.getDuration(), film1.getDuration());
        assertEquals(film.getId(), film1.getId());
    }

    @Test
    void addAlreadyExistsFilmTest() {
        filmController.addFilm(film1);
        film2.setName(film1.getName());

        Exception exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film2));
        String expectedMessage = "Фильм с таким названием уже есть в списке";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void addEmptyNameFilmTest() {
        film1.setName("               ");

        Exception exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film1));
        String expectedMessage = "Название не может быть пустым";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void addTooLongFilmDescriptionTest() {
        film1.setDescription("DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                "DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                "DescriptionDescriptionDescriptionEnd");

        Exception exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film1));
        String expectedMessage = "Максимальная длина описания — 200 символов";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
        assertEquals(201, film1.getDescription().length());
    }

    @Test
    void addWrongReleaseDateTest() {
        LocalDate releaseDate = LocalDate.of(1895, 12, 27);
        film1.setReleaseDate(releaseDate);

        Exception exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film1));
        String expectedMessage = "Дата релиза не может быть раньше 28 декабря 1895 года";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void addWrongDurationDateTest() {
        film1.setDuration(-30);

        Exception exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film1));
        String expectedMessage = "Продолжительность фильма должна быть положительным числом и больше 0";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateRequestWithoutIdTest() {
        film1.setId(null);
        Exception exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film1));
        String expectedMessage = "Id должен быть указан";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateIdNotExistsTest() {
        film1.setId(900L);

        Exception exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film1));
        String expectedMessage = "Фильм с таким ID не найден";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateFilmAlreadyExistsTest() {
        Film film3 = new Film();
        LocalDate releaseDateFilm3 = LocalDate.of(1953, 3, 5);
        film3.setName("NameFilm_3");
        film3.setDescription("DescriptionFilm_3");
        film3.setReleaseDate(releaseDateFilm3);
        film3.setDuration(40);
        filmController.addFilm(film1);
        filmController.addFilm(film3);
        System.out.println(film1.getId());
        film2.setName(film3.getName());
        film2.setId(film1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        String expectedMessage = "Фильм с таким названием уже есть в списке";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateEmptyFilmNameTest() {
        filmController.addFilm(film1);
        film2.setName(null);
        film2.setId(film1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        String expectedMessage = "Название не может быть пустым";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateTooLongDescriptionTest() {
        filmController.addFilm(film1);
        film2.setDescription("DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                "DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                "DescriptionDescriptionDescriptionEnd");
        film2.setId(film1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        String expectedMessage = "Максимальная длина описания — 200 символов";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateWrongReleaseDateTest() {
        filmController.addFilm(film1);
        LocalDate releaseDate = LocalDate.of(1895, 12, 27);
        film2.setReleaseDate(releaseDate);
        film2.setId(film1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        String expectedMessage = "Дата релиза не может быть раньше 28 декабря 1895 года";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void updateWrongDurationDateTest() {
        filmController.addFilm(film1);
        film2.setDuration(-100);
        film2.setId(film1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        String expectedMessage = "Продолжительность фильма должна быть положительным числом и больше 0";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}