package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mapping.GenreMapperToDto;
import ru.yandex.practicum.filmorate.service.mapping.UserMapperToDto;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserMapperToDto userMapperToDto;
    private final GenreDbStorage genreDbStorage;
    private final GenreMapperToDto genreMapperToDto;

    public UserDto addFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("Нельзя добавлять в друзья самого себя");
        }
        User user = getUser(id);
        User userFriend = getUser(friendId);
        user.addFriend(friendId);
        userFriend.addFriend(id);
        log.info("Друг добавлен");
        return userMapperToDto.toDto(user);
    }

    public UserDto deleteFriend(Long id, Long friendId) {
        User user = getUser(id);
        User userFriend = getUser(friendId);
        user.deleteFriend(friendId);
        userFriend.deleteFriend(id);
        log.info("Друг удален");
        return userMapperToDto.toDto(user);
    }

    public List<UserDto> getListCommonFriends(Long first, Long second) {
        log.info("Запрошен список общих друзей");
        User userFirst = getUser(first);
        User userSecond = getUser(second);
        Set<Long> commonFriends = new HashSet<>(userFirst.getFriends());
        commonFriends.retainAll(userSecond.getFriends());
        List<User> commonFriendUser = userStorage.getUserByIds(commonFriends);
        return commonFriendUser.stream()
                .map(userMapperToDto::toDto)
                .toList();
    }

    public List<UserDto> getListFriends(Long id) {
        User user = getUser(id);
        log.info("Запрошен список друзей пользователя");
        List<User> users = userStorage.getUserByIds(user.getFriends());
        //return userStorage.getUserByIds(user.getFriends());
        return users.stream()
                .map(userFriend -> userMapperToDto.toDto(userFriend))
                .toList();
    }

    public Collection<UserDto> getListUsers() {
        List<User> users = (List<User>) userStorage.getUsers();
        if (users.isEmpty()) {
            log.warn("Список users пуст");
            throw new NotFoundException("Список users пуст");
        }
        return users.stream()
                .map(user -> userMapperToDto.toDto(user))
                .toList();
    }

    public UserDto createUser(User user) {
        if (user == null) {
            log.warn("User пуст");
            throw new NotFoundException("User пуст");
        }
        return userMapperToDto.toDto(userStorage.createUser(user));
    }

    public UserDto updateUser(User newUser) {
        if (newUser == null) {
            log.warn("User пуст");
            throw new NotFoundException("User пуст");
        }
        return userMapperToDto.toDto(userStorage.updateUser(newUser));
    }

    public void deleteUsers() {
        userStorage.deleteUsers();
    }

    public UserDto getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Неправильно введен id пользователя");
            throw new NotFoundException("User с таким id отсутствует");
        }
        return userMapperToDto.toDto(user);
    }

    private User getUser(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Неправильно введен id пользователя");
            throw new NotFoundException("User с таким id отсутствует");
        }
        return user;
    }
}