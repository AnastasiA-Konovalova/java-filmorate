package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    public Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        boolean userAlreadyAdded = users.values().stream()
                .anyMatch(userCheck -> userCheck.getLogin().equalsIgnoreCase(user.getLogin()));
        if (userAlreadyAdded) {
            log.warn("Ошибка. Попытка добавить логин, который уже существует");
            throw new ValidationException("Пользователь с таким логином уже существует");
        }
        checkUserConditions(user);

        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    private long generateId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Ошибка. Не указан id для изменения user");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.get(newUser.getId()) == null) {
            log.warn("Ошибка. Такого пользователя нет в списке");
            throw new ValidationException("Пользователь не найден");
        }
        boolean userAlreadyAdded = users.values().stream()
                .anyMatch(userCheck -> userCheck.getLogin().equalsIgnoreCase(newUser.getLogin()));
        if (userAlreadyAdded) {
            log.warn("Ошибка. Пользователь пытается добавить логин, который уже используется");
            throw new ValidationException("Пользователь с таким логином уже существует");
        }
        checkUserConditions(newUser);

        User oldUser = users.get(newUser.getId());
        oldUser.setName(newUser.getName());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());

        return newUser;
    }

    public void checkUserConditions(User user) {
        if (user == null) {
            log.warn("Ошибка. Получено пустое тело запроса");
            throw new ValidationException("Тело запроса user null");
        }

        Optional<String> emailUser = Optional.ofNullable(user.getEmail());
        emailUser.filter(email -> !email.trim().isEmpty())
                .orElseThrow(() -> {
                    log.warn("Ошибка. Пользователь указал пустой email");
                    return new ValidationException("Email не может быть пустым");
                });

        if (!user.getEmail().contains("@")) {
            log.warn("Ошибка. В введенном email отсутствует @");
            throw new ValidationException("В email отсутствует символ '@'");
        }

        Optional<String> login = Optional.ofNullable(user.getLogin());
        login.filter(loginName -> (!loginName.trim().isEmpty()) && !loginName.contains(" "))
                .orElseThrow(() -> {
                    log.warn("Ошибка. Пользователь неверно добавил логин");
                    return new ValidationException("Логин не может быть пустым и содержать пробелы");
                });

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя user пустое, поэтому изменено на логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка. Пользователь указал дату рождения в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}