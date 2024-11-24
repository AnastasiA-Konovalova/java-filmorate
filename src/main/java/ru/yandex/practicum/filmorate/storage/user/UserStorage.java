package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
public interface UserStorage {

    Collection<User> getUsers();

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(User newUser);

    void deleteUsers();
}