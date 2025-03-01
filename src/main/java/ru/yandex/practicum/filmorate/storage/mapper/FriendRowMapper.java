package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.FriendStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendRowMapper implements RowMapper<Friend> {

    private static final String FRIEND_ID = "friend_id";
    private static final String STATUS = "status";

    @Override
    public Friend mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friend friend = new Friend();
        friend.setId(rs.getLong(FRIEND_ID));
        friend.setStatus(FriendStatus.valueOf(rs.getString(STATUS)));

        return friend;
    }
}
