package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FilmDbStorage.class, FilmRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class, MpaDbStorage.class, MpaRowMapper.class})
class FilmoRateAppTests {

    private final UserDbStorage userDbStorage;
    private static User user1;
    private static User user2;
    private final FilmDbStorage filmDbStorage;
    private static Film film1;
    private static Film film2;
    private static Film film3;
    private final GenreDbStorage genreDbStorage;
    private static Genre genre1;
    private static Genre genre2;
    private final MpaDbStorage mpaDbStorage;
    private static Mpa mpa1;
    private static Mpa mpa2;

    @BeforeAll
    static void setUp() {
        user1 = new User();
        LocalDate birthdayUser1 = LocalDate.of(1985, 10, 5);
        user1.setId(4L);
        user1.setName("UserName1");
        user1.setEmail("User1@yandex.ru");
        user1.setLogin("UserLogin1");
        user1.setBirthday(birthdayUser1);

        user2 = new User();
        user2.setId(2L);
        LocalDate birthdayUser2 = LocalDate.of(2001, 1, 1);
        user2.setName("userName2");
        user2.setEmail("email2@mail.ru");
        user2.setLogin("userLogin2");
        user2.setBirthday(birthdayUser2);

        film1 = new Film();
        film1.setId(1L);
        film1.setName("filmName1");
        film1.setDescription("filmDescription1");
        LocalDate releaseDateFilm1 = LocalDate.of(2003, 1, 1);
        film1.setReleaseDate(releaseDateFilm1);
        film1.setDuration(120);

        film2 = new Film();
        film2.setId(3L);
        film2.setName("NameFilm2");
        film2.setDescription("AwesomeFilm2");
        LocalDate releaseDateFilm2 = LocalDate.of(2004, 9, 27);
        film2.setReleaseDate(releaseDateFilm2);
        film2.setDuration(140);

        film3 = new Film();
        film3.setId(4L);
        film3.setName("NameFilm3");
        film3.setDescription("AwesomeFilm3");
        LocalDate releaseDateFilm3 = LocalDate.of(2020, 6, 20);
        film3.setReleaseDate(releaseDateFilm3);
        film3.setDuration(160);

        genre1 = new Genre();
        genre1.setId(1L);
        genre1.setName("genreName1");

        genre2 = new Genre();
        genre2.setName("genreName2");

        mpa1 = new Mpa();
        mpa1.setId(1L);
        mpa1.setName("ratingName1");

        mpa2 = new Mpa();
        mpa2.setName("ratingName2");
    }

    @Test
    void testFindUserById() {
        Optional.ofNullable(userDbStorage.getUserById(3L));
        User user = userDbStorage.getUserById(user2.getId());

        assertEquals(user2, user);
    }

    @Test
    void testFindAllUser() {
        List<User> users = (List<User>) userDbStorage.getUsers();
        assertThat(users)
                .isNotEmpty()
                .anySatisfy(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
        assertEquals(3, users.size());
    }

    @Test
    void testFindByIds() {
        Set<Long> ids = Set.of(1L, 2L, 3L);
        List<User> users = userDbStorage.getUserByIds(ids);

        assertThat(users)
                .isNotEmpty()
                .anySatisfy(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void testUserSave() { // doesnt work
        Optional.ofNullable(userDbStorage.createUser(user1));

        User user = userDbStorage.getUserById(user1.getId());

        assertEquals(user1, user);
    }

    @Test
    void testUserUpdate() {
        userDbStorage.createUser(user1);
        user2.setId(user1.getId());
        userDbStorage.updateUser(user2);

        User updateUser = userDbStorage.getUserById(user1.getId());
        assertEquals(user2, updateUser);
    }

    @Test
    void testUserDelete() {
        userDbStorage.createUser(user1);
        userDbStorage.deleteUsers();
        List<User> users = (List<User>) userDbStorage.getUsers();

        assertEquals(0, users.size());
    }

    //--------------------------

    @Test
    void testFindFilmById() {
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(1L));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void testFindAllFilms() {
        List<Film> films = (List<Film>) filmDbStorage.getFilms();
        assertThat(films)
                .isNotEmpty()
                .anySatisfy(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
        assertEquals(2, films.size());
    }

    @Test
    void testFilmSave() {
        Optional.ofNullable(filmDbStorage.createFilm(film3));
        Film film = filmDbStorage.getFilmById(film3.getId());

        assertEquals(film3, film);
    }

    @Test
    void testFilmUpdate() {
        filmDbStorage.createFilm(film2);
        film1.setId(film2.getId());
        filmDbStorage.updateFilm(film2);

        Film updateFilm = filmDbStorage.getFilmById(film2.getId());
        assertEquals(film2, updateFilm);
    }

    @Test
    void testFilmDelete() {
        filmDbStorage.createFilm(film2);
        filmDbStorage.createFilm(film3);

        filmDbStorage.deleteFilms();
        List<Film> films = (List<Film>) filmDbStorage.getFilms();

        assertEquals(0, films.size());
    }

    //___________________________________________
    //genre and mpa

    @Test
    void testFindGenreById() {
        Optional.ofNullable(genreDbStorage.getGenreById(1L));

        Genre genre = genreDbStorage.getGenreById(genre1.getId());

        assertEquals(genre1, genre);
    }

    @Test
    void testFindAllGenres() {
        List<Genre> genres = (List<Genre>) genreDbStorage.getGenres();
//
        assertEquals(1, genres.size());
    }

    @Test
    void testFindMpaById() {
        Optional.ofNullable(mpaDbStorage.getMpaById(1L));
        System.out.println(mpa1);

        Mpa mpa = mpaDbStorage.getMpaById(mpa1.getId());
        System.out.println(mpa);

        assertEquals(mpa1, mpa);
    }

    @Test
    void testFindAllMpas() {
        List<Mpa> mpas = (List<Mpa>) mpaDbStorage.getMpas();

        assertEquals(1, mpas.size());
    }

}
