package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
//@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        log.info("Запрошен список пользователей");
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Ошибка. Пользователя с таким id нет");
            throw new NotFoundException("Пользователя с таким id нет");
        }
        return users.get(id);
    }

    @Override
    public List<User> getUserByIds(Set<Long> ids) {
        List<User> userList = new ArrayList<>();
        userList.addAll(ids.stream()
                .filter(users::containsKey)
                .map(users::get)
                .toList());
        return userList;
    }

    @Override
    public User createUser(User user) {
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

    @Override
    public User updateUser(User newUser) {
        log.info("Обновление характеристик пользователя");
        if (newUser.getId() == null) {
            log.warn("Ошибка. Не указан id для изменения user");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.get(newUser.getId()) == null) {
            log.warn("Ошибка. Такого пользователя нет в списке");
            throw new NotFoundException("Пользователь с таким id не найден");
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

    @Override
    public void deleteUsers() {
        log.info("Все пользователи удалены");
        users.clear();
    }

    @Override
    public void addFriend(Long id, Long friendId) {

    }

    @Override
    public void deleteFriend(Long id, Long friendId) {

    }

    private long generateId() {
        return users.keySet().stream().max(Long::compareTo).map(i -> i + 1).orElse(1L);
    }

    private void renameIfNameEmpty(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя user пустое, поэтому изменено на логин");
        }
    }
}