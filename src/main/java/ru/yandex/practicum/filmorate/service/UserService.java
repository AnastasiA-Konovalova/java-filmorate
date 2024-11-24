package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("Нельзя добавлять в друзья самого себя");
        }
        User user = getUserById(id);
        User userFriend = getUserById(friendId);
        user.getFriends().add(friendId);
        userFriend.getFriends().add(id);
        log.info("Друг добавлен");
        return user;
    }

    public User deleteFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User userFriend = getUserById(friendId);
        user.getFriends().remove(friendId);
        userFriend.getFriends().remove(id);
        log.info("Друг удален");
        return user;
    }

    public List<User> getListCommonFriends(Long first, Long second) {
        User userFirst = getUserById(first);
        User userSecond = getUserById(second);
        Set<Long> commonList = new HashSet<>(userFirst.getFriends());
        commonList.retainAll(userSecond.getFriends());
        log.info("Запрошен список общих друзей");
        List<User> result = new ArrayList<>();
        result.addAll(commonList.stream()
                .map(this::getUserById)
                .toList());
        return result;
    }

    public List<User> getListFriends(Long id) {
        User user = getUserById(id);
        log.info("Запрошен список друзей пользователя");
        List<User> result = new ArrayList<>();

        result.addAll(user.getFriends().stream()
                .map(this::getUserById)
                .toList());
        return result;
    }

    private User getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Неправильно введен id пользователя");
            throw new NotFoundException("User с таким id отсутствует");
        }
        return user;
    }
}