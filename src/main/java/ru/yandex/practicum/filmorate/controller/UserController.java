package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        users.values().stream()
                .filter(userCheck -> userCheck.getLogin().equalsIgnoreCase(user.getLogin()))
                .findFirst().ifPresent(duplicate -> {
                    log.warn("Ошибка. Попытка добавить логин, который уже существует");
                    throw new ValidationException("Пользователь с таким логином уже существует");
                });
        log.info("Добавление пользователя в список");
        renameIfNameEmpty(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен");
        return user;
    }

    private long generateId() {
        return users.keySet().stream().max(Long::compareTo).map(i -> i + 1).orElse(1L);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Обновление характеристик пользователя");
        if (newUser.getId() == null) {
            log.warn("Ошибка. Не указан id для изменения user");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.get(newUser.getId()) == null) {
            log.warn("Ошибка. Такого пользователя нет в списке");
            throw new ResponseStatusException(NOT_FOUND, "Пользователь с таким id не найден");
        }
        users.values().stream()
                .filter(userCheck -> userCheck.getLogin().equalsIgnoreCase(newUser.getLogin()))
                .findFirst().ifPresent(duplicate -> {
                    log.warn("Ошибка. Пользователь пытается добавить логин, который уже используется");
                    throw new ValidationException("Пользователь с таким логином уже существует");
                });
        renameIfNameEmpty(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Пользователь обновлен");
        return newUser;
    }

    public void renameIfNameEmpty(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя user пустое, поэтому изменено на логин");
        }
    }

    public void deleteUsers() {
        users.clear();
    }
}