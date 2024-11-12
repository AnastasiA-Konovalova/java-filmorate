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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;
    private User user1;
    private User user2;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userController.deleteUsers();

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
    void emptyEmailConstraintViolationUserTest() {
        user1.setEmail(null);

        Set<ConstraintViolation<User>> violationSet = validator.validate(user1);
        assertFalse(violationSet.isEmpty(), "Ожидались нарушения валидации для пустого email");

        assertEquals(1, violationSet.size());
        assertEquals(violationSet.stream().findFirst().get().getMessageTemplate(), "Email не может быть пустым");
    }

    @Test
    void emailNotContainsSymbolAtUserTest() {
        user1.setEmail("User1yandex.ru");

        Set<ConstraintViolation<User>> violationSet = validator.validate(user1);
        assertFalse(violationSet.isEmpty(), "Ожидались нарушения валидации для email без @");

        assertEquals(1, violationSet.size());
        assertEquals(violationSet.stream().findFirst().get().getMessageTemplate(), "Неверный формат email");
    }

    @Test
    void loginIsEmptyConstraintViolationUserTest() {
        user1.setLogin(null);

        Set<ConstraintViolation<User>> violationSet = validator.validate(user1);
        assertFalse(violationSet.isEmpty(), "Ожидались нарушения валидации для пустого логина");
        assertEquals(1, violationSet.size());
        assertEquals(violationSet.stream().findFirst().get().getMessageTemplate(), "Логин не может быть пустым");
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
    void nameUserIsEmptyConstraintViolationTest() {
        user1.setName(null);

        userController.createUser(user1);

        assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    void wrongBirthdayUserConstraintViolationUserTest() {
        LocalDate birthdayUser3 = LocalDate.now().plusDays(1);
        user1.setBirthday(birthdayUser3);

        Set<ConstraintViolation<User>> violationSet = validator.validate(user1);
        assertFalse(violationSet.isEmpty(), "Ожидались нарушения валидации по дате рождения");
        assertEquals(1, violationSet.size());
        assertEquals(violationSet.stream().findFirst().get().getMessageTemplate(), "День рождения не может быть в будущем");
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

        Exception exception = assertThrows(ResponseStatusException.class, () -> userController.updateUser(user1));
        String expectedMessage = "404 NOT_FOUND \"Пользователь с таким id не найден\"";
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
        user2.setLogin("   u    ");
        user2.setId(user1.getId());

        Set<ConstraintViolation<User>> violationSet = validator.validate(user2);
        assertFalse(violationSet.isEmpty(), "Ожидались нарушения валидации логина в виду наличия пробелов");
        assertEquals(1, violationSet.size());
        assertEquals(violationSet.stream().findFirst().get().getMessageTemplate(),
                "Логин не может содержать пробелы");
    }

    @Test
    void updateIfNameEmptyTest() {
        userController.createUser(user1);
        user2.setName(null);
        user2.setId(user1.getId());

        User user = userController.updateUser(user2);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void createUserSuccess() {
        userController.createUser(user1);
        userController.createUser(user2);

        Collection<User> users = userController.getUsers();

        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        assertEquals(2, users.size());
    }

    @Test
    void updateUserSuccessTest() {
        userController.createUser(user1);
        user2.setId(user1.getId());

        userController.updateUser(user2);

        Collection<User> users = userController.getUsers();
        boolean userExists = users.stream().anyMatch(user -> user.getLogin().equals("UserLogin2"));
        assertEquals(1, users.size());
        assertTrue(userExists);
    }
}