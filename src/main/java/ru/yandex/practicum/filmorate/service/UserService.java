package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FriendDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mapping.FriendMapperToDto;
import ru.yandex.practicum.filmorate.service.mapping.UserMapperToDto;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserMapperToDto userMapperToDto;

    public UserDto addFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("Нельзя добавлять в друзья самого себя");
        }

        User user = getUser(id);
        getUser(friendId);

        boolean friendshipExists = userStorage.isFriendshipExists(friendId, id);
        Friend friend;

        if (friendshipExists) {
            userStorage.updateFriendStatus(friendId, id, FriendStatus.CONFIRMED);
            friend = userStorage.addFriend(id, friendId, FriendStatus.CONFIRMED);
        } else {
            friend = userStorage.addFriend(id, friendId, FriendStatus.UNCONFIRMED);
        }

        user.addFriend(friend);
        return userMapperToDto.toDto(user);
    }


    public UserDto deleteFriend(Long id, Long friendId) {
        User user = getUser(id);
        getUser(friendId);

        Friend friend = getFriend(friendId, id);

        user.deleteFriend(friend);
        userStorage.deleteFriend(id, friendId);

        return userMapperToDto.toDto(user);
    }

    public List<FriendDto> getListCommonFriends(Long firstId, Long secondId) {
        User userFirst = userStorage.getUserById(firstId);
        User userSecond = userStorage.getUserById(secondId);

        Set<Friend> commonFriends = new HashSet<>(userFirst.getFriends());
        commonFriends.retainAll(userSecond.getFriends());

        return commonFriends.stream()
                .map(FriendMapperToDto::toDto)
                .toList();
    }

    public Friend getFriend(Long friendId, Long id) {
        if (Objects.equals(friendId, id)) {
            throw new ValidationException("Id друга " + friendId + " не должно совпадать с id " +
                    "пользователя " + id);
        }

        return userStorage.getFriend(friendId, id);
    }

    public List<FriendDto> getListFriends(Long id) {
        User user = userStorage.getUserById(id);

        log.info("user " + user);
        log.info("Запрошен список друзей пользователя " + user.getFriends());

        return user.getFriends().stream()
                .map(FriendMapperToDto::toDto)
                .toList();
    }

    public Collection<UserDto> getListUsers() {
        Collection<User> users = userStorage.getUsers();
        if (users.isEmpty()) {
            log.warn("Список users пуст");
            throw new NotFoundException("Список users пуст");
        }
        return users.stream()
                .map(userMapperToDto::toDto)
                .toList();
    }

    public UserDto createUser(User user) {
        if (user == null) {
            log.warn("User пуст");
            throw new NotFoundException("User пуст");
        }

        User newUser = userStorage.createUser(user);
        return userMapperToDto.toDto(newUser);
    }

    public UserDto updateUser(User newUser) {
        User userById = userStorage.getUserById(newUser.getId());
        if (userById == null) {
            log.warn("User с таким id не существует.");
            throw new NotFoundException("User с таким id не существует. Обновление невозможно");
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

    private User getUser(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn("Неправильно введен id пользователя");
            throw new NotFoundException("User с таким id отсутствует");
        }
        return user;
    }
}