package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mapping.UserMapperToDto;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
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
        User user = getUserById(id);
        User userFriend = getUserById(friendId);
        user.addFriend(friendId);
        userFriend.addFriend(id);
        log.info("Друг добавлен");
        return userMapperToDto.toDto(user);
    }

    public UserDto deleteFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User userFriend = getUserById(friendId);
        user.deleteFriend(friendId);
        userFriend.deleteFriend(id);
        log.info("Друг удален");
        return userMapperToDto.toDto(user);
    }

    public List<UserDto> getListCommonFriends(Long first, Long second) {
        log.info("Запрошен список общих друзей");
        User userFirst = getUserById(first);
        User userSecond = getUserById(second);
        Set<Long> commonFriends = new HashSet<>(userFirst.getFriends());
        commonFriends.retainAll(userSecond.getFriends());
        List<User> commonFriendUser = userStorage.getUserByIds(commonFriends);
        return commonFriendUser.stream()
                .map(userMapperToDto::toDto)
                .toList();
    }

    public List<UserDto> getListFriends(Long id) {
        User user = getUserById(id);
        log.info("Запрошен список друзей пользователя");
        List<User> users = userStorage.getUserByIds(user.getFriends());
        //return userStorage.getUserByIds(user.getFriends());
        return users.stream()
                .map(userFriend -> userMapperToDto.toDto(userFriend))
                .toList();
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