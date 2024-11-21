package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    @Autowired
    private FilmController filmController;
    private Film film1;
    private Film film2;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmController.deleteFilms();

        film1 = new Film();
        film1.setName("NameFilm1");
        film1.setDescription("Awesome film1");
        LocalDate releaseDateFilm1 = LocalDate.of(2020, 11, 10);
        film1.setReleaseDate(releaseDateFilm1);
        film1.setDuration(120);

        film2 = new Film();
        film2.setName("NameFilm2");
        film2.setDescription("Awesome film2");
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
    void emptyNameConstraintViolationFilmTest() {
        film1.setName(null);

        Set<ConstraintViolation<Film>> violationSet = validator.validate(film1);
        assertFalse(violationSet.isEmpty(), "Ожидались нарушения валидации для пустого имени фильма");

        assertEquals(1, violationSet.size());
        assertEquals(violationSet.stream().findFirst().get().getMessageTemplate(), "Название не может быть пустым");
    }

    @Test
    void tooLongDescriptionConstraintViolationFilmTest() {
        film1.setDescription("DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                "DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                "DescriptionDescriptionDescriptionEnd");

        Set<ConstraintViolation<Film>> violationSet = validator.validate(film1);
        assertFalse(violationSet.isEmpty(), "Ожидались нарушения валидации для длины символов");
        assertEquals(1, violationSet.size());
        assertEquals(violationSet.stream().findFirst().get().getMessageTemplate(),
                "Длина описания не может быть больше 200 символов");
    }

    @Test
    void wrongReleaseDateConstraintViolationFilmTest() {
        LocalDate releaseDate = LocalDate.of(1895, 12, 27);
        film1.setReleaseDate(releaseDate);

        Set<ConstraintViolation<Film>> violationSet = validator.validate(film1);
        assertFalse(violationSet.isEmpty(), "Ожидались нарушения валидации для даты релиза");
        assertEquals(1, violationSet.size());
        assertEquals(violationSet.stream().findFirst().get().getMessageTemplate(),
                "Дата релиза не может быть раньше 28 декабря 1895 года");
    }

    @Test
    void wrongDurationDateConstraintViolationFilmTest() {
        film1.setDuration(-30);

        Set<ConstraintViolation<Film>> violationSet = validator.validate(film1);
        assertFalse(violationSet.isEmpty(), "Ожидались нарушения валидации для продолжительности фильма");
        assertEquals(1, violationSet.size());
        assertEquals(violationSet.stream().findFirst().get().getMessageTemplate(),
                "Продолжительность фильма должна быть позитивным числом");
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

        Exception exception = assertThrows(ResponseStatusException.class, () -> filmController.updateFilm(film1));
        String expectedMessage = "404 NOT_FOUND \"Фильм с таким id не найден\"";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateFilmAlreadyExistsTest() {
        Film film3 = new Film();
        LocalDate releaseDateFilm3 = LocalDate.of(1953, 3, 5);
        film3.setName("NameFilm3");
        film3.setDescription("DescriptionFilm3");
        film3.setReleaseDate(releaseDateFilm3);
        film3.setDuration(40);
        filmController.addFilm(film1);
        filmController.addFilm(film3);
        film2.setName(film3.getName());
        film2.setId(film1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        String expectedMessage = "Фильм с таким названием уже есть в списке";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void addFilmSuccess() {
        filmController.addFilm(film1);
        filmController.addFilm(film2);

        Collection<Film> films = filmController.getFilms();

        assertTrue(films.contains(film1));
        assertTrue(films.contains(film2));
        assertEquals(2, films.size());
    }

    @Test
    void updateFilmSuccess() {
        filmController.addFilm(film1);
        film2.setId(film1.getId());

        filmController.updateFilm(film2);

        Collection<Film> films = filmController.getFilms();
        boolean filmExists = films.stream().anyMatch(film -> film.getName().equals("NameFilm2"));
        assertEquals(1, films.size());
        assertTrue(filmExists);
    }
}