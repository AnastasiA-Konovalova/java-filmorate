package ru.yandex.practicum.filmorate.storage.Friend;

import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;

public interface FriendStorage {

    Friend getFriend(Long id, Long friendId);

    List<Friend> getListFriends(Long id);
}