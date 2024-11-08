package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTest {

    private static UserController userController;
    private static User user1 = new User();
    private static User user2 = new User();

    @BeforeEach
    void setUp() {
        userController = new UserController();
        userController.users.clear();

        user1 = new User();
        LocalDate birthdayUser1 = LocalDate.of(1985, 10, 5);
        user1.setName("UserName1");
        user1.setEmail("User1@yandex.ru");
        user1.setLogin("UserLogin1");
        user1.setBirthday(birthdayUser1);

        user2 = new User();
        LocalDate birthdayUser2 = LocalDate.of(2018, 5, 10);
        user2.setName("UserName2");
        user2.setEmail("User2@yandex.ru");
        user2.setLogin("UserLogin2");
        user2.setBirthday(birthdayUser2);
    }

    @Test
    void getUsersTest() {
        userController.createUser(user1);
        userController.createUser(user2);

        Collection<User> users = userController.getUsers();

        assertEquals(2, users.size());
    }

    @Test
    void createEmptyEmailUserTest() {
        user1.setEmail(null);

        Exception exception = assertThrows(ValidationException.class, () -> userController.createUser(user1));
        String expectedMessage = "Email не может быть пустым";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void createUserEmailNotContainsSymbolAtTest() {
        user1.setEmail("User1yandex.ru");

        Exception exception = assertThrows(ValidationException.class, () -> userController.createUser(user1));
        String expectedMessage = "В email отсутствует символ '@'";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void createUserLoginIsEmptyTest() {
        user1.setLogin("  User1@ yandex.ru  ");

        Exception exception = assertThrows(ValidationException.class, () -> userController.createUser(user1));
        String expectedMessage = "Логин не может быть пустым и содержать пробелы";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void createUserLoginAlreadyExistsTest() {
        userController.createUser(user1);
        user2.setLogin(user1.getLogin());

        Exception exception = assertThrows(ValidationException.class, () -> userController.createUser(user2));
        String expectedMessage = "Пользователь с таким логином уже существует";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void createUserNameIsEmptyTest() {
        user1.setName(null);
        userController.createUser(user1);

        assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    void createUserWrongBirthdayTest() {
        LocalDate birthdayUser3 = LocalDate.now().plusDays(1);
        user1.setBirthday(birthdayUser3);

        Exception exception = assertThrows(ValidationException.class, () -> userController.createUser(user1));
        String expectedMessage = "Дата рождения не может быть в будущем";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserRequestWithoutIdTest() {
        user1.setId(null);
        Exception exception = assertThrows(ValidationException.class, () -> userController.updateUser(user1));
        String expectedMessage = "Id должен быть указан";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserIdNotExistsTest() {
        user1.setId(900L);

        Exception exception = assertThrows(ValidationException.class, () -> userController.updateUser(user1));
        String expectedMessage = "Пользователь не найден";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserEmptyEmailTest() {
        userController.createUser(user1);
        user2.setEmail(null);
        user2.setId(user1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> userController.updateUser(user2));
        String expectedMessage = "Email не может быть пустым";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserEmailNotContainsSymbolAtTest() {
        userController.createUser(user1);
        user2.setEmail("User1yandex.ru");
        user2.setId(user1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> userController.updateUser(user2));
        String expectedMessage = "В email отсутствует символ '@'";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserLoginAlreadyExistsTest() {
        User user3 = new User();
        LocalDate birthdayUser3 = LocalDate.of(2007, 6, 4);
        user3.setName("UserName3");
        user3.setEmail("User3@yandex.ru");
        user3.setLogin("UserLogin3");
        user3.setBirthday(birthdayUser3);
        userController.createUser(user1);
        userController.createUser(user3);
        user2.setLogin(user3.getLogin());
        user2.setId(user1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> userController.updateUser(user2));
        String expectedMessage = "Пользователь с таким логином уже существует";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserLoginIsEmptyTest() {
        userController.createUser(user1);
        user2.setLogin("       ");
        user2.setId(user1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> userController.updateUser(user2));
        String expectedMessage = "Логин не может быть пустым и содержать пробелы";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserNameIsEmptyTest() {
        userController.createUser(user1);
        user2.setName(null);
        user2.setId(user1.getId());

        userController.updateUser(user2);

        assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    void updateUserWrongBirthdayTest() {
        LocalDate birthdayUser3 = LocalDate.now().plusDays(1);
        userController.createUser(user1);
        user2.setBirthday(birthdayUser3);
        user2.setId(user1.getId());

        Exception exception = assertThrows(ValidationException.class, () -> userController.updateUser(user2));
        String expectedMessage = "Дата рождения не может быть в будущем";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}