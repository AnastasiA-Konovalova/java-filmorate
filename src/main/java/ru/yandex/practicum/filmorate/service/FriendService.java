package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.Friend.FriendStorage;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendStorage friendStorage;

    public Friend getFriend(Long id, Long friendId) {
        if (Objects.equals(friendId, id)) {
            throw new ValidationException("Id друга " + friendId + " не должно совпадать с id " +
                    "пользователя " + id);
        }

        Optional<Friend> friend = friendStorage.getFriend(id, friendId);
        return friend.orElseThrow(() ->
                new NotFoundException("Друг с id " + friendId + " не найден для пользователя с id " + id));
    }
}