package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserStorage {

    Collection<User> getUsers();

    User getUserById(Long id);

    List<User> getUserByIds(Set<Long> ids);

    User createUser(User user);

    User updateUser(User newUser);

    void deleteUsers();

    void addFriend(Long id, Long friendId, FriendStatus friendStatus);

    Friend getFriend(Long friendId, Long id);

    void deleteFriend(Long id, Long friendId);

    void updateFriendStatus(Long id, Long friendId, FriendStatus newStatus);

    boolean isFriendshipExists(Long id, Long friendId);
}