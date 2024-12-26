package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FilmDbStorage.class, FilmRowMapper.class})
class FilmoRateAppTests {

    private final UserDbStorage userStorage;
    private static User user1;
    private static User user2;
    private final FilmDbStorage filmDbStorage;
    private static Film film1;
    private static Film film2;

    @BeforeAll
    static void setUp () {
        user1 = new User();
        LocalDate birthdayUser1 = LocalDate.of(1985, 10, 5);
        user1.setId(4L);
        user1.setName("UserName1");
        user1.setEmail("User1@yandex.ru");
        user1.setLogin("UserLogin1");
        user1.setBirthday(birthdayUser1);

        user2 = new User();
        LocalDate birthdayUser2 = LocalDate.of(2001, 12, 6);
        user2.setName("UserName2");
        user2.setEmail("User2@yandex.ru");
        user2.setLogin("UserLogin2");
        user2.setBirthday(birthdayUser2);

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
    void testFindUserById() {
        Optional<User> userOptional = userStorage.findById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void testFindAllUser() {
        List<User> users = userStorage.findAll();

        assertThat(users)
                .isNotEmpty()
                .anySatisfy(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

@Test
    void testFindByIds() {
        Set<Long> ids = Set.of(1L, 2L, 3L);
        List<User> users = userStorage.findByIds(ids);

        assertThat(users)
                .isNotEmpty()
                .anySatisfy(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void testUserSave() { // doesnt work
        Optional<User> userOptional = Optional.ofNullable(userStorage.saveUser(user1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userSave ->
                        assertThat(userSave).hasFieldOrPropertyWithValue("id", user1.getId())
                );
    }

    @Test
    void testUserUpdate() {
        userStorage.saveUser(user1);
        user2.setId(user1.getId());
        Optional.ofNullable(userStorage.update(user2));

        assertEquals(user1, user2);
    }

    @Test
    void testUserDelete() {
        userStorage.saveUser(user1);
        userStorage.saveUser(user2);

        userStorage.delete(user1.getId());
        List<User> users = userStorage.findAll();

        assertEquals(1, users.size());
    }

    //--------------------------

    @Test
    void testFindFilmById() {
        Optional<Film> filmOptional = filmDbStorage.findById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void testFindAllFilms() {
        List<Film> films = filmDbStorage.findAll();

        assertThat(films)
                .isNotEmpty()
                .anySatisfy(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void testFilmSave() {
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.saveFilm(film1));
        List<Film> films = filmDbStorage.findAll();

        assertEquals(1, films.size());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(userSave ->
                        assertThat(userSave).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void testFilmUpdate() {
        filmDbStorage.saveFilm(film1);
        film2.setId(film1.getId());
        filmDbStorage.update(film2);

        assertEquals(film1, film2);
    }

    @Test
    void testFilmDelete() {
        filmDbStorage.saveFilm(film1);
        filmDbStorage.saveFilm(film2);

        filmDbStorage.delete(film1.getId());
        List<Film> films = filmDbStorage.findAll();

        assertEquals(1, films.size());
    }


}

