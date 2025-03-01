package ru.yandex.practicum.filmorate.storage.Friend;

import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;
import java.util.Optional;

public interface FriendStorage {

    Optional<Friend> getFriend(Long id, Long friendId);

    List<Friend> getListFriends(Long id);
}