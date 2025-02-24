package ru.yandex.practicum.filmorate;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FilmDbStorage.class, FilmRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class, MpaDbStorage.class, MpaRowMapper.class})
class FilmoRateAppTests {

    private final UserDbStorage userDbStorage;
    private static User user1;
    private static User user2;
    private static User user3;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
    }

    @BeforeEach
    void setUp() {
        user1 = new User();
        LocalDate birthdayUser1 = LocalDate.of(1985, 10, 5);
        user1.setName("UserName1");
        user1.setEmail("User1@yandex.ru");
        user1.setLogin("UserLogin1");
        user1.setBirthday(birthdayUser1);

        user2 = new User();
        LocalDate birthdayUser2 = LocalDate.of(2001, 1, 1);
        user2.setName("userName2");
        user2.setEmail("email2@mail.ru");
        user2.setLogin("userLogin2");
        user2.setBirthday(birthdayUser2);

        user3 = new User();
        LocalDate birthdayUser3 = LocalDate.of(2006, 10, 19);
        user3.setName("userName3");
        user3.setEmail("email3@mail.ru");
        user3.setLogin("userLogin3");
        user3.setBirthday(birthdayUser3);

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
        genre1.setName("Комедия");

        genre2 = new Genre();
        genre2.setName("Боевик");

        mpa1 = new Mpa();
        mpa1.setId(1L);
        mpa1.setName("G");

        mpa2 = new Mpa();
        mpa2.setId(5L);
        mpa2.setName("NC-17");

        Set<Long> likesFilm3 = new HashSet<>();
        likesFilm3.add(user1.getId());
        likesFilm3.add(user2.getId());
        film3.setLikes(likesFilm3);
    }

    @Test
    void testFindUserById() {
        userDbStorage.createUser(user1);

        Optional.ofNullable(userDbStorage.getUserById(user1.getId()));
        User user = userDbStorage.getUserById(user1.getId());

        assertNotNull(user);
        assertEquals("UserName1", user.getName());
    }

    @Test
    void testFindAllUser() {
        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        List<User> users = (List<User>) userDbStorage.getUsers();
        assertThat(users)
                .isNotEmpty()
                .anySatisfy(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
        assertEquals(2, users.size());
    }

    @Test
    void testFindByIds() {
        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);
        Set<Long> ids = Set.of(user1.getId(), user2.getId());

        List<User> users = userDbStorage.getUserByIds(ids);

        assertThat(users)
                .isNotEmpty()
                .anySatisfy(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
        assertEquals(users.size(), 2);
    }

    @Test
    void testUserSave() {
        userDbStorage.createUser(user1);
        User user = userDbStorage.getUserById(user1.getId());

        assertEquals(user.getName(), user1.getName());
    }

    @Test
    void testUserUpdate() {
        userDbStorage.createUser(user1);

        user2.setId(user1.getId());

        userDbStorage.updateUser(user2);
        User userUpdated = userDbStorage.getUserById(user1.getId());

        assertEquals(userUpdated.getName(), user2.getName());
        assertEquals(userUpdated.getLogin(), user2.getLogin());
        assertEquals(userUpdated.getEmail(), user2.getEmail());
        assertEquals(userUpdated.getBirthday(), user2.getBirthday());
    }

    @Test
    void testUserDelete() {
        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        userDbStorage.deleteUsers();
        List<User> users = (List<User>) userDbStorage.getUsers();

        assertEquals(0, users.size());
    }

    @Test
    void addFriend() {
        User newUser1 = userDbStorage.createUser(user1);
        User newUser2 = userDbStorage.createUser(user2);

        userDbStorage.addFriend(newUser1.getId(), newUser2.getId(), FriendStatus.UNCONFIRMED);

        assertTrue(userDbStorage.isFriendshipExists(newUser1.getId(), newUser2.getId()));
    }

    @Test
    void updateFriendStatusTest() {
        User newUser1 = userDbStorage.createUser(user1);
        User newUser2 = userDbStorage.createUser(user2);

        userDbStorage.addFriend(newUser1.getId(), newUser2.getId(), FriendStatus.UNCONFIRMED);
        userDbStorage.updateFriendStatus(newUser1.getId(), newUser2.getId(), FriendStatus.CONFIRMED);

        List<User> friends = List.copyOf(userDbStorage.getUserById(newUser1.getId()).getFriends());
        friends.get(0)
    }

    @Test
    void isFriendshipExistsTest() {
        User newUser1 = userDbStorage.createUser(user1);
        User newUser2 = userDbStorage.createUser(user2);
        User newUser3 = userDbStorage.createUser(user3);

        userDbStorage.addFriend(newUser1.getId(), newUser2.getId(), FriendStatus.UNCONFIRMED);
        userDbStorage.addFriend(newUser1.getId(), newUser3.getId(), FriendStatus.UNCONFIRMED);
        userDbStorage.addFriend(newUser3.getId(), newUser1.getId(), FriendStatus.CONFIRMED);

        User updatedUser1 = userDbStorage.getUserById(newUser1.getId());
        User updatedUser2 = userDbStorage.getUserById(newUser2.getId());
        User updatedUser3 = userDbStorage.getUserById(newUser3.getId());
    }

    @Test
    void deleteFriend() {
        User newUser1 = userDbStorage.createUser(user1);
        User newUser2 = userDbStorage.createUser(user2);
        User newUser3 = userDbStorage.createUser(user3);
        userDbStorage.addFriend(newUser1.getId(), newUser2.getId(), FriendStatus.UNCONFIRMED);
        userDbStorage.addFriend(newUser1.getId(), newUser3.getId(), FriendStatus.UNCONFIRMED);
        userDbStorage.addFriend(newUser3.getId(), newUser1.getId(), FriendStatus.CONFIRMED);

        userDbStorage.deleteFriend(newUser1.getId(), newUser3.getId());

        User updatedUser1 = userDbStorage.getUserById(newUser1.getId());
        User updatedUser2 = userDbStorage.getUserById(newUser2.getId());
        User updatedUser3 = userDbStorage.getUserById(newUser3.getId());

        assertEquals(1, userDbStorage.getUserById(updatedUser1.getId()).getFriends().size());
        assertEquals(0, updatedUser2.getFriends().size());
        assertEquals(1, updatedUser3.getFriends().size());
    }

    @Test
    void testFindFilmById() {
        filmDbStorage.createFilm(film1);
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(film1.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
        assertEquals("filmName1", film1.getName());
    }

    @Test
    void testFindAllFilms() {
        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);

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
        Film filmCreate = filmDbStorage.createFilm(film3);
        Film filmGet = filmDbStorage.getFilmById(film3.getId());

        assertEquals(filmCreate.getName(), filmGet.getName());
    }

    @Test
    void testFilmUpdate() { //doesn't work
        Film oldFilm = filmDbStorage.createFilm(film1);
        film2.setId(oldFilm.getId());
        film2.setMpa(mpa2);

        Film updateFilm = filmDbStorage.updateFilm(film2);

        assertEquals(updateFilm.getName(), film2.getName());
        assertEquals(updateFilm.getDescription(), film2.getDescription());
    }

    @Test
    void testFilmsDelete() {
        filmDbStorage.createFilm(film2);
        filmDbStorage.createFilm(film3);

        filmDbStorage.deleteFilms();
        List<Film> films = (List<Film>) filmDbStorage.getFilms();

        assertEquals(0, films.size());
    }

    @Test
    void testAddLike() {
        User userCreate = userDbStorage.createUser(user1);
        Film filmCreate = filmDbStorage.createFilm(film1);

        filmDbStorage.addLike(filmCreate.getId(), userCreate.getId());

        Integer likesCount = filmDbStorage.getFilmById(filmCreate.getId()).getLikes().size();
        assertEquals(1, likesCount);
    }

    @Test
    void testDeleteLike() {
        User userCreateOne = userDbStorage.createUser(user1);
        User userCreateTwo = userDbStorage.createUser(user2);
        Film filmCreate = filmDbStorage.createFilm(film2);
        filmDbStorage.addLike(filmCreate.getId(), userCreateOne.getId());
        filmDbStorage.addLike(filmCreate.getId(), userCreateTwo.getId());

        Integer likesCount = filmDbStorage.deleteLike(filmCreate.getId(), userCreateOne.getId());

        assertEquals(1, likesCount);
    }

    @Test
    void testGetPopularFilm() {
        User userCreateOne = userDbStorage.createUser(user1);
        User userCreateTwo = userDbStorage.createUser(user2);
        Film filmCreateOne = filmDbStorage.createFilm(film1);
        Film filmCreateTwo = filmDbStorage.createFilm(film2);
        Film filmCreateThree = filmDbStorage.createFilm(film3);
        filmDbStorage.addLike(filmCreateOne.getId(), userCreateOne.getId());
        filmDbStorage.addLike(filmCreateThree.getId(), userCreateOne.getId());
        filmDbStorage.addLike(filmCreateThree.getId(), userCreateTwo.getId());

        List<Film> films = (List<Film>) filmDbStorage.getPopularFilms(3L);

        assertEquals(films.get(0), filmDbStorage.getFilmById(filmCreateThree.getId()));
        assertEquals(films.get(1), filmDbStorage.getFilmById(filmCreateOne.getId()));
        assertEquals(films.get(2), filmDbStorage.getFilmById(filmCreateTwo.getId()));

    }

    @Test
    void testFindGenreById() {
        Film film = filmDbStorage.createFilm(film1);
        Film filmFromDb = filmDbStorage.getFilmById(film.getId());
        List<Genre> genres = new ArrayList<>();
        genre1.setId(1L);
        genres.add(genre1);
        filmFromDb.setGenres(genres);

        Genre genre = genreDbStorage.getGenreById(1L);

        assertEquals(filmFromDb.getGenres().get(0), genre);
    }

    @Test
    void testFindAllGenres() {
        List<Genre> genres = (List<Genre>) genreDbStorage.getGenres();

        assertEquals(6, genres.size());
    }

    @Test
    void getGenreByIds() {
        List<Long> genres = new ArrayList<>();
        Genre genre1 = genreDbStorage.getGenreById(1L);
        Genre genre2 = genreDbStorage.getGenreById(2L);
        genres.add(genre1.getId());
        genres.add(genre2.getId());

        List<Genre> genreIds = genreDbStorage.getGenreByIds(genres);

        assertEquals(2, genreIds.size());
    }

    @Test
    void testFindMpaById() {
        Film film = filmDbStorage.createFilm(film1);
        Film filmFromDb = filmDbStorage.getFilmById(film.getId());
        filmFromDb.setMpa(mpa1);

        Mpa mpa = mpaDbStorage.getMpaById(mpa1.getId());

        assertEquals(mpa, filmFromDb.getMpa());
    }

    @Test
    void testFindAllMpas() {
        List<Mpa> mpas = (List<Mpa>) mpaDbStorage.getMpas();

        assertEquals(5, mpas.size());
    }
}