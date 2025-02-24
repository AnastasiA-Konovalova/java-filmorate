package ru.yandex.practicum.filmorate.storage.Friend;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.mapper.FriendRowMapper;

import java.util.List;

@Repository
@Qualifier("friendDbStorage")
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_FRIEND_BY_ID = """
            SELECT id, user_id, friend_id, status
            FROM friends
            WHERE user_id = ? AND friend_id = ?
            """;
    private static final String FIND_LIST_FRIENDS = """
            SELECT id, user_id, friend_id, status
            FROM friends
            WHERE user_id = ?
            """;

    public Friend getFriend(Long userId, Long friendId) {
        List<Friend> friends = jdbcTemplate.query(FIND_FRIEND_BY_ID, new FriendRowMapper(), userId, friendId);
        return friends.isEmpty() ? null : friends.getFirst();
    }

    public List<Friend> getListFriends(Long id) {
        return jdbcTemplate.query(FIND_LIST_FRIENDS, new FriendRowMapper(), id);
    }
}