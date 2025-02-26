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
import ru.yandex.practicum.filmorate.storage.Friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FriendService friendService;
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

        Friend friend = new Friend();
        friend.setId(friendId);

        user.deleteFriend(friend);
        userStorage.deleteFriend(id, friendId);

        return userMapperToDto.toDto(user);
    }

    public List<FriendDto> getListCommonFriends(Long firstId, Long secondId) {
        Optional<User> userFirstOpt = userStorage.getUserById(firstId);
        Optional<User> userSecondOpt = userStorage.getUserById(secondId);

        User userFirst = userFirstOpt.orElseThrow(() -> new NotFoundException("Пользователь с id " + firstId + " не найден."));
        User userSecond = userSecondOpt.orElseThrow(() -> new NotFoundException("Пользователь с id " + secondId + " не найден."));

        Set<Friend> commonFriends = new HashSet<>(userFirst.getFriends());
        commonFriends.retainAll(userSecond.getFriends());

        return commonFriends.stream()
                .map(FriendMapperToDto::toDto)
                .toList();
    }

    public List<FriendDto> getListFriends(Long id) {
        Optional<User> userOpt = userStorage.getUserById(id);

        User user = userOpt.orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));

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
        Optional<User> user = userStorage.getUserById(newUser.getId());

        user.orElseThrow(() -> new NotFoundException("Пользователь с id "
                + newUser.getId() + " не найден."));

        return userMapperToDto.toDto(userStorage.updateUser(newUser));
    }

    public void deleteUsers() {
        userStorage.deleteUsers();
    }

    public UserDto getUserById(Long userId) {
        Optional<User> userById = userStorage.getUserById(userId);

        User user = userById.orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));

        return userMapperToDto.toDto(user);
    }

    private User getUser(Long id) {
        Optional<User> userById = userStorage.getUserById(id);

        User user = userById.orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));

        return user;
    }
}